package cipher;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * Main application driver for Cipher Lab.
 * Provides a robust, Scanner-based interactive console menu.
 *
 * Catches all input and processing exceptions to ensure the program never crashes.
 */
public class Main {

    private final Scanner scanner;
    private final FileProcessor fileProcessor;
    private Cipher currentCipher;

    public Main() {
        this.scanner = new Scanner(System.in);
        this.fileProcessor = new FileProcessor();
        // Default cipher on startup
        try {
            this.currentCipher = new CaesarCipher(3);
        } catch (InvalidCipherKeyException ignored) {
            // Unreachable with shift 3
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    /**
     * Main interactive loop.
     */
    public void run() {
        System.out.println("==================================================");
        System.out.println("            WELCOME TO CIPHER LAB                ");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = promptInt("Select an option (1-8): ");

            switch (choice) {
                case 1 -> handleTextMessage(true);
                case 2 -> handleTextMessage(false);
                case 3 -> handleFileProcessing(true);
                case 4 -> handleFileProcessing(false);
                case 5 -> handleBuildCipherChain();
                case 6 -> handleSaveLoadChain();
                case 7 -> handleCaesarBruteForce();
                case 8 -> {
                    System.out.println("\nExiting Cipher Lab. Goodbye!");
                    running = false;
                }
                default -> System.out.println("(!) Invalid option. Please enter a number between 1 and 8.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Active Cipher: " + (currentCipher != null ? currentCipher : "None"));
        System.out.println("--------------------------------------------------");
        System.out.println("1. Encrypt a message (typed in)");
        System.out.println("2. Decrypt a message (typed in)");
        System.out.println("3. Encrypt a file (with Benchmark Report)");
        System.out.println("4. Decrypt a file (with Benchmark Report)");
        System.out.println("5. Configure Cipher / Build a Cipher Chain");
        System.out.println("6. Save / Load Cipher Chain (File Config)");
        System.out.println("7. Brute-Force Crack Caesar Cipher");
        System.out.println("8. Exit");
    }

    /**
     * Handles text encryption or decryption from typed console input.
     */
    private void handleTextMessage(boolean encrypt) {
        ensureActiveCipher();
        String action = encrypt ? "Encryption" : "Decryption";
        System.out.println("\n--- Text " + action + " ---");

        if (encrypt) {
            System.out.print("Enter plain text to encrypt: ");
            String input = scanner.nextLine();
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

            byte[] outputBytes = currentCipher.encrypt(inputBytes);
            // Present encrypted output as Base64 so binary non-printable characters are visible
            String base64Output = Base64.getEncoder().encodeToString(outputBytes);

            System.out.println("\n[Result]");
            System.out.println("Ciphertext (Base64): " + base64Output);
        } else {
            System.out.print("Enter Base64 ciphertext to decrypt: ");
            String input = scanner.nextLine().trim();

            try {
                byte[] inputBytes = Base64.getDecoder().decode(input);
                byte[] outputBytes = currentCipher.decrypt(inputBytes);
                String recoveredText = new String(outputBytes, StandardCharsets.UTF_8);

                System.out.println("\n[Result]");
                System.out.println("Decrypted Text: " + recoveredText);
            } catch (IllegalArgumentException e) {
                System.out.println("(!) Error: Input is not valid Base64 encoded data.");
            }
        }
    }

    /**
     * Handles file encryption or decryption.
     */
    private void handleFileProcessing(boolean encrypt) {
        ensureActiveCipher();
        String action = encrypt ? "Encryption" : "Decryption";
        System.out.println("\n--- File " + action + " ---");

        System.out.print("Enter source file path: ");
        String sourcePath = scanner.nextLine().trim();

        if (sourcePath.isEmpty()) {
            System.out.println("(!) Error: Source file path cannot be empty.");
            return;
        }

        File src = new File(sourcePath);
        if (!src.exists() || !src.isFile()) {
            System.out.println("(!) Error: File not found: '" + sourcePath + "'");
            return;
        }

        System.out.print("Enter destination file path: ");
        String destPath = scanner.nextLine().trim();
        if (destPath.isEmpty()) {
            System.out.println("(!) Error: Destination file path cannot be empty.");
            return;
        }

        try {
            ProcessingReport report = fileProcessor.processFile(sourcePath, destPath, currentCipher, encrypt);
            System.out.println("(+) Success: File " + (encrypt ? "encrypted" : "decrypted")
                    + " successfully to: " + destPath);
            System.out.println(report);
        } catch (FileProcessingException e) {
            System.out.println("(!) File Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("    Cause: " + e.getCause().getMessage());
            }
        }
    }

    /**
     * Handles saving and loading CipherChain configurations to/from disk.
     */
    private void handleSaveLoadChain() {
        System.out.println("\n--- Save / Load Cipher Chain Configuration ---");
        System.out.println("1. Save Current Active Chain to File");
        System.out.println("2. Load Chain Configuration from File");
        System.out.println("3. Back");

        int choice = promptInt("Select option (1-3): ");
        if (choice == 1) {
            if (!(currentCipher instanceof CipherChain chain)) {
                System.out.println("(!) Current cipher is not a CipherChain. (Active: " + currentCipher + ")");
                System.out.println("    Create or configure a CipherChain first (Option 5).");
                return;
            }
            System.out.print("Enter destination config file path (e.g. my_chain.cfg): ");
            String path = scanner.nextLine().trim();
            try {
                ChainFileManager.saveChain(chain, path);
                System.out.println("(+) Chain saved successfully to: " + path);
            } catch (FileProcessingException e) {
                System.out.println("(!) Failed to save chain: " + e.getMessage());
            }
        } else if (choice == 2) {
            System.out.print("Enter config file path to load: ");
            String path = scanner.nextLine().trim();
            try {
                CipherChain loaded = ChainFileManager.loadChain(path);
                currentCipher = loaded;
                System.out.println("(+) Successfully loaded and activated chain: " + currentCipher);
            } catch (FileProcessingException e) {
                System.out.println("(!) Failed to load chain: " + e.getMessage());
            }
        }
    }

    /**
     * Attempts to brute-force crack a Caesar cipher ciphertext.
     */
    private void handleCaesarBruteForce() {
        System.out.println("\n--- Caesar Cipher Brute-Force Cracker ---");
        System.out.print("Enter Base64 encoded ciphertext: ");
        String input = scanner.nextLine().trim();

        byte[] cipherBytes;
        try {
            cipherBytes = Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException e) {
            System.out.println("(!) Error: Input is not valid Base64 encoded data.");
            return;
        }

        System.out.println("Analyzing all 255 shifts with English text heuristic...");
        var candidates = CaesarCracker.crack(cipherBytes, 5);

        if (candidates.isEmpty()) {
            System.out.println("No candidates found.");
            return;
        }

        System.out.println("\nTop 5 Candidates (Ranked by likelihood):");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-6s | %-8s | %s%n", "Shift", "Score", "Decrypted Preview");
        System.out.println("----------------------------------------------------------------------");
        for (var c : candidates) {
            System.out.printf("%-6d | %-8.1f | %s%n", c.shift(), c.score(), c.sampleDecrypted());
        }
        System.out.println("----------------------------------------------------------------------");

        System.out.print("Would you like to set one of these shifts as active CaesarCipher? (y/n): ");
        String resp = scanner.nextLine().trim().toLowerCase();
        if (resp.startsWith("y")) {
            int chosenShift = promptInt("Enter shift to activate: ");
            try {
                currentCipher = new CaesarCipher(chosenShift);
                System.out.println("(+) Active cipher updated to: " + currentCipher);
            } catch (InvalidCipherKeyException e) {
                System.out.println("(!) Error: " + e.getMessage());
            }
        }
    }

    /**
     * Submenu to configure a single cipher or build a composite CipherChain.
     */
    private void handleBuildCipherChain() {
        System.out.println("\n--- Cipher Configuration ---");
        System.out.println("1. Select Single Cipher (Caesar, Vigenere, or Reverse)");
        System.out.println("2. Build Multi-Cipher Chain");
        System.out.println("3. Back to Main Menu");

        int mode = promptInt("Select mode (1-3): ");
        if (mode == 1) {
            Cipher single = promptSingleCipher();
            if (single != null) {
                currentCipher = single;
                System.out.println("(+) Active cipher set to: " + currentCipher);
            }
        } else if (mode == 2) {
            CipherChain chain = new CipherChain();
            boolean adding = true;
            System.out.println("\nBuilding a Cipher Chain. Add ciphers in order:");

            while (adding) {
                System.out.println("\nCurrent Chain: " + chain);
                System.out.println("1. Add Caesar Cipher");
                System.out.println("2. Add Vigenere Cipher");
                System.out.println("3. Add Reverse Cipher");
                System.out.println("4. Finish and Activate Chain");
                System.out.println("5. Cancel");

                int choice = promptInt("Choose an action (1-5): ");
                switch (choice) {
                    case 1 -> {
                        Cipher c = createCaesar();
                        if (c != null) chain.addCipher(c);
                    }
                    case 2 -> {
                        Cipher v = createVigenere();
                        if (v != null) chain.addCipher(v);
                    }
                    case 3 -> {
                        chain.addCipher(new ReverseCipher());
                        System.out.println("(+) Added ReverseCipher.");
                    }
                    case 4 -> {
                        if (chain.getCiphers().isEmpty()) {
                            System.out.println("(!) Warning: Chain is empty. Active cipher unchanged.");
                        } else {
                            currentCipher = chain;
                            System.out.println("(+) Activated new chain: " + currentCipher);
                        }
                        adding = false;
                    }
                    case 5 -> {
                        System.out.println("Chain creation cancelled.");
                        adding = false;
                    }
                    default -> System.out.println("(!) Invalid choice.");
                }
            }
        }
    }

    private Cipher promptSingleCipher() {
        System.out.println("\nSelect Cipher Type:");
        System.out.println("1. Caesar Cipher");
        System.out.println("2. Vigenere Cipher");
        System.out.println("3. Reverse Cipher");

        int choice = promptInt("Choose (1-3): ");
        return switch (choice) {
            case 1 -> createCaesar();
            case 2 -> createVigenere();
            case 3 -> new ReverseCipher();
            default -> {
                System.out.println("(!) Invalid selection.");
                yield null;
            }
        };
    }

    private Cipher createCaesar() {
        int shift = promptInt("Enter shift integer: ");
        try {
            return new CaesarCipher(shift);
        } catch (InvalidCipherKeyException e) {
            System.out.println("(!) Key Error: " + e.getMessage());
            return null;
        }
    }

    private Cipher createVigenere() {
        System.out.print("Enter key string: ");
        String key = scanner.nextLine().trim();
        try {
            return new VigenereCipher(key);
        } catch (InvalidCipherKeyException e) {
            System.out.println("(!) Key Error: " + e.getMessage());
            return null;
        }
    }

    private void ensureActiveCipher() {
        if (currentCipher == null) {
            try {
                currentCipher = new CaesarCipher(3);
            } catch (InvalidCipherKeyException ignored) {
            }
        }
    }

    /**
     * Safely reads an integer from console input without throwing InputMismatchException.
     */
    private int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("(!) Invalid input. Please enter a valid integer.");
            }
        }
    }
}
