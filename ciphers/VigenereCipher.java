package ciphers;

import utils.TextUtils;

public class VigenereCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        String key = validateKey(params.getString("key"));
        return apply(text, key, true);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        String key = validateKey(params.getString("key"));
        return apply(text, key, false);
    }

    private String apply(String text, String key, boolean encrypt) {
        String upper = text.toUpperCase();
        StringBuilder out = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                if (!encrypt) {
                    shift = -shift;
                }
                int value = (c - 'A' + shift + 26) % 26;
                out.append((char) ('A' + value));
                keyIndex++;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String validateKey(String key) {
        if (!TextUtils.isLettersOnly(key)) {
            throw new IllegalArgumentException("Vigenere key must contain letters only.");
        }
        return key.toUpperCase();
    }
}
