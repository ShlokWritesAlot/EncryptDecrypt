package cipher;

/**
 * Custom checked exception thrown when an error occurs during file I/O
 * or file cryptographic processing.
 *
 * Demonstrates:
 * - Exception Wrapping / Chaining: Preserves original causes like FileNotFoundException or IOException.
 */
public class FileProcessingException extends Exception {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
