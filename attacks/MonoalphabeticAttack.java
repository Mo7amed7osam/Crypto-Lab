package attacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import utils.TextUtils;

public class MonoalphabeticAttack {
    private static final String EN_FREQ_ORDER = "ETAOINSHRDLCUMWFGYPBVKJXQZ";

    public List<AttackResult> run(String cipherText, int maxCandidates) {
        String clean = TextUtils.lettersOnly(cipherText);
        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext is empty after normalization.");
        }

        int[] counts = new int[26];
        for (int i = 0; i < clean.length(); i++) {
            counts[clean.charAt(i) - 'A']++;
        }

        Integer[] idx = new Integer[26];
        for (int i = 0; i < 26; i++) {
            idx[i] = i;
        }
        Arrays.sort(idx, Comparator.comparingInt((Integer i) -> counts[i]).reversed());

        List<AttackResult> results = new ArrayList<>();
        int limit = Math.max(1, Math.min(maxCandidates, 10));
        for (int offset = 0; offset < limit; offset++) {
            char[] cipherToPlain = new char[26];
            Arrays.fill(cipherToPlain, '?');

            for (int rank = 0; rank < 26; rank++) {
                int cipherIndex = idx[rank];
                char guessedPlain = EN_FREQ_ORDER.charAt((rank + offset) % 26);
                cipherToPlain[cipherIndex] = guessedPlain;
            }

            String preview = decryptWithMap(clean, cipherToPlain);
            double score = scoreByVowelRatio(preview);
            String mapping = mappingPreview(cipherToPlain);
            results.add(new AttackResult(mapping, String.format("%.3f", score), preview.substring(0, Math.min(120, preview.length()))));
        }
        return results;
    }

    private String decryptWithMap(String text, char[] cipherToPlain) {
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            out.append(cipherToPlain[c - 'A']);
        }
        return out.toString();
    }

    private String mappingPreview(char[] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append((char) ('A' + i)).append("->").append(map[i]);
        }
        return sb.toString();
    }

    private double scoreByVowelRatio(String text) {
        int vowels = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                vowels++;
            }
        }
        double ratio = (double) vowels / Math.max(1, text.length());
        return 1.0 - Math.abs(0.38 - ratio);
    }
}
