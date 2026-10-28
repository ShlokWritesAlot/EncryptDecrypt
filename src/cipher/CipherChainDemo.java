package cipher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Demonstrates CipherChain with a 3-cipher pipeline and verifies
 * end-to-end round-trip encryption and decryption.
 */
public class CipherChainDemo {

    public static void main(String[] args) {
        System.out.println("=== CipherChain Demo ===\n");

        try {
            // 1. Build individual ciphers
            Cipher caesar = new CaesarCipher(3);
            Cipher vigenere = new VigenereCipher("KEY");
            Cipher reverse = new ReverseCipher();

            // 2. Assemble into CipherChain
            CipherChain chain = new CipherChain();
            chain.addCipher(caesar);
            chain.addCipher(vigenere);
            chain.addCipher(reverse);

            // 3. Display chain representation
            System.out.println("Constructed Chain: " + chain);

            // 4. Sample test payload
            String secretMessage = "TopSecret: CipherChain Works Seamlessly! 12345";
            byte[] originalBytes = secretMessage.getBytes(StandardCharsets.UTF_8);

            System.out.println("\nOriginal Message: \"" + secretMessage + "\"");
            System.out.println("Original Bytes:   " + Arrays.toString(originalBytes));

            // 5. Encrypt through the entire chain
            byte[] encryptedBytes = chain.encrypt(originalBytes);
            System.out.println("\nEncrypted Bytes:  " + Arrays.toString(encryptedBytes));

            // 6. Decrypt through the chain (reverses order)
            byte[] decryptedBytes = chain.decrypt(encryptedBytes);
            String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("Decrypted Bytes:  " + Arrays.toString(decryptedBytes));
            System.out.println("Decrypted Text:   \"" + decryptedMessage + "\"");

            // 7. Verify round trip
            boolean bytesMatch = Arrays.equals(originalBytes, decryptedBytes);
            boolean textMatches = secretMessage.equals(decryptedMessage);

            System.out.println("\nRound-trip Status:");
            System.out.println("- Byte arrays identical: " + bytesMatch);
            System.out.println("- String matches original: " + textMatches);

        } catch (InvalidCipherKeyException e) {
            System.err.println("Error initializing cipher key: " + e.getMessage());
        }
    }
}
