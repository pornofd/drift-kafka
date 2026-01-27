# Examples

Sample snapshots and diffs for drift-kafka.

## Files

- `snapshot-before.json` - Initial cluster state
- `snapshot-after.json` - Cluster state after changes
- `diff-output.json` - JSON diff output
- `diff-output.md` - Markdown diff output
- `client.properties.example` - Authentication config template

## Basic Usage

Capture snapshot:
```bash
java -jar drift-kafka.jar snapshot \
  --bootstrap-server localhost:9092 \
  --out snapshot.json
```

Compare snapshots:
```bash
java -jar drift-kafka.jar diff \
  --before snapshot-before.json \
  --after snapshot-after.json
```

With authentication:
```bash
java -jar drift-kafka.jar snapshot \
  --bootstrap-server kafka:9092 \
  --command-config client.properties \
  --out snapshot.json
```

Include offsets:
```bash
java -jar drift-kafka.jar snapshot \
  --bootstrap-server localhost:9092 \
  --include-offsets \
  --out snapshot.json
```

Output formats:
```bash
# JSON (default)
java -jar drift-kafka.jar diff --before a.json --after b.json

# Markdown
java -jar drift-kafka.jar diff --before a.json --after b.json --format md
```
