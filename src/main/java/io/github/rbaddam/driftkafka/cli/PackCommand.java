package io.github.rbaddam.driftkafka.cli;

import picocli.CommandLine.Command;

@Command(
        name = "pack",
        description = "Package snapshots and diffs for incident reporting (stub for v0.1)"
)
public class PackCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("pack command: stub implementation for v0.1");
        System.out.println("This command will create:");
        System.out.println("  - Compressed archive of snapshots");
        System.out.println("  - Generated diff reports");
        System.out.println("  - Incident-ready artifacts");
    }
}
