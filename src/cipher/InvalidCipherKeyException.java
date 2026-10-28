package cipher;

/**
 * Custom checked exception thrown when an invalid key is provided to a cipher.
 */
public class InvalidCipherKeyException extends Exception {

    public InvalidCipherKeyException(String message) {
        super(message);
    }

    public InvalidCipherKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
