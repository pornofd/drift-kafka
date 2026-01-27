# Kafka State Diff

**Before:** 2024-01-15T10:30:00Z
**After:** 2024-01-15T11:45:00Z

## Summary

Total changes: 6

- Topics added: 1
- Topic changes: 1
- Partition changes: 2
- Consumer group changes: 2

## Topic Changes

### Topics Added

- `notifications`

### Topic Configuration Changes

| Topic | Field | Before | After |
|-------|-------|--------|-------|
| `events` | config.retention.ms | 604800000 | 864000000 |

## Partition Changes

| Topic | Partition | Field | Before | After |
|-------|-----------|-------|--------|-------|
| `events` | 2 | leader | 3 | 1 |
| `events` | 2 | isr | [3, 1, 2] | [1, 2] |

## Consumer Group Changes

| Group ID | Change Type | Details |
|----------|-------------|----------|
| `event-processor` | memberCount | 2 -> 3 |
| `notification-consumer` | added | state=Stable, members=1 |


