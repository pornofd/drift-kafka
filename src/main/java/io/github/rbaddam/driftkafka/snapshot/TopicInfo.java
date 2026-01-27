package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class TopicInfo {
    @JsonProperty("name")
    private String name;

    @JsonProperty("partitionCount")
    private int partitionCount;

    @JsonProperty("replicationFactor")
    private int replicationFactor;

    @JsonProperty("partitions")
    private List<PartitionInfo> partitions;

    @JsonProperty("config")
    private Map<String, String> config;

    public TopicInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public List<PartitionInfo> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<PartitionInfo> partitions) {
        this.partitions = partitions;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
