package io.github.rbaddam.driftkafka.diff;

import io.github.rbaddam.driftkafka.snapshot.*;

import java.util.*;
import java.util.stream.Collectors;

public class DiffEngine {

    public SnapshotDiff diff(Snapshot before, Snapshot after) {
        SnapshotDiff diff = new SnapshotDiff();

        diff.setBeforeTimestamp(before.getTimestamp().toString());
        diff.setAfterTimestamp(after.getTimestamp().toString());

        diffTopics(before, after, diff);
        diffPartitions(before, after, diff);
        diffConsumerGroups(before, after, diff);

        if (before.isOffsetsCaptured() && after.isOffsetsCaptured()) {
            diffOffsets(before, after, diff);
        }

        return diff;
    }

    private void diffTopics(Snapshot before, Snapshot after, SnapshotDiff diff) {
        Set<String> beforeTopics = before.getTopics().stream()
                .map(TopicInfo::getName)
                .collect(Collectors.toSet());

        Set<String> afterTopics = after.getTopics().stream()
                .map(TopicInfo::getName)
                .collect(Collectors.toSet());

        List<String> added = afterTopics.stream()
                .filter(t -> !beforeTopics.contains(t))
                .sorted()
                .collect(Collectors.toList());

        List<String> removed = beforeTopics.stream()
                .filter(t -> !afterTopics.contains(t))
                .sorted()
                .collect(Collectors.toList());

        diff.setTopicsAdded(added.isEmpty() ? null : added);
        diff.setTopicsRemoved(removed.isEmpty() ? null : removed);

        Map<String, TopicInfo> beforeMap = before.getTopics().stream()
                .collect(Collectors.toMap(TopicInfo::getName, t -> t));

        Map<String, TopicInfo> afterMap = after.getTopics().stream()
                .collect(Collectors.toMap(TopicInfo::getName, t -> t));

        List<SnapshotDiff.TopicChange> changes = new ArrayList<>();

        for (String topic : beforeTopics) {
            if (!afterTopics.contains(topic)) continue;

            TopicInfo b = beforeMap.get(topic);
            TopicInfo a = afterMap.get(topic);

            if (b.getPartitionCount() != a.getPartitionCount()) {
                changes.add(new SnapshotDiff.TopicChange(topic, "partitionCount",
                        String.valueOf(b.getPartitionCount()),
                        String.valueOf(a.getPartitionCount())));
            }

            if (b.getReplicationFactor() != a.getReplicationFactor()) {
                changes.add(new SnapshotDiff.TopicChange(topic, "replicationFactor",
                        String.valueOf(b.getReplicationFactor()),
                        String.valueOf(a.getReplicationFactor())));
            }

            diffTopicConfigs(topic, b.getConfig(), a.getConfig(), changes);
        }

        diff.setTopicChanges(changes.isEmpty() ? null : changes);
    }

    private void diffTopicConfigs(String topic, Map<String, String> before, Map<String, String> after,
                                   List<SnapshotDiff.TopicChange> changes) {
        Set<String> allKeys = new HashSet<>();
        if (before != null) allKeys.addAll(before.keySet());
        if (after != null) allKeys.addAll(after.keySet());

        for (String key : allKeys.stream().sorted().collect(Collectors.toList())) {
            String beforeVal = before != null ? before.get(key) : null;
            String afterVal = after != null ? after.get(key) : null;

            if (!Objects.equals(beforeVal, afterVal)) {
                changes.add(new SnapshotDiff.TopicChange(topic, "config." + key,
                        beforeVal != null ? beforeVal : "null",
                        afterVal != null ? afterVal : "null"));
            }
        }
    }

    private void diffPartitions(Snapshot before, Snapshot after, SnapshotDiff diff) {
        Map<String, TopicInfo> beforeMap = before.getTopics().stream()
                .collect(Collectors.toMap(TopicInfo::getName, t -> t));

        Map<String, TopicInfo> afterMap = after.getTopics().stream()
                .collect(Collectors.toMap(TopicInfo::getName, t -> t));

        List<SnapshotDiff.PartitionChange> changes = new ArrayList<>();

        for (String topic : beforeMap.keySet()) {
            if (!afterMap.containsKey(topic)) continue;

            TopicInfo beforeTopic = beforeMap.get(topic);
            TopicInfo afterTopic = afterMap.get(topic);

            Map<Integer, PartitionInfo> beforePartitions = beforeTopic.getPartitions().stream()
                    .collect(Collectors.toMap(PartitionInfo::getPartition, p -> p));

            Map<Integer, PartitionInfo> afterPartitions = afterTopic.getPartitions().stream()
                    .collect(Collectors.toMap(PartitionInfo::getPartition, p -> p));

            for (Integer partition : beforePartitions.keySet()) {
                if (!afterPartitions.containsKey(partition)) continue;

                PartitionInfo b = beforePartitions.get(partition);
                PartitionInfo a = afterPartitions.get(partition);

                if (b.getLeader() != a.getLeader()) {
                    changes.add(new SnapshotDiff.PartitionChange(topic, partition, "leader",
                            String.valueOf(b.getLeader()),
                            String.valueOf(a.getLeader())));
                }

                if (!b.getIsr().equals(a.getIsr())) {
                    changes.add(new SnapshotDiff.PartitionChange(topic, partition, "isr",
                            b.getIsr().toString(),
                            a.getIsr().toString()));
                }

                if (!b.getReplicas().equals(a.getReplicas())) {
                    changes.add(new SnapshotDiff.PartitionChange(topic, partition, "replicas",
                            b.getReplicas().toString(),
                            a.getReplicas().toString()));
                }
            }
        }

        diff.setPartitionChanges(changes.isEmpty() ? null : changes);
    }

    private void diffConsumerGroups(Snapshot before, Snapshot after, SnapshotDiff diff) {
        Map<String, ConsumerGroupInfo> beforeMap = before.getConsumerGroups().stream()
                .collect(Collectors.toMap(ConsumerGroupInfo::getGroupId, g -> g));

        Map<String, ConsumerGroupInfo> afterMap = after.getConsumerGroups().stream()
                .collect(Collectors.toMap(ConsumerGroupInfo::getGroupId, g -> g));

        List<SnapshotDiff.ConsumerGroupChange> changes = new ArrayList<>();

        Set<String> allGroups = new HashSet<>();
        allGroups.addAll(beforeMap.keySet());
        allGroups.addAll(afterMap.keySet());

        for (String groupId : allGroups.stream().sorted().collect(Collectors.toList())) {
            ConsumerGroupInfo b = beforeMap.get(groupId);
            ConsumerGroupInfo a = afterMap.get(groupId);

            if (b == null) {
                changes.add(new SnapshotDiff.ConsumerGroupChange(groupId, "added",
                        String.format("state=%s, members=%d", a.getState(), a.getMemberCount())));
            } else if (a == null) {
                changes.add(new SnapshotDiff.ConsumerGroupChange(groupId, "removed",
                        String.format("state=%s, members=%d", b.getState(), b.getMemberCount())));
            } else {
                if (!b.getState().equals(a.getState())) {
                    changes.add(new SnapshotDiff.ConsumerGroupChange(groupId, "state",
                            String.format("%s -> %s", b.getState(), a.getState())));
                }

                if (b.getMemberCount() != a.getMemberCount()) {
                    changes.add(new SnapshotDiff.ConsumerGroupChange(groupId, "memberCount",
                            String.format("%d -> %d", b.getMemberCount(), a.getMemberCount())));
                }

                Set<String> beforeAssignments = formatAssignments(b.getAssignments());
                Set<String> afterAssignments = formatAssignments(a.getAssignments());

                if (!beforeAssignments.equals(afterAssignments)) {
                    changes.add(new SnapshotDiff.ConsumerGroupChange(groupId, "assignments",
                            "partition assignments changed"));
                }
            }
        }

        diff.setConsumerGroupChanges(changes.isEmpty() ? null : changes);
    }

    private Set<String> formatAssignments(List<PartitionAssignment> assignments) {
        return assignments.stream()
                .map(a -> a.getTopic() + "-" + a.getPartition())
                .collect(Collectors.toSet());
    }

    private void diffOffsets(Snapshot before, Snapshot after, SnapshotDiff diff) {
        Map<String, ConsumerGroupInfo> beforeMap = before.getConsumerGroups().stream()
                .collect(Collectors.toMap(ConsumerGroupInfo::getGroupId, g -> g));

        Map<String, ConsumerGroupInfo> afterMap = after.getConsumerGroups().stream()
                .collect(Collectors.toMap(ConsumerGroupInfo::getGroupId, g -> g));

        List<SnapshotDiff.OffsetChange> changes = new ArrayList<>();

        for (String groupId : beforeMap.keySet()) {
            if (!afterMap.containsKey(groupId)) continue;

            ConsumerGroupInfo beforeGroup = beforeMap.get(groupId);
            ConsumerGroupInfo afterGroup = afterMap.get(groupId);

            if (beforeGroup.getOffsets() == null || afterGroup.getOffsets() == null) continue;

            Map<String, OffsetInfo> beforeOffsets = beforeGroup.getOffsets().stream()
                    .collect(Collectors.toMap(
                            o -> o.getTopic() + "-" + o.getPartition(),
                            o -> o
                    ));

            Map<String, OffsetInfo> afterOffsets = afterGroup.getOffsets().stream()
                    .collect(Collectors.toMap(
                            o -> o.getTopic() + "-" + o.getPartition(),
                            o -> o
                    ));

            for (String key : beforeOffsets.keySet()) {
                if (!afterOffsets.containsKey(key)) continue;

                OffsetInfo b = beforeOffsets.get(key);
                OffsetInfo a = afterOffsets.get(key);

                if (b.getOffset() != a.getOffset()) {
                    changes.add(new SnapshotDiff.OffsetChange(
                            groupId, b.getTopic(), b.getPartition(),
                            b.getOffset(), a.getOffset()
                    ));
                }
            }
        }

        diff.setOffsetChanges(changes.isEmpty() ? null : changes);
    }
}
