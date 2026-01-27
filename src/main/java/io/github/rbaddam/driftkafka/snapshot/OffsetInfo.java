package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OffsetInfo {
    @JsonProperty("topic")
    private String topic;

    @JsonProperty("partition")
    private int partition;

    @JsonProperty("offset")
    private long offset;

    public OffsetInfo() {
    }

    public OffsetInfo(String topic, int partition, long offset) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
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

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
