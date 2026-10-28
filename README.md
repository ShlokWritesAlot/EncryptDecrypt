# 🔐 Cipher Lab — Java OOP Cryptography Project

A modular, extensible Java console project designed to demonstrate core Object-Oriented Programming (OOP) concepts, raw byte-stream I/O, custom exception hierarchies, and cryptographic pipelines.

---

## 🌟 Key Features

- **Raw Byte-Level Processing:** All ciphers operate on `byte[]` directly (modulo 256), making them binary-safe for any file type (images, compiled binaries, PDFs, text).
- **Composite Cipher Chaining:** Combine any number of ciphers in sequence (e.g., `Caesar(7) -> Vigenere(SECRET) -> Reverse`).
  - **Encryption** applies ciphers forward ($C_1 \to C_2 \to \dots \to C_n$).
  - **Decryption** automatically reverses the order ($C_n^{-1} \to \dots \to C_2^{-1} \to C_1^{-1}$).
- **Stream-Based File Processor:** Encrypts and decrypts files using `FileInputStream` and `FileOutputStream` with guaranteed resource closing via `try-with-resources`.
- **Benchmarking & Sizing Reports:** Measures file size, elapsed processing time, and throughput in KB/s.
- **Chain Persistence:** Save and load your multi-cipher chain configurations to/from `.cfg` files.
- **Caesar Brute-Force Cracker:** Analyzes ciphertext samples by testing all 255 shifts and scoring candidates using an English frequency heuristic.
- **Crash-Proof Interactive CLI:** Console menu built with `Scanner` and comprehensive input validation to prevent crashes.

---

## 🏛️ OOP Concepts Demonstrated

| OOP Concept | Implementation in Cipher Lab |
| :--- | :--- |
| **Interface & Abstraction** | The `Cipher` interface defines the contract (`encrypt(byte[])` and `decrypt(byte[])`) without exposing algorithm internals. |
| **Polymorphism** | `FileProcessor` and `CipherChain` process any object implementing `Cipher` identically via dynamic method dispatch. |
| **Composition** | `CipherChain` **has-a** `List<Cipher>` (Composite Pattern), delegating operations through the list. |
| **Encapsulation** | Cipher keys and shifts are `private final` fields, validated at initialization, with no unauthorized mutation. |
| **Exception Handling** | Custom checked exceptions (`InvalidCipherKeyException`, `FileProcessingException`) with root-cause preservation and exception chaining. |

---

## 📁 Project Structure

```
src/cipher/
 ├── Cipher.java                     # Core interface for all ciphers
 ├── CaesarCipher.java               # Shift cipher (modulo 256)
 ├── VigenereCipher.java             # Polyalphabetic byte substitution
 ├── ReverseCipher.java              # Keyless byte-inversion cipher
 ├── CipherChain.java                # Composite pattern container (List<Cipher>)
 ├── FileProcessor.java              # Byte-stream I/O with FileInputStream/FileOutputStream
 ├── ProcessingReport.java           # Sizing and throughput performance report
 ├── ChainFileManager.java          # Serialization/deserialization for chain configs
 ├── CaesarCracker.java              # Frequency-based brute-force cracker
 ├── InvalidCipherKeyException.java  # Custom checked exception for key validation
 ├── FileProcessingException.java    # Custom checked exception for stream/file errors
 ├── Main.java                       # Interactive console application
 ├── PolymorphismDemo.java           # Standalone test demonstrating runtime polymorphism
 └── FileProcessorTest.java          # End-to-end file encryption/decryption test
```

---

## 🚀 Quick Start

### 1. Compile

From the project root:

```bash
javac -d bin src/cipher/*.java
```

### 2. Run the Interactive App

```bash
java -cp bin cipher.Main
```

### 3. Run Standalone OOP Tests

**Runtime Polymorphism Demo:**
```bash
java -cp bin cipher.PolymorphismDemo
```

**Composite CipherChain Demo:**
```bash
java -cp bin cipher.CipherChainDemo
```

**File Processor & Stream Test:**
```bash
java -cp bin cipher.FileProcessorTest
```

---

## 📋 Interactive Menu Options

```
==================================================
            WELCOME TO CIPHER LAB                
==================================================
1. Encrypt a message (typed in)
2. Decrypt a message (typed in)
3. Encrypt a file (with Benchmark Report)
4. Decrypt a file (with Benchmark Report)
5. Configure Cipher / Build a Cipher Chain
6. Save / Load Cipher Chain (File Config)
7. Brute-Force Crack Caesar Cipher
8. Exit
```

---

## 📄 License
This project is part of coursework for Object-Oriented Programming (OOP).
