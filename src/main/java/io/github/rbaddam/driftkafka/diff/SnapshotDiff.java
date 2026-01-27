package io.github.rbaddam.driftkafka.diff;

import java.util.List;
import java.util.Map;

public class SnapshotDiff {
    private String beforeTimestamp;
    private String afterTimestamp;
    private List<String> topicsAdded;
    private List<String> topicsRemoved;
    private List<TopicChange> topicChanges;
    private List<PartitionChange> partitionChanges;
    private List<ConsumerGroupChange> consumerGroupChanges;
    private List<OffsetChange> offsetChanges;

    public SnapshotDiff() {
    }

    public String getBeforeTimestamp() {
        return beforeTimestamp;
    }

    public void setBeforeTimestamp(String beforeTimestamp) {
        this.beforeTimestamp = beforeTimestamp;
    }

    public String getAfterTimestamp() {
        return afterTimestamp;
    }

    public void setAfterTimestamp(String afterTimestamp) {
        this.afterTimestamp = afterTimestamp;
    }

    public List<String> getTopicsAdded() {
        return topicsAdded;
    }

    public void setTopicsAdded(List<String> topicsAdded) {
        this.topicsAdded = topicsAdded;
    }

    public List<String> getTopicsRemoved() {
        return topicsRemoved;
    }

    public void setTopicsRemoved(List<String> topicsRemoved) {
        this.topicsRemoved = topicsRemoved;
    }

    public List<TopicChange> getTopicChanges() {
        return topicChanges;
    }

    public void setTopicChanges(List<TopicChange> topicChanges) {
        this.topicChanges = topicChanges;
    }

    public List<PartitionChange> getPartitionChanges() {
        return partitionChanges;
    }

    public void setPartitionChanges(List<PartitionChange> partitionChanges) {
        this.partitionChanges = partitionChanges;
    }

    public List<ConsumerGroupChange> getConsumerGroupChanges() {
        return consumerGroupChanges;
    }

    public void setConsumerGroupChanges(List<ConsumerGroupChange> consumerGroupChanges) {
        this.consumerGroupChanges = consumerGroupChanges;
    }

    public List<OffsetChange> getOffsetChanges() {
        return offsetChanges;
    }

    public void setOffsetChanges(List<OffsetChange> offsetChanges) {
        this.offsetChanges = offsetChanges;
    }

    public static class TopicChange {
        private String topic;
        private String field;
        private String before;
        private String after;

        public TopicChange(String topic, String field, String before, String after) {
            this.topic = topic;
            this.field = field;
            this.before = before;
            this.after = after;
        }

        public String getTopic() {
            return topic;
        }

        public String getField() {
            return field;
        }

        public String getBefore() {
            return before;
        }

        public String getAfter() {
            return after;
        }
    }

    public static class PartitionChange {
        private String topic;
        private int partition;
        private String field;
        private String before;
        private String after;

        public PartitionChange(String topic, int partition, String field, String before, String after) {
            this.topic = topic;
            this.partition = partition;
            this.field = field;
            this.before = before;
            this.after = after;
        }

        public String getTopic() {
            return topic;
        }

        public int getPartition() {
            return partition;
        }

        public String getField() {
            return field;
        }

        public String getBefore() {
            return before;
        }

        public String getAfter() {
            return after;
        }
    }

    public static class ConsumerGroupChange {
        private String groupId;
        private String changeType;
        private String details;

        public ConsumerGroupChange(String groupId, String changeType, String details) {
            this.groupId = groupId;
            this.changeType = changeType;
            this.details = details;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getChangeType() {
            return changeType;
        }

        public String getDetails() {
            return details;
        }
    }

    public static class OffsetChange {
        private String groupId;
        private String topic;
        private int partition;
        private long beforeOffset;
        private long afterOffset;
        private long delta;

        public OffsetChange(String groupId, String topic, int partition,
                            long beforeOffset, long afterOffset) {
            this.groupId = groupId;
            this.topic = topic;
            this.partition = partition;
            this.beforeOffset = beforeOffset;
            this.afterOffset = afterOffset;
            this.delta = afterOffset - beforeOffset;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getTopic() {
            return topic;
        }

        public int getPartition() {
            return partition;
        }

        public long getBeforeOffset() {
            return beforeOffset;
        }

        public long getAfterOffset() {
            return afterOffset;
        }

        public long getDelta() {
            return delta;
        }
    }
}
