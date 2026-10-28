package cipher;

/**
 * Encapsulates performance and sizing statistics for file processing operations.
 *
 * OOP Concepts Demonstrated:
 * - Encapsulation: Read-only report values initialized via constructor and exposed via getters.
 */
public class ProcessingReport {

    private final String sourcePath;
    private final String destPath;
    private final long inputBytes;
    private final long outputBytes;
    private final long elapsedNanos;
    private final boolean encrypted;

    public ProcessingReport(String sourcePath, String destPath, long inputBytes,
                            long outputBytes, long elapsedNanos, boolean encrypted) {
        this.sourcePath = sourcePath;
        this.destPath = destPath;
        this.inputBytes = inputBytes;
        this.outputBytes = outputBytes;
        this.elapsedNanos = elapsedNanos;
        this.encrypted = encrypted;
    }

    public long getInputBytes() {
        return inputBytes;
    }

    public long getOutputBytes() {
        return outputBytes;
    }

    public double getElapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    public double getThroughputKBPerSec() {
        double seconds = elapsedNanos / 1_000_000_000.0;
        if (seconds <= 0) return 0;
        return (inputBytes / 1024.0) / seconds;
    }

    @Override
    public String toString() {
        return String.format(
            """
            ================ File Processing Report ================
            Operation:       %s
            Source File:     %s (%d bytes)
            Output File:     %s (%d bytes)
            Time Elapsed:    %.2f ms
            Throughput:      %.2f KB/s
            ========================================================""",
            encrypted ? "Encryption" : "Decryption",
            sourcePath, inputBytes,
            destPath, outputBytes,
            getElapsedMillis(),
            getThroughputKBPerSec()
        );
    }
}
