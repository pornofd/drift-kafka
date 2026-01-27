# Snapshot Schema

## Version 1

The snapshot schema uses `schemaVersion: 1` for all v0.1 snapshots.

### Top-level fields

```json
{
  "schemaVersion": 1,
  "toolVersion": "0.1.0",
  "timestamp": "2024-01-15T10:30:00Z",
  "cluster": { ... },
  "topics": [ ... ],
  "consumerGroups": [ ... ],
  "offsetsCaptured": false,
  "offsetErrors": [ ... ]
}
```

- `schemaVersion`: Integer version of the schema format (currently 1)
- `toolVersion`: Version of drift-kafka that created this snapshot
- `timestamp`: UTC timestamp in RFC3339 format
- `cluster`: Cluster-level information
- `topics`: List of all topics and their configurations
- `consumerGroups`: List of all consumer groups
- `offsetsCaptured`: Boolean indicating if offsets were included
- `offsetErrors`: Optional list of error messages if offset capture failed for some groups

### Cluster

```json
{
  "kafkaVersion": "unknown",
  "brokers": [
    {
      "id": 1,
      "rack": "us-west-2a"
    }
  ]
}
```

- `kafkaVersion`: Kafka version (best effort, defaults to "unknown")
- `brokers`: List of broker nodes with ID and rack info

### Topics

```json
{
  "name": "events",
  "partitionCount": 6,
  "replicationFactor": 3,
  "partitions": [ ... ],
  "config": {
    "retention.ms": "604800000",
    "cleanup.policy": "delete"
  }
}
```

- `name`: Topic name
- `partitionCount`: Number of partitions
- `replicationFactor`: Replication factor
- `partitions`: List of partition details
- `config`: Allowlisted topic configurations (see below)

#### Partition details

```json
{
  "partition": 0,
  "leader": 1,
  "replicas": [1, 2, 3],
  "isr": [1, 2, 3]
}
```

- `partition`: Partition number
- `leader`: Broker ID of the current leader
- `replicas`: List of broker IDs for all replicas
- `isr`: List of broker IDs for in-sync replicas

### Allowlisted topic configs

Only these configs are captured (non-default values only):

- `retention.ms`
- `retention.bytes`
- `cleanup.policy`
- `min.insync.replicas`
- `segment.ms`
- `segment.bytes`
- `max.message.bytes`
- `compression.type`

### Consumer groups

```json
{
  "groupId": "event-processor",
  "state": "Stable",
  "memberCount": 2,
  "assignments": [
    {
      "topic": "events",
      "partition": 0
    }
  ],
  "offsets": [
    {
      "topic": "events",
      "partition": 0,
      "offset": 1234567
    }
  ]
}
```

- `groupId`: Consumer group ID
- `state`: Group state (e.g., Stable, Dead, Empty)
- `memberCount`: Number of active members
- `assignments`: List of topic-partition assignments
- `offsets`: List of committed offsets (only if `--include-offsets` was used)

### Backward compatibility

Schema version 1 is the initial version. Future schema changes will increment `schemaVersion` and document migration paths.

Diff engine will require matching schema versions on both snapshots.
