package attacks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ciphers.KeyParams;
import ciphers.VigenereCipher;
import utils.TextUtils;

public class VigenereAttackHelper {
    private static final double[] ENGLISH_FREQ = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,
            0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749,
            0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
            0.00978, 0.02360, 0.00150, 0.01974, 0.00074
    };

    public List<AttackResult> suggestKeys(String cipherText, int keyLength, int maxResults) {
        if (keyLength < 1) {
            throw new IllegalArgumentException("Guessed key length must be >= 1.");
        }
        String clean = TextUtils.lettersOnly(cipherText);
        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext is empty after normalization.");
        }

        List<List<ShiftScore>> perColumn = new ArrayList<>();
        for (int col = 0; col < keyLength; col++) {
            StringBuilder sb = new StringBuilder();
            for (int i = col; i < clean.length(); i += keyLength) {
                sb.append(clean.charAt(i));
            }
            perColumn.add(bestShifts(sb.toString(), 3));
        }

        List<String> candidateKeys = new ArrayList<>();
        candidateKeys.add(buildKey(perColumn, 0));
        for (int i = 0; i < keyLength && candidateKeys.size() < maxResults; i++) {
            if (perColumn.get(i).size() > 1) {
                candidateKeys.add(buildKeyWithOneAlt(perColumn, i, 1));
            }
            if (perColumn.get(i).size() > 2 && candidateKeys.size() < maxResults) {
                candidateKeys.add(buildKeyWithOneAlt(perColumn, i, 2));
            }
        }

        VigenereCipher cipher = new VigenereCipher();
        List<AttackResult> results = new ArrayList<>();
        Set<String> unique = new LinkedHashSet<String>(candidateKeys);
        int added = 0;
        for (String key : unique) {
            if (added >= maxResults) {
                break;
            }
            String preview = cipher.decrypt(clean, new KeyParams().put("key", key));
            double score = languageScore(preview);
            results.add(new AttackResult(key, String.format("%.3f", score), preview.substring(0, Math.min(120, preview.length()))));
            added++;
        }
        results.sort(Comparator.comparing(AttackResult::getScore).reversed());
        return results;
    }

    private List<ShiftScore> bestShifts(String columnText, int top) {
        List<ShiftScore> scores = new ArrayList<>();
        for (int shift = 0; shift < 26; shift++) {
            double chi = chiSquare(columnText, shift);
            scores.add(new ShiftScore(shift, chi));
        }
        scores.sort(Comparator.comparingDouble(ShiftScore::chi));
        return scores.subList(0, Math.min(top, scores.size()));
    }

    private double chiSquare(String text, int decryptShift) {
        int[] counts = new int[26];
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) - 'A';
            int p = (c - decryptShift + 26) % 26;
            counts[p]++;
        }

        double chi = 0;
        int len = Math.max(1, text.length());
        for (int i = 0; i < 26; i++) {
            double expected = ENGLISH_FREQ[i] * len;
            double diff = counts[i] - expected;
            chi += (diff * diff) / Math.max(1e-9, expected);
        }
        return chi;
    }

    private String buildKey(List<List<ShiftScore>> perColumn, int rank) {
        StringBuilder key = new StringBuilder();
        for (List<ShiftScore> scores : perColumn) {
            key.append((char) ('A' + scores.get(Math.min(rank, scores.size() - 1)).shift()));
        }
        return key.toString();
    }

    private String buildKeyWithOneAlt(List<List<ShiftScore>> perColumn, int altCol, int altRank) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < perColumn.size(); i++) {
            int rank = (i == altCol) ? altRank : 0;
            List<ShiftScore> scores = perColumn.get(i);
            key.append((char) ('A' + scores.get(Math.min(rank, scores.size() - 1)).shift()));
        }
        return key.toString();
    }

    private double languageScore(String text) {
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

    private static class ShiftScore {
        private final int shift;
        private final double chi;

        ShiftScore(int shift, double chi) {
            this.shift = shift;
            this.chi = chi;
        }

        int shift() {
            return shift;
        }

        double chi() {
            return chi;
        }
    }
}
