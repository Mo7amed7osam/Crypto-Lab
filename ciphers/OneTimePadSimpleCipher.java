package ciphers;

import utils.TextUtils;

public class OneTimePadSimpleCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        String input = text.toUpperCase();
        String key = TextUtils.lettersOnly(params.getString("key"));

        int lettersCount = countLetters(input);
        if (key.length() != lettersCount) {
            throw new IllegalArgumentException("OTP key length must equal text letters count: " + lettersCount);
        }

        StringBuilder out = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                int k = key.charAt(keyIndex) - 'A';
                int val = (ch - 'A' + k) % 26;
                out.append((char) ('A' + val));
                keyIndex++;
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        throw new UnsupportedOperationException("OTP decrypt is not included in this simple implementation.");
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
