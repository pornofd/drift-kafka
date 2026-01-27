package io.github.rbaddam.driftkafka.cli;

import io.github.rbaddam.driftkafka.snapshot.Snapshot;
import io.github.rbaddam.driftkafka.snapshot.SnapshotService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Command(
        name = "snapshot",
        description = "Capture Kafka cluster state at a point in time"
)
public class SnapshotCommand implements Runnable {

    @Option(names = {"--bootstrap-server"}, required = true,
            description = "Kafka bootstrap server (host:port)")
    private String bootstrapServer;

    @Option(names = {"--out"}, required = true,
            description = "Output file path for snapshot JSON")
    private File outputFile;

    @Option(names = {"--command-config"},
            description = "Path to Kafka client properties file")
    private File configFile;

    @Option(names = {"--include-offsets"},
            description = "Include consumer group offsets in snapshot")
    private boolean includeOffsets;

    @Override
    public void run() {
        try {
            Properties kafkaConfig = loadKafkaConfig();
            kafkaConfig.put("bootstrap.servers", bootstrapServer);

            SnapshotService service = new SnapshotService();
            Snapshot snapshot = service.captureSnapshot(kafkaConfig, includeOffsets);
            service.writeSnapshot(snapshot, outputFile);

            System.out.println("Snapshot captured successfully: " + outputFile.getAbsolutePath());

            if (snapshot.getOffsetErrors() != null && !snapshot.getOffsetErrors().isEmpty()) {
                System.err.println("\nWarnings:");
                for (String error : snapshot.getOffsetErrors()) {
                    System.err.println("  " + error);
                }
            }

        } catch (Exception e) {
            System.err.println("Error capturing snapshot: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Properties loadKafkaConfig() throws Exception {
        Properties props = new Properties();

        if (configFile != null) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
        }

        return props;
    }
}
