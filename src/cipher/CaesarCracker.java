package cipher;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CaesarCracker performs a brute-force attack on a ciphertext byte array
 * encrypted with a Caesar cipher.
 *
 * It tries all 255 non-zero shifts, measures ASCII English character plausibility,
 * and returns ranked candidates.
 */
public class CaesarCracker {

    public record CrackResult(int shift, String sampleDecrypted, double score) {}

    /**
     * Attempts all 255 shifts and returns the top N most likely plaintext matches.
     *
     * @param ciphertext input byte array
     * @param topN number of best candidates to return
     * @return list of CrackResult ordered from highest score to lowest
     */
    public static List<CrackResult> crack(byte[] ciphertext, int topN) {
        if (ciphertext == null || ciphertext.length == 0) {
            return List.of();
        }

        List<CrackResult> results = new ArrayList<>();

        for (int shift = 1; shift < 256; shift++) {
            try {
                CaesarCipher cipher = new CaesarCipher(shift);
                byte[] decrypted = cipher.decrypt(ciphertext);

                double score = scorePlaintext(decrypted);
                String preview = new String(decrypted, 0, Math.min(decrypted.length, 60), StandardCharsets.UTF_8)
                        .replaceAll("[\\r\\n]", " ");

                results.add(new CrackResult(shift, preview, score));
            } catch (InvalidCipherKeyException ignored) {
            }
        }

        results.sort(Comparator.comparingDouble(CrackResult::score).reversed());
        return results.subList(0, Math.min(topN, results.size()));
    }

    /**
     * Scores byte array based on percentage of printable ASCII letters, space, and common punctuation.
     */
    private static double scorePlaintext(byte[] data) {
        int printableLetters = 0;
        int spaces = 0;
        int nonPrintable = 0;

        for (byte b : data) {
            int val = b & 0xFF;
            if ((val >= 'A' && val <= 'Z') || (val >= 'a' && val <= 'z')) {
                printableLetters++;
            } else if (val == ' ') {
                spaces++;
            } else if (val == '\n' || val == '\r' || val == '\t' || val == '.' || val == ',' || val == '!') {
                printableLetters++;
            } else if (val < 32 || val > 126) {
                nonPrintable += 3; // penalize non-printable binary bytes
            }
        }

        return (printableLetters * 1.5 + spaces * 2.0) - nonPrintable;
    }
}
