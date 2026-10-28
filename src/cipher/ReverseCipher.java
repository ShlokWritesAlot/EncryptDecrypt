package cipher;

/**
 * ReverseCipher reverses the order of bytes in the byte array.
 *
 * OOP Concepts Demonstrated:
 * - Interface Implementation: Implements {@link Cipher}.
 * - Keyless Cipher: Uses a default (no-arg) constructor.
 * - Symmetry / Involutory function: Encrypting and decrypting are identical operations
 *   because reversing an array twice returns the original array.
 */
public class ReverseCipher implements Cipher {

    /**
     * Default constructor. No key is required for a reverse cipher.
     */
    public ReverseCipher() {
        // No-arg constructor
    }

    @Override
    public String toString() {
        return "Reverse";
    }

    /**
     * Encrypts by reversing the byte array.
     *
     * @param data byte array to reverse
     * @return a new byte array with elements reversed
     */
    @Override
    public byte[] encrypt(byte[] data) {
        return reverse(data);
    }

    /**
     * Decrypts by reversing the byte array back to original order.
     *
     * @param data byte array to reverse
     * @return a new byte array with elements restored
     */
    @Override
    public byte[] decrypt(byte[] data) {
        return reverse(data);
    }

    /**
     * Helper method to invert the order of bytes in an array.
     */
    private byte[] reverse(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[data.length - 1 - i];
        }
        return result;
    }
}
