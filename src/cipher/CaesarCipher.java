package cipher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * CaesarCipher implements byte-level Caesar substitution encryption and decryption.
 *
 * OOP Concepts Demonstrated:
 * - Interface Implementation: Implements {@link Cipher}.
 * - Encapsulation: The shift key is private and final, accessible only via a getter.
 * - Exception Handling: Throws {@link InvalidCipherKeyException} when an invalid shift is given.
 */
public class CaesarCipher implements Cipher {

    // Encapsulation: Private and final field ensures immutability once constructed
    private final int shift;

    /**
     * Parameterized constructor.
     *
     * @param shift the integer amount by which to shift each byte.
     *              Normalized modulo 256.
     * @throws InvalidCipherKeyException if the effective shift (shift % 256) is 0 (no encryption effect).
     */
    public CaesarCipher(int shift) throws InvalidCipherKeyException {
        // Normalize shift into a positive [0, 255] range
        int normalizedShift = ((shift % 256) + 256) % 256;

        // Validation: an effective shift of 0 results in identity transformation (no encryption)
        if (normalizedShift == 0) {
            throw new InvalidCipherKeyException(
                "Invalid shift key: " + shift + " (effective shift modulo 256 is 0, which performs no encryption)."
            );
        }

        this.shift = normalizedShift;
    }

    /**
     * Getter for the normalized shift value.
     *
     * @return the shift amount
     */
    public int getShift() {
        return shift;
    }

    @Override
    public String toString() {
        return "Caesar(" + shift + ")";
    }

    /**
     * Encrypts the input byte array by shifting every byte value by the shift amount modulo 256.
     * Operates cleanly on binary/arbitrary byte data.
     *
     * @param data the byte array to encrypt
     * @return a new byte array containing the encrypted bytes
     */
    @Override
    public byte[] encrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            // (b & 0xFF) converts signed byte (-128 to 127) to unsigned int (0 to 255)
            int unsignedByte = data[i] & 0xFF;
            int encryptedVal = (unsignedByte + shift) % 256;
            output[i] = (byte) encryptedVal;
        }
        return output;
    }

    /**
     * Decrypts the input byte array by reversing the byte shift modulo 256.
     *
     * @param data the byte array to decrypt
     * @return a new byte array containing the decrypted bytes
     */
    @Override
    public byte[] decrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            int unsignedByte = data[i] & 0xFF;
            // Add 256 before modulo to guarantee non-negative value
            int decryptedVal = (unsignedByte - shift + 256) % 256;
            output[i] = (byte) decryptedVal;
        }
        return output;
    }

    /**
     * Test main method to demonstrate CaesarCipher usage and exception handling.
     */
    public static void main(String[] args) {
        System.out.println("=== CaesarCipher Test ===\n");

        // 1. Test Valid Shift with a sample string
        try {
            int shiftAmount = 5;
            CaesarCipher cipher = new CaesarCipher(shiftAmount);
            System.out.println("Initialized CaesarCipher with shift: " + cipher.getShift());

            String originalText = "Hello, Cipher Lab! \uD83D\uDE80 (Binary-safe OOP Project)";
            byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);

            // Encryption
            byte[] encryptedBytes = cipher.encrypt(originalBytes);
            System.out.println("Original Text:  " + originalText);
            System.out.println("Original Bytes:  " + Arrays.toString(originalBytes));
            System.out.println("Encrypted Bytes: " + Arrays.toString(encryptedBytes));

            // Decryption
            byte[] decryptedBytes = cipher.decrypt(encryptedBytes);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("Decrypted Text: " + decryptedText);

            // Verification
            boolean matches = originalText.equals(decryptedText);
            System.out.println("Matches Original: " + matches);

        } catch (InvalidCipherKeyException e) {
            System.err.println("Key Error: " + e.getMessage());
        }

        System.out.println("\n=== Testing Invalid Key Handling ===");

        // 2. Test Invalid Shift (shift % 256 == 0)
        try {
            System.out.println("Attempting to instantiate CaesarCipher with shift = 256 (0 mod 256)...");
            new CaesarCipher(256);
            System.out.println("FAIL: Exception was not thrown!");
        } catch (InvalidCipherKeyException e) {
            System.out.println("SUCCESS: Caught expected exception -> " + e.getMessage());
        }

        try {
            System.out.println("Attempting to instantiate CaesarCipher with shift = 0...");
            new CaesarCipher(0);
            System.out.println("FAIL: Exception was not thrown!");
        } catch (InvalidCipherKeyException e) {
            System.out.println("SUCCESS: Caught expected exception -> " + e.getMessage());
        }
    }
}
