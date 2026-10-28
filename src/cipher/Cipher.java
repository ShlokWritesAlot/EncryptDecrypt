package cipher;

/**
 * The core Cipher interface that defines the contract for encryption
 * and decryption operations on raw byte arrays.
 *
 * Demonstrates:
 * - Abstraction: Exposes high-level operations without revealing underlying implementation.
 * - Interface & Polymorphism: Allows any cipher implementation to be used interchangeably.
 */
public interface Cipher {

    /**
     * Encrypts the provided raw byte array.
     *
     * @param data the byte array to encrypt
     * @return a new byte array containing the encrypted data
     */
    byte[] encrypt(byte[] data);

    /**
     * Decrypts the provided raw byte array.
     *
     * @param data the byte array to decrypt
     * @return a new byte array containing the decrypted data
     */
    byte[] decrypt(byte[] data);
}
