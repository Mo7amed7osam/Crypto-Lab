package ciphers;

import java.util.LinkedHashSet;
import java.util.Set;

import utils.TextUtils;

public class PlayfairCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        String key = params.getString("key");
        char[][] matrix = buildMatrix(key);
        String prepared = prepareForEncrypt(text);
        return transform(prepared, matrix, true);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        String key = params.getString("key");
        char[][] matrix = buildMatrix(key);
        String prepared = TextUtils.lettersOnly(text).replace('J', 'I');
        if (prepared.length() % 2 != 0) {
            prepared = prepared + 'X';
        }
        return transform(prepared, matrix, false);
    }

    private String transform(String text, char[][] matrix, boolean encrypt) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            int[] p1 = find(matrix, a);
            int[] p2 = find(matrix, b);

            if (p1[0] == p2[0]) {
                int shift = encrypt ? 1 : -1;
                out.append(matrix[p1[0]][(p1[1] + shift + 5) % 5]);
                out.append(matrix[p2[0]][(p2[1] + shift + 5) % 5]);
            } else if (p1[1] == p2[1]) {
                int shift = encrypt ? 1 : -1;
                out.append(matrix[(p1[0] + shift + 5) % 5][p1[1]]);
                out.append(matrix[(p2[0] + shift + 5) % 5][p2[1]]);
            } else {
                out.append(matrix[p1[0]][p2[1]]);
                out.append(matrix[p2[0]][p1[1]]);
            }
        }
        return out.toString();
    }

    private String prepareForEncrypt(String text) {
        String clean = TextUtils.lettersOnly(text).replace('J', 'I');
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < clean.length()) {
            char a = clean.charAt(i);
            char b = (i + 1 < clean.length()) ? clean.charAt(i + 1) : 'X';
            if (a == b) {
                sb.append(a).append('X');
                i++;
            } else {
                sb.append(a).append(b);
                i += 2;
            }
        }
        if (sb.length() % 2 != 0) {
            sb.append('X');
        }
        return sb.toString();
    }

    private char[][] buildMatrix(String keyInput) {
        String key = TextUtils.lettersOnly(keyInput).replace('J', 'I');
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Playfair keyword is required.");
        }

        Set<Character> set = new LinkedHashSet<>();
        for (int i = 0; i < key.length(); i++) {
            set.add(key.charAt(i));
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J') {
                set.add(c);
            }
        }

        char[][] matrix = new char[5][5];
        int idx = 0;
        for (char c : set) {
            matrix[idx / 5][idx % 5] = c;
            idx++;
            if (idx == 25) {
                break;
            }
        }
        return matrix;
    }

    private int[] find(char[][] matrix, char target) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (matrix[r][c] == target) {
                    return new int[]{r, c};
                }
            }
        }
        throw new IllegalArgumentException("Character not found in Playfair matrix.");
    }
}
