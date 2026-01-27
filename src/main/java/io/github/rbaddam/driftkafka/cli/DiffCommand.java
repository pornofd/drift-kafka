package io.github.rbaddam.driftkafka.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rbaddam.driftkafka.diff.DiffEngine;
import io.github.rbaddam.driftkafka.diff.MarkdownRenderer;
import io.github.rbaddam.driftkafka.diff.SnapshotDiff;
import io.github.rbaddam.driftkafka.snapshot.Snapshot;
import io.github.rbaddam.driftkafka.snapshot.SnapshotService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;

@Command(
        name = "diff",
        description = "Compare two Kafka snapshots and detect changes"
)
public class DiffCommand implements Runnable {

    @Option(names = {"--before"}, required = true,
            description = "Path to the first (earlier) snapshot JSON")
    private File beforeFile;

    @Option(names = {"--after"}, required = true,
            description = "Path to the second (later) snapshot JSON")
    private File afterFile;

    @Option(names = {"--format"}, defaultValue = "json",
            description = "Output format: json or md (default: json)")
    private String format;

    @Option(names = {"--out"},
            description = "Output file path (default: stdout)")
    private File outputFile;

    @Override
    public void run() {
        try {
            SnapshotService service = new SnapshotService();
            Snapshot before = service.readSnapshot(beforeFile);
            Snapshot after = service.readSnapshot(afterFile);

            DiffEngine engine = new DiffEngine();
            SnapshotDiff diff = engine.diff(before, after);

            String output;
            if ("json".equalsIgnoreCase(format)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                output = mapper.writeValueAsString(diff);
            } else {
                MarkdownRenderer renderer = new MarkdownRenderer();
                output = renderer.render(diff);
            }

            if (outputFile != null) {
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write(output);
                }
                System.out.println("Diff written to: " + outputFile.getAbsolutePath());
            } else {
                System.out.println(output);
            }

        } catch (Exception e) {
            System.err.println("Error computing diff: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
