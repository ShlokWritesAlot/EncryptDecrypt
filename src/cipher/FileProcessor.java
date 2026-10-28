package cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileProcessor handles reading from and writing to files using raw byte streams.
 *
 * OOP Concepts Demonstrated:
 * - Polymorphism & Inversion of Control: FileProcessor operates on the {@link Cipher}
 *   interface, agnostic to whether it is a CaesarCipher, VigenereCipher, ReverseCipher,
 *   or a complex CipherChain.
 * - Resource Management & Exception Handling: Uses try-with-resources to guarantee
 *   proper stream closure, and wraps underlying IOExceptions into {@link FileProcessingException}.
 */
public class FileProcessor {

    /**
     * Reads all bytes from an input file, transforms them via the specified cipher
     * (encrypting or decrypting), and writes the transformed bytes to the output file.
     *
     * @param inputPath  path to the source file
     * @param outputPath path to the destination file
     * @param cipher     any implementation of {@link Cipher} (single cipher or CipherChain)
     * @param encrypt    true to encrypt, false to decrypt
     * @throws FileProcessingException if any file or I/O error occurs
     */
    public ProcessingReport processFile(String inputPath, String outputPath, Cipher cipher, boolean encrypt)
            throws FileProcessingException {

        if (inputPath == null || outputPath == null) {
            throw new FileProcessingException("Input and output file paths cannot be null.");
        }
        if (cipher == null) {
            throw new FileProcessingException("Cipher cannot be null.");
        }

        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new FileProcessingException(
                "Source file not found: " + inputPath,
                new FileNotFoundException("File does not exist: " + inputPath)
            );
        }

        long startTime = System.nanoTime();
        long inputSize = inputFile.length();
        long outputSize = 0;

        // Try-with-resources: automatically closes streams even if an exception occurs
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            // Read all bytes from the source file
            byte[] inputBytes = fis.readAllBytes();

            // Polymorphic dispatch: cipher.encrypt() or cipher.decrypt()
            byte[] processedBytes;
            if (encrypt) {
                processedBytes = cipher.encrypt(inputBytes);
            } else {
                processedBytes = cipher.decrypt(inputBytes);
            }

            // Write transformed bytes to output
            if (processedBytes != null) {
                fos.write(processedBytes);
                outputSize = processedBytes.length;
            }

            long elapsedNanos = System.nanoTime() - startTime;
            return new ProcessingReport(inputPath, outputPath, inputSize, outputSize, elapsedNanos, encrypt);

        } catch (FileNotFoundException e) {
            throw new FileProcessingException("Could not open file: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new FileProcessingException("I/O error while processing file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FileProcessingException("Unexpected error during cipher execution: " + e.getMessage(), e);
        }
    }
}
