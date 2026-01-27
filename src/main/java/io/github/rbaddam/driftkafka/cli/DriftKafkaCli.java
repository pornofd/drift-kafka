package io.github.rbaddam.driftkafka.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "drift-kafka",
        description = "Read-only Kafka CLI tool for capturing and comparing cluster state",
        subcommands = {
                SnapshotCommand.class,
                DiffCommand.class,
                DoctorCommand.class,
                PackCommand.class
        },
        mixinStandardHelpOptions = true,
        version = "drift-kafka 0.1.0"
)
public class DriftKafkaCli implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DriftKafkaCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
