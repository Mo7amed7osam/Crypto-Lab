package ciphers;

import utils.TextUtils;

public class OneTimePadCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        return apply(text, params.getString("key"), true);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        return apply(text, params.getString("key"), false);
    }

    private String apply(String text, String keyInput, boolean encrypt) {
        String upper = text.toUpperCase();
        String key = TextUtils.lettersOnly(keyInput == null ? "" : keyInput);
        int letterCount = countLetters(upper);
        if (key.length() != letterCount) {
            throw new IllegalArgumentException("OTP key length must match text letters count exactly (" + letterCount + ").");
        }

        StringBuilder out = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int k = key.charAt(keyIndex) - 'A';
                int val = encrypt ? (c - 'A' + k) % 26 : (c - 'A' - k + 26) % 26;
                out.append((char) ('A' + val));
                keyIndex++;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private int countLetters(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                count++;
            }
        }
        return count;
    }
}
