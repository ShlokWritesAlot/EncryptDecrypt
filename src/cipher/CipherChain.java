package cipher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CipherChain aggregates multiple {@link Cipher} objects into an ordered pipeline.
 *
 * OOP Concepts Demonstrated:
 * - Interface Implementation: Implements {@link Cipher}, so a chain can be treated
 *   identically to a single cipher (Composite Design Pattern).
 * - Composition (HAS-A): CipherChain contains a {@code List<Cipher>} as an internal component.
 * - Polymorphism: Each cipher in the list can be any concrete type implementing {@link Cipher}.
 */
public class CipherChain implements Cipher {

    // Composition: CipherChain has-a List of Cipher references
    private final List<Cipher> ciphers;

    /**
     * Default constructor creating an empty chain.
     */
    public CipherChain() {
        this.ciphers = new ArrayList<>();
    }

    /**
     * Parameterized constructor initializing the chain with existing ciphers.
     *
     * @param initialCiphers initial list of ciphers to include in the chain
     */
    public CipherChain(List<Cipher> initialCiphers) {
        this.ciphers = new ArrayList<>();
        if (initialCiphers != null) {
            for (Cipher c : initialCiphers) {
                addCipher(c);
            }
        }
    }

    /**
     * Adds a cipher to the end of the chain.
     *
     * @param c the Cipher to add
     * @throws IllegalArgumentException if the cipher is null or if attempting to add
     *                                  the chain to itself (direct self-reference loop)
     */
    public void addCipher(Cipher c) {
        if (c == null) {
            throw new IllegalArgumentException("Cannot add a null cipher to CipherChain.");
        }
        if (c == this) {
            throw new IllegalArgumentException("Cannot add a CipherChain to itself.");
        }
        this.ciphers.add(c);
    }

    /**
     * Returns an unmodifiable view of the ciphers in this chain.
     *
     * @return unmodifiable list of ciphers
     */
    public List<Cipher> getCiphers() {
        return Collections.unmodifiableList(ciphers);
    }

    /**
     * Encrypts data by applying each cipher in FORWARD sequence:
     * C1 -> C2 -> ... -> Cn
     *
     * @param data initial byte array
     * @return fully encrypted byte array
     */
    @Override
    public byte[] encrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] current = data;
        for (Cipher cipher : ciphers) {
            current = cipher.encrypt(current);
        }
        return current;
    }

    /**
     * Decrypts data by applying decryption in REVERSE order:
     * Cn^-1 -> ... -> C2^-1 -> C1^-1
     *
     * @param data encrypted byte array
     * @return fully decrypted byte array
     */
    @Override
    public byte[] decrypt(byte[] data) {
        if (data == null) {
            return null;
        }

        byte[] current = data;
        // Traverse the chain in reverse order (from last cipher down to first)
        for (int i = ciphers.size() - 1; i >= 0; i--) {
            current = ciphers.get(i).decrypt(current);
        }
        return current;
    }

    /**
     * Returns a string representation of the chain, e.g.:
     * "Caesar(3) -> Vigenere(KEY) -> Reverse"
     *
     * @return formatted chain string, or "(empty chain)" if no ciphers added
     */
    @Override
    public String toString() {
        if (ciphers.isEmpty()) {
            return "(empty chain)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciphers.size(); i++) {
            sb.append(ciphers.get(i).toString());
            if (i < ciphers.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
