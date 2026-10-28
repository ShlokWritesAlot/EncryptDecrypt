package cipher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles serialization and deserialization of a {@link CipherChain} configuration
 * to and from human-readable text files.
 *
 * File format example:
 * CAESAR:5
 * VIGENERE:SECRET_KEY
 * REVERSE
 */
public class ChainFileManager {

    /**
     * Saves a CipherChain to a configuration file.
     *
     * @param chain the CipherChain to save
     * @param filePath destination path
     * @throws FileProcessingException if file writing fails
     */
    public static void saveChain(CipherChain chain, String filePath) throws FileProcessingException {
        if (chain == null || filePath == null) {
            throw new FileProcessingException("Chain and filePath cannot be null.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Cipher cipher : chain.getCiphers()) {
                if (cipher instanceof CaesarCipher cc) {
                    writer.write("CAESAR:" + cc.getShift());
                } else if (cipher instanceof VigenereCipher vc) {
                    writer.write("VIGENERE:" + vc.getKey());
                } else if (cipher instanceof ReverseCipher) {
                    writer.write("REVERSE");
                } else {
                    throw new FileProcessingException("Unsupported cipher type in chain: " + cipher.getClass());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to save chain configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Loads a CipherChain from a configuration file.
     *
     * @param filePath path to the config file
     * @return the reconstructed CipherChain
     * @throws FileProcessingException if file reading or key validation fails
     */
    public static CipherChain loadChain(String filePath) throws FileProcessingException {
        if (filePath == null) {
            throw new FileProcessingException("filePath cannot be null.");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileProcessingException("Chain config file not found: " + filePath);
        }

        CipherChain chain = new CipherChain();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip comments and empty lines
                }

                String[] parts = line.split(":", 2);
                String type = parts[0].toUpperCase();

                try {
                    switch (type) {
                        case "CAESAR" -> {
                            if (parts.length < 2) {
                                throw new FileProcessingException("Line " + lineNum + ": Missing shift for CAESAR.");
                            }
                            int shift = Integer.parseInt(parts[1].trim());
                            chain.addCipher(new CaesarCipher(shift));
                        }
                        case "VIGENERE" -> {
                            if (parts.length < 2) {
                                throw new FileProcessingException("Line " + lineNum + ": Missing key for VIGENERE.");
                            }
                            chain.addCipher(new VigenereCipher(parts[1].trim()));
                        }
                        case "REVERSE" -> chain.addCipher(new ReverseCipher());
                        default -> throw new FileProcessingException("Line " + lineNum + ": Unknown cipher type '" + type + "'.");
                    }
                } catch (NumberFormatException e) {
                    throw new FileProcessingException("Line " + lineNum + ": Invalid integer for Caesar shift: " + parts[1]);
                } catch (InvalidCipherKeyException e) {
                    throw new FileProcessingException("Line " + lineNum + ": Invalid key: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("Error reading chain configuration: " + e.getMessage(), e);
        }

        return chain;
    }
}
