package cipher;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * FileProcessorTest verifies file reading, encrypting, writing, decrypting,
 * and comparing outputs with both a single cipher and a CipherChain.
 */
public class FileProcessorTest {

    public static void main(String[] args) {
        System.out.println("=== FileProcessor Test ===\n");

        String originalFilePath = "test_original.txt";
        String encryptedFilePath = "test_encrypted.bin";
        String decryptedFilePath = "test_decrypted.txt";

        try {
            // 1. Prepare a sample original file
            String testContent = "Line 1: Welcome to Cipher Lab!\n"
                               + "Line 2: Encrypting and decrypting with Java Streams.\n"
                               + "Line 3: 100% Binary Safe, works with any Cipher or CipherChain!";
            Files.writeString(new File(originalFilePath).toPath(), testContent);
            System.out.println("Created original test file: " + originalFilePath);

            // 2. Build a Cipher (Demonstrating using a CipherChain with FileProcessor)
            CipherChain chain = new CipherChain();
            chain.addCipher(new CaesarCipher(7));
            chain.addCipher(new VigenereCipher("JAVA_OOP"));
            chain.addCipher(new ReverseCipher());

            System.out.println("Using Cipher: " + chain);

            FileProcessor processor = new FileProcessor();

            // 3. Encrypt the file
            System.out.println("\nEncrypting " + originalFilePath + " -> " + encryptedFilePath + "...");
            processor.processFile(originalFilePath, encryptedFilePath, chain, true);
            System.out.println("Encryption completed successfully.");

            // 4. Decrypt the file
            System.out.println("Decrypting " + encryptedFilePath + " -> " + decryptedFilePath + "...");
            processor.processFile(encryptedFilePath, decryptedFilePath, chain, false);
            System.out.println("Decryption completed successfully.");

            // 5. Compare byte contents
            byte[] originalBytes = Files.readAllBytes(new File(originalFilePath).toPath());
            byte[] decryptedBytes = Files.readAllBytes(new File(decryptedFilePath).toPath());
            boolean matches = Arrays.equals(originalBytes, decryptedBytes);

            System.out.println("\n=== Verification ===");
            System.out.println("Original File Size:  " + originalBytes.length + " bytes");
            System.out.println("Decrypted File Size: " + decryptedBytes.length + " bytes");
            System.out.println("Binary contents match: " + matches);

            String decryptedContent = Files.readString(new File(decryptedFilePath).toPath());
            System.out.println("\nDecrypted File Content:\n" + decryptedContent);

            // 6. Test FileProcessingException with a non-existent file
            System.out.println("\nTesting Exception Handling for non-existent file:");
            try {
                processor.processFile("non_existent_file.xyz", "out.bin", chain, true);
                System.out.println("FAIL: FileProcessingException was not thrown!");
            } catch (FileProcessingException e) {
                System.out.println("SUCCESS: Caught FileProcessingException -> " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Root Cause preserved: " + e.getCause().getClass().getName());
                }
            }

        } catch (Exception e) {
            System.err.println("Unexpected test error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up temporary files
            new File(originalFilePath).delete();
            new File(encryptedFilePath).delete();
            new File(decryptedFilePath).delete();
            System.out.println("\nCleaned up temporary test files.");
        }
    }
}
