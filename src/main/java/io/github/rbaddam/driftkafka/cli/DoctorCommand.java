package io.github.rbaddam.driftkafka.cli;

import picocli.CommandLine.Command;

@Command(
        name = "doctor",
        description = "Validate environment and configuration (stub for v0.1)"
)
public class DoctorCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("doctor command: stub implementation for v0.1");
        System.out.println("This command will validate:");
        System.out.println("  - Kafka connectivity");
        System.out.println("  - Client configuration");
        System.out.println("  - Required permissions");
    }
}
