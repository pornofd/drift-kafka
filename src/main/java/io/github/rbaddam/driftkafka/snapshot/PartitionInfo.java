package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PartitionInfo {
    @JsonProperty("partition")
    private int partition;

    @JsonProperty("leader")
    private int leader;

    @JsonProperty("replicas")
    private List<Integer> replicas;

    @JsonProperty("isr")
    private List<Integer> isr;

    public PartitionInfo() {
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int leader) {
        this.leader = leader;
    }

    public List<Integer> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Integer> replicas) {
        this.replicas = replicas;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    public void setIsr(List<Integer> isr) {
        this.isr = isr;
    }
}
