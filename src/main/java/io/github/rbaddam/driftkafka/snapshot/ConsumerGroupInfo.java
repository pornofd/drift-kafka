package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ConsumerGroupInfo {
    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("state")
    private String state;

    @JsonProperty("memberCount")
    private int memberCount;

    @JsonProperty("assignments")
    private List<PartitionAssignment> assignments;

    @JsonProperty("offsets")
    private List<OffsetInfo> offsets;

    public ConsumerGroupInfo() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<PartitionAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<PartitionAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<OffsetInfo> getOffsets() {
        return offsets;
    }

    public void setOffsets(List<OffsetInfo> offsets) {
        this.offsets = offsets;
    }
}
