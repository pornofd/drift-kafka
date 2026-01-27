package io.github.rbaddam.driftkafka.kafka;

import io.github.rbaddam.driftkafka.snapshot.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KafkaClient {
    // Allowlist of topic configs to capture
    private static final Set<String> ALLOWED_CONFIGS = Set.of(
            "retention.ms",
            "retention.bytes",
            "cleanup.policy",
            "min.insync.replicas",
            "segment.ms",
            "segment.bytes",
            "max.message.bytes",
            "compression.type"
    );

    private final AdminClient adminClient;

    public KafkaClient(Properties config) {
        this.adminClient = AdminClient.create(config);
    }

    public ClusterInfo getClusterInfo() throws ExecutionException, InterruptedException {
        ClusterInfo clusterInfo = new ClusterInfo();

        DescribeClusterResult clusterResult = adminClient.describeCluster();
        Collection<Node> nodes = clusterResult.nodes().get();

        List<BrokerInfo> brokers = nodes.stream()
                .map(node -> new BrokerInfo(node.id(), node.rack()))
                .sorted(Comparator.comparingInt(BrokerInfo::getId))
                .collect(Collectors.toList());

        clusterInfo.setBrokers(brokers);

        // Try to get Kafka version from broker, otherwise set to "unknown"
        try {
            String version = clusterResult.clusterId().get();
            clusterInfo.setKafkaVersion("unknown");
        } catch (Exception e) {
            clusterInfo.setKafkaVersion("unknown");
        }

        return clusterInfo;
    }

    public List<TopicInfo> getTopicInfo() throws ExecutionException, InterruptedException {
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> topicNames = topicsResult.names().get();

        DescribeTopicsResult describeResult = adminClient.describeTopics(topicNames);
        Map<String, TopicDescription> topicDescriptions = describeResult.allTopicNames().get();

        List<ConfigResource> configResources = topicNames.stream()
                .map(name -> new ConfigResource(ConfigResource.Type.TOPIC, name))
                .collect(Collectors.toList());

        DescribeConfigsResult configsResult = adminClient.describeConfigs(configResources);
        Map<ConfigResource, Config> configs = configsResult.all().get();

        List<TopicInfo> topics = new ArrayList<>();

        for (String topicName : topicNames) {
            TopicDescription desc = topicDescriptions.get(topicName);
            if (desc == null) continue;

            TopicInfo topicInfo = new TopicInfo();
            topicInfo.setName(topicName);
            topicInfo.setPartitionCount(desc.partitions().size());

            if (!desc.partitions().isEmpty()) {
                topicInfo.setReplicationFactor(desc.partitions().get(0).replicas().size());
            }

            List<PartitionInfo> partitions = desc.partitions().stream()
                    .map(pInfo -> {
                        PartitionInfo partition = new PartitionInfo();
                        partition.setPartition(pInfo.partition());
                        partition.setLeader(pInfo.leader() != null ? pInfo.leader().id() : -1);
                        partition.setReplicas(pInfo.replicas().stream()
                                .map(Node::id)
                                .collect(Collectors.toList()));
                        partition.setIsr(pInfo.isr().stream()
                                .map(Node::id)
                                .collect(Collectors.toList()));
                        return partition;
                    })
                    .sorted(Comparator.comparingInt(PartitionInfo::getPartition))
                    .collect(Collectors.toList());

            topicInfo.setPartitions(partitions);

            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            Config config = configs.get(resource);
            if (config != null) {
                Map<String, String> filteredConfig = config.entries().stream()
                        .filter(entry -> ALLOWED_CONFIGS.contains(entry.name()))
                        .filter(entry -> !entry.isDefault())
                        .collect(Collectors.toMap(
                                ConfigEntry::name,
                                ConfigEntry::value,
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        ));
                topicInfo.setConfig(filteredConfig);
            }

            topics.add(topicInfo);
        }

        topics.sort(Comparator.comparing(TopicInfo::getName));
        return topics;
    }

    public List<ConsumerGroupInfo> getConsumerGroupInfo(boolean includeOffsets, List<String> errors)
            throws ExecutionException, InterruptedException {
        ListConsumerGroupsResult groupsResult = adminClient.listConsumerGroups();
        Collection<ConsumerGroupListing> groupListings = groupsResult.all().get();

        List<String> groupIds = groupListings.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());

        if (groupIds.isEmpty()) {
            return Collections.emptyList();
        }

        DescribeConsumerGroupsResult describeResult = adminClient.describeConsumerGroups(groupIds);
        Map<String, ConsumerGroupDescription> descriptions = describeResult.all().get();

        List<ConsumerGroupInfo> groups = new ArrayList<>();

        for (String groupId : groupIds) {
            ConsumerGroupDescription desc = descriptions.get(groupId);
            if (desc == null) continue;

            ConsumerGroupInfo groupInfo = new ConsumerGroupInfo();
            groupInfo.setGroupId(groupId);
            groupInfo.setState(desc.state().toString());
            groupInfo.setMemberCount(desc.members().size());

            List<PartitionAssignment> assignments = desc.members().stream()
                    .flatMap(member -> member.assignment().topicPartitions().stream())
                    .map(tp -> new PartitionAssignment(tp.topic(), tp.partition()))
                    .sorted(Comparator.comparing(PartitionAssignment::getTopic)
                            .thenComparingInt(PartitionAssignment::getPartition))
                    .collect(Collectors.toList());

            groupInfo.setAssignments(assignments);

            if (includeOffsets) {
                try {
                    Map<TopicPartition, OffsetAndMetadata> offsets =
                            adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();

                    List<OffsetInfo> offsetInfos = offsets.entrySet().stream()
                            .map(entry -> new OffsetInfo(
                                    entry.getKey().topic(),
                                    entry.getKey().partition(),
                                    entry.getValue().offset()
                            ))
                            .sorted(Comparator.comparing(OffsetInfo::getTopic)
                                    .thenComparingInt(OffsetInfo::getPartition))
                            .collect(Collectors.toList());

                    groupInfo.setOffsets(offsetInfos);
                } catch (Exception e) {
                    errors.add(String.format("Failed to fetch offsets for group %s: %s",
                            groupId, e.getMessage()));
                }
            }

            groups.add(groupInfo);
        }

        groups.sort(Comparator.comparing(ConsumerGroupInfo::getGroupId));
        return groups;
    }

    public void close() {
        adminClient.close();
    }
}
