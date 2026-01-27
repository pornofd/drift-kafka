package io.github.rbaddam.driftkafka.snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.rbaddam.driftkafka.kafka.KafkaClient;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SnapshotService {
    private static final String TOOL_VERSION = "0.1.0";

    private final ObjectMapper mapper;

    public SnapshotService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Snapshot captureSnapshot(Properties kafkaConfig, boolean includeOffsets) throws Exception {
        KafkaClient client = new KafkaClient(kafkaConfig);

        try {
            Snapshot snapshot = new Snapshot();
            snapshot.setSchemaVersion(1);
            snapshot.setToolVersion(TOOL_VERSION);
            snapshot.setTimestamp(Instant.now());

            snapshot.setCluster(client.getClusterInfo());
            snapshot.setTopics(client.getTopicInfo());

            List<String> offsetErrors = new ArrayList<>();
            snapshot.setConsumerGroups(client.getConsumerGroupInfo(includeOffsets, offsetErrors));
            snapshot.setOffsetsCaptured(includeOffsets);
            snapshot.setOffsetErrors(offsetErrors.isEmpty() ? null : offsetErrors);

            return snapshot;
        } finally {
            client.close();
        }
    }

    public void writeSnapshot(Snapshot snapshot, File outputFile) throws IOException {
        mapper.writeValue(outputFile, snapshot);
    }

    public Snapshot readSnapshot(File inputFile) throws IOException {
        return mapper.readValue(inputFile, Snapshot.class);
    }
}
