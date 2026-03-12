package ciphers;

import utils.TextUtils;

public class VigenereSimpleCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        return apply(text, params.getString("key"), true);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        return apply(text, params.getString("key"), false);
    }

    private String apply(String text, String keyText, boolean encrypt) {
        String key = TextUtils.lettersOnly(keyText);
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Vigenere key is required (letters only).");
        }

        String input = text.toUpperCase();
        StringBuilder out = new StringBuilder();
        int keyIndex = 0;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                int k = key.charAt(keyIndex % key.length()) - 'A';
                int shift = encrypt ? k : -k;
                int val = (ch - 'A' + shift + 26) % 26;
                out.append((char) ('A' + val));
                keyIndex++;
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }
}
