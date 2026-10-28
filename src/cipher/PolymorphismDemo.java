package cipher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Demonstrates runtime polymorphism with Cipher[], testing CaesarCipher,
 * VigenereCipher, and ReverseCipher.
 */
public class PolymorphismDemo {

    public static void main(String[] args) {
        System.out.println("=== Runtime Polymorphism & Ciphers Test ===\n");

        try {
            // Polymorphism in action:
            // An array of the interface type 'Cipher' holding distinct concrete subclasses
            Cipher[] ciphers = new Cipher[] {
                new CaesarCipher(13),
                new VigenereCipher("SECRET_KEY"),
                new ReverseCipher()
            };

            String originalText = "Cipher Lab - Testing OOP Polymorphism!";
            byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);

            System.out.println("Original String: \"" + originalText + "\"");
            System.out.println("Original Bytes:  " + Arrays.toString(originalBytes) + "\n");

            // Loop uniformly over the Cipher[] array
            for (Cipher cipher : ciphers) {
                String className = cipher.getClass().getSimpleName();
                System.out.println("--------------------------------------------------");
                System.out.println("Processing with: " + className);

                // Runtime polymorphism dynamically dispatches to the correct encrypt method
                byte[] encrypted = cipher.encrypt(originalBytes);
                System.out.println("Encrypted Bytes: " + Arrays.toString(encrypted));

                // Runtime polymorphism dynamically dispatches to the correct decrypt method
                byte[] decrypted = cipher.decrypt(encrypted);
                String recoveredText = new String(decrypted, StandardCharsets.UTF_8);
                System.out.println("Decrypted Bytes: " + Arrays.toString(decrypted));
                System.out.println("Recovered Text:  \"" + recoveredText + "\"");

                // Verification
                boolean match = Arrays.equals(originalBytes, decrypted) && originalText.equals(recoveredText);
                System.out.println("Round-trip match verified? " + match);
            }

            System.out.println("\n--------------------------------------------------");
            System.out.println("Testing VigenereCipher empty key exception:");
            try {
                new VigenereCipher("");
                System.out.println("FAIL: Exception not thrown!");
            } catch (InvalidCipherKeyException e) {
                System.out.println("SUCCESS: Caught expected InvalidCipherKeyException -> " + e.getMessage());
            }

        } catch (InvalidCipherKeyException e) {
            System.err.println("Key error: " + e.getMessage());
        }
    }
}
