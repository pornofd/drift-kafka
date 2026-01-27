# drift-kafka

A read-only Kafka CLI tool for capturing cluster state and detecting drift during incidents.

Think of it as "git diff for Kafka" — snapshot your cluster state, compare snapshots, and get clean reports ready for incident docs.

## What it does

- Captures Kafka cluster state (topics, partitions, consumer groups, configs)
- Compares snapshots to detect changes over time
- Generates markdown reports for Slack/Jira/incident tracking
- Read-only — no cluster modifications

## What it does NOT do

- No continuous monitoring or alerting
- No background agents or polling
- No message payload inspection
- No cluster remediation or automation
- No JMX, Prometheus, or Connect integration

## Usage

```bash
java -jar drift-kafka.jar <command> [options]
```

Commands:
- `snapshot` - Capture cluster state
- `diff` - Compare two snapshots
- `doctor` - Validate environment (stub)
- `pack` - Package reports (stub)

## Quickstart

Build:

```bash
mvn clean package
```

Capture snapshot:

```bash
java -jar target/drift-kafka.jar snapshot \
  --bootstrap-server localhost:9092 \
  --out before.json \
  --include-offsets
```

Capture another later:

```bash
java -jar target/drift-kafka.jar snapshot \
  --bootstrap-server localhost:9092 \
  --out after.json
```

Compare:

```bash
java -jar target/drift-kafka.jar diff \
  --before before.json \
  --after after.json
```

Options:
- `--include-offsets` - Include consumer offsets (slower)
- `--command-config` - Path to client properties file
- `--format md` - Output markdown instead of JSON

Example with authentication:

```bash
java -jar target/drift-kafka.jar snapshot \
  --bootstrap-server localhost:9092 \
  --command-config client.properties \
  --out snapshot.json
```

## Example output

See `examples/` for sample snapshots and diffs.

## Requirements

- Java 17+
- Kafka cluster access (read-only permissions)

## License

Apache License 2.0

