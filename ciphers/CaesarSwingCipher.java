package ciphers;

public class CaesarSwingCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        int shift = getShift(params);
        return apply(text, shift);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int shift = getShift(params);
        return apply(text, 26 - (shift % 26));
    }

    private int getShift(KeyParams params) {
        Integer shift = params.getInt("shift");
        if (shift == null) {
            throw new IllegalArgumentException("Caesar shift is required.");
        }
        if (shift < 0) {
            throw new IllegalArgumentException("Caesar shift must be >= 0.");
        }
        return shift % 26;
    }

    private String apply(String text, int shift) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                result.append((char) ('A' + (ch - 'A' + shift) % 26));
            } else if (ch >= 'a' && ch <= 'z') {
                result.append((char) ('a' + (ch - 'a' + shift) % 26));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
