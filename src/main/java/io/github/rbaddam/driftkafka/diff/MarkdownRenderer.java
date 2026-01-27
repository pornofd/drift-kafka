package io.github.rbaddam.driftkafka.diff;

import java.util.List;

public class MarkdownRenderer {

    public String render(SnapshotDiff diff) {
        StringBuilder md = new StringBuilder();

        md.append("# Kafka State Diff\n\n");
        md.append("**Before:** ").append(diff.getBeforeTimestamp()).append("\n");
        md.append("**After:** ").append(diff.getAfterTimestamp()).append("\n\n");

        renderSummary(diff, md);
        renderTopicChanges(diff, md);
        renderPartitionChanges(diff, md);
        renderConsumerGroupChanges(diff, md);
        renderOffsetChanges(diff, md);

        return md.toString();
    }

    private void renderSummary(SnapshotDiff diff, StringBuilder md) {
        md.append("## Summary\n\n");

        int changeCount = 0;
        if (diff.getTopicsAdded() != null) changeCount += diff.getTopicsAdded().size();
        if (diff.getTopicsRemoved() != null) changeCount += diff.getTopicsRemoved().size();
        if (diff.getTopicChanges() != null) changeCount += diff.getTopicChanges().size();
        if (diff.getPartitionChanges() != null) changeCount += diff.getPartitionChanges().size();
        if (diff.getConsumerGroupChanges() != null) changeCount += diff.getConsumerGroupChanges().size();
        if (diff.getOffsetChanges() != null) changeCount += diff.getOffsetChanges().size();

        if (changeCount == 0) {
            md.append("No changes detected.\n\n");
            return;
        }

        md.append("Total changes: ").append(changeCount).append("\n\n");

        if (diff.getTopicsAdded() != null) {
            md.append("- Topics added: ").append(diff.getTopicsAdded().size()).append("\n");
        }
        if (diff.getTopicsRemoved() != null) {
            md.append("- Topics removed: ").append(diff.getTopicsRemoved().size()).append("\n");
        }
        if (diff.getTopicChanges() != null) {
            md.append("- Topic changes: ").append(diff.getTopicChanges().size()).append("\n");
        }
        if (diff.getPartitionChanges() != null) {
            md.append("- Partition changes: ").append(diff.getPartitionChanges().size()).append("\n");
        }
        if (diff.getConsumerGroupChanges() != null) {
            md.append("- Consumer group changes: ").append(diff.getConsumerGroupChanges().size()).append("\n");
        }
        if (diff.getOffsetChanges() != null) {
            md.append("- Offset changes: ").append(diff.getOffsetChanges().size()).append("\n");
        }

        md.append("\n");
    }

    private void renderTopicChanges(SnapshotDiff diff, StringBuilder md) {
        if (diff.getTopicsAdded() == null && diff.getTopicsRemoved() == null && diff.getTopicChanges() == null) {
            return;
        }

        md.append("## Topic Changes\n\n");

        if (diff.getTopicsAdded() != null && !diff.getTopicsAdded().isEmpty()) {
            md.append("### Topics Added\n\n");
            for (String topic : diff.getTopicsAdded()) {
                md.append("- `").append(topic).append("`\n");
            }
            md.append("\n");
        }

        if (diff.getTopicsRemoved() != null && !diff.getTopicsRemoved().isEmpty()) {
            md.append("### Topics Removed\n\n");
            for (String topic : diff.getTopicsRemoved()) {
                md.append("- `").append(topic).append("`\n");
            }
            md.append("\n");
        }

        if (diff.getTopicChanges() != null && !diff.getTopicChanges().isEmpty()) {
            md.append("### Topic Configuration Changes\n\n");
            md.append("| Topic | Field | Before | After |\n");
            md.append("|-------|-------|--------|-------|\n");
            for (SnapshotDiff.TopicChange change : diff.getTopicChanges()) {
                md.append("| `").append(change.getTopic()).append("` | ")
                        .append(change.getField()).append(" | ")
                        .append(change.getBefore()).append(" | ")
                        .append(change.getAfter()).append(" |\n");
            }
            md.append("\n");
        }
    }

    private void renderPartitionChanges(SnapshotDiff diff, StringBuilder md) {
        if (diff.getPartitionChanges() == null || diff.getPartitionChanges().isEmpty()) {
            return;
        }

        md.append("## Partition Changes\n\n");
        md.append("| Topic | Partition | Field | Before | After |\n");
        md.append("|-------|-----------|-------|--------|-------|\n");

        for (SnapshotDiff.PartitionChange change : diff.getPartitionChanges()) {
            md.append("| `").append(change.getTopic()).append("` | ")
                    .append(change.getPartition()).append(" | ")
                    .append(change.getField()).append(" | ")
                    .append(change.getBefore()).append(" | ")
                    .append(change.getAfter()).append(" |\n");
        }

        md.append("\n");
    }

    private void renderConsumerGroupChanges(SnapshotDiff diff, StringBuilder md) {
        if (diff.getConsumerGroupChanges() == null || diff.getConsumerGroupChanges().isEmpty()) {
            return;
        }

        md.append("## Consumer Group Changes\n\n");
        md.append("| Group ID | Change Type | Details |\n");
        md.append("|----------|-------------|----------|\n");

        for (SnapshotDiff.ConsumerGroupChange change : diff.getConsumerGroupChanges()) {
            md.append("| `").append(change.getGroupId()).append("` | ")
                    .append(change.getChangeType()).append(" | ")
                    .append(change.getDetails()).append(" |\n");
        }

        md.append("\n");
    }

    private void renderOffsetChanges(SnapshotDiff diff, StringBuilder md) {
        if (diff.getOffsetChanges() == null || diff.getOffsetChanges().isEmpty()) {
            return;
        }

        md.append("## Offset Changes\n\n");
        md.append("| Group ID | Topic | Partition | Before | After | Delta |\n");
        md.append("|----------|-------|-----------|--------|-------|-------|\n");

        for (SnapshotDiff.OffsetChange change : diff.getOffsetChanges()) {
            md.append("| `").append(change.getGroupId()).append("` | ")
                    .append("`").append(change.getTopic()).append("` | ")
                    .append(change.getPartition()).append(" | ")
                    .append(change.getBeforeOffset()).append(" | ")
                    .append(change.getAfterOffset()).append(" | ")
                    .append(formatDelta(change.getDelta())).append(" |\n");
        }

        md.append("\n");
    }

    private String formatDelta(long delta) {
        if (delta > 0) {
            return "+" + delta;
        }
        return String.valueOf(delta);
    }
}
