package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public class Snapshot {
    @JsonProperty("schemaVersion")
    private int schemaVersion = 1;

    @JsonProperty("toolVersion")
    private String toolVersion;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("cluster")
    private ClusterInfo cluster;

    @JsonProperty("topics")
    private List<TopicInfo> topics;

    @JsonProperty("consumerGroups")
    private List<ConsumerGroupInfo> consumerGroups;

    @JsonProperty("offsetsCaptured")
    private boolean offsetsCaptured;

    @JsonProperty("offsetErrors")
    private List<String> offsetErrors;

    public Snapshot() {
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ClusterInfo getCluster() {
        return cluster;
    }

    public void setCluster(ClusterInfo cluster) {
        this.cluster = cluster;
    }

    public List<TopicInfo> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicInfo> topics) {
        this.topics = topics;
    }

    public List<ConsumerGroupInfo> getConsumerGroups() {
        return consumerGroups;
    }

    public void setConsumerGroups(List<ConsumerGroupInfo> consumerGroups) {
        this.consumerGroups = consumerGroups;
    }

    public boolean isOffsetsCaptured() {
        return offsetsCaptured;
    }

    public void setOffsetsCaptured(boolean offsetsCaptured) {
        this.offsetsCaptured = offsetsCaptured;
    }

    public List<String> getOffsetErrors() {
        return offsetErrors;
    }

    public void setOffsetErrors(List<String> offsetErrors) {
        this.offsetErrors = offsetErrors;
    }
}
