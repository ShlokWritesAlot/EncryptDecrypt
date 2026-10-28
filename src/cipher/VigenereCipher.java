package cipher;

import java.nio.charset.StandardCharsets;

/**
 * VigenereCipher implements polyalphabetic substitution at the byte level.
 * 
 * Each byte of input data is shifted by the value of the corresponding byte in the key,
 * cycling through the key periodically.
 *
 * OOP Concepts Demonstrated:
 * - Interface Implementation: Implements {@link Cipher}.
 * - Encapsulation: The key is kept private and immutable.
 * - Exception Handling: Throws {@link InvalidCipherKeyException} for empty or null keys.
 */
public class VigenereCipher implements Cipher {

    // Encapsulation: private and final byte-representation of the key
    private final byte[] keyBytes;
    private final String originalKey;

    /**
     * Parameterized constructor.
     *
     * @param key the String key used to shift data bytes
     * @throws InvalidCipherKeyException if key is null or empty
     */
    public VigenereCipher(String key) throws InvalidCipherKeyException {
        if (key == null || key.isEmpty()) {
            throw new InvalidCipherKeyException("Key cannot be null or empty.");
        }
        this.originalKey = key;
        this.keyBytes = key.getBytes(StandardCharsets.UTF_8);
    }

    public String getKey() {
        return originalKey;
    }

    @Override
    public String toString() {
        return "Vigenere(" + originalKey + ")";
    }

    /**
     * Encrypts the input byte array by shifting each byte by the corresponding
     * key byte modulo 256.
     *
     * @param data the byte array to encrypt
     * @return a new byte array with encrypted bytes
     */
    @Override
    public byte[] encrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            int dataByte = data[i] & 0xFF;
            int keyShift = keyBytes[i % keyBytes.length] & 0xFF;
            output[i] = (byte) ((dataByte + keyShift) % 256);
        }
        return output;
    }

    /**
     * Decrypts the input byte array by reversing the key-based shift modulo 256.
     *
     * @param data the byte array to decrypt
     * @return a new byte array with decrypted bytes
     */
    @Override
    public byte[] decrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            int dataByte = data[i] & 0xFF;
            int keyShift = keyBytes[i % keyBytes.length] & 0xFF;
            output[i] = (byte) ((dataByte - keyShift + 256) % 256);
        }
        return output;
    }
}
