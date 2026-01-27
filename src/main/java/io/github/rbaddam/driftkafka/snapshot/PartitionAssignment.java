package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartitionAssignment {
    @JsonProperty("topic")
    private String topic;

    @JsonProperty("partition")
    private int partition;

    public PartitionAssignment() {
    }

    public PartitionAssignment(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }
}
