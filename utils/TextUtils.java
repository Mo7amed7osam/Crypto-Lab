package utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TextUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    private TextUtils() {
    }

    public static String normalize(String text, boolean lettersOnly) {
        if (text == null) {
            return "";
        }
        String upper = text.toUpperCase();
        if (!lettersOnly) {
            return upper;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String lettersOnly(String text) {
        return normalize(text, true);
    }

    public static boolean isLettersOnly(String text) {
        return text != null && text.matches("[A-Za-z]+$");
    }

    public static int[] parseNumericKeyOrder(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Row transposition key is required.");
        }
        String[] parts = key.trim().split("\\s+");
        int[] order = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            order[i] = Integer.parseInt(parts[i]);
        }
        validatePermutation(order);
        return order;
    }

    public static void validatePermutation(int[] order) {
        if (order.length == 0) {
            throw new IllegalArgumentException("Key order cannot be empty.");
        }
        Set<Integer> values = new HashSet<>();
        for (int value : order) {
            if (value < 1 || value > order.length) {
                throw new IllegalArgumentException("Key order must contain values 1..n exactly once.");
            }
            values.add(value);
        }
        if (values.size() != order.length) {
            throw new IllegalArgumentException("Key order must not contain duplicates.");
        }
    }

    public static String generateRandomLetters(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative.");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + RANDOM.nextInt(26)));
        }
        return sb.toString();
    }

    public static String generateRandomSubstitutionMap() {
        List<Character> letters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letters.add(c);
        }
        for (int i = letters.size() - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = letters.get(i);
            letters.set(i, letters.get(j));
            letters.set(j, tmp);
        }
        StringBuilder sb = new StringBuilder(26);
        for (char c : letters) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void validateSubstitutionMap(String map) {
        if (map == null || map.length() != 26 || !map.matches("[A-Za-z]{26}")) {
            throw new IllegalArgumentException("Substitution map must be exactly 26 letters.");
        }
        String upper = map.toUpperCase();
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < upper.length(); i++) {
            set.add(upper.charAt(i));
        }
        if (set.size() != 26) {
            throw new IllegalArgumentException("Substitution map must contain 26 unique letters.");
        }
    }
}
