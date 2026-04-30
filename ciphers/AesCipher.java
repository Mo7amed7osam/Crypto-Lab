package ciphers;

import java.nio.charset.StandardCharsets;

public class AesCipher implements Cipher {
    // AES S-Box in 16x16 form
    private static final int[][] S_BOX = {
            {0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76},
            {0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0},
            {0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15},
            {0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75},
            {0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84},
            {0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF},
            {0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8},
            {0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2},
            {0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73},
            {0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB},
            {0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79},
            {0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08},
            {0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A},
            {0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E},
            {0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF},
            {0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16}
    };

    private static final int[] RCON = {
            0x00, 0x01, 0x02, 0x04, 0x08, 0x10,
            0x20, 0x40, 0x80, 0x1B, 0x36
    };

    @Override
    public String encrypt(String text, KeyParams params) {
        int[] keyBytes = readKey(params);
        int[][] words = expandKey(keyBytes);

        byte[] inputBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] padded = addPadding(inputBytes);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < padded.length; i += 16) {
            int[] block = new int[16];
            for (int j = 0; j < 16; j++) {
                block[j] = padded[i + j] & 0xFF;
            }

            int[] encrypted = encryptBlock(block, words);
            for (int value : encrypted) {
                out.append(toHex(value));
            }
        }

        return out.toString();
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int[] keyBytes = readKey(params);
        int[][] words = expandKey(keyBytes);
        int[] cipherBytes = hexToBytes(text);

        if (cipherBytes.length == 0) {
            return "";
        }
        if (cipherBytes.length % 16 != 0) {
            throw new IllegalArgumentException("AES ciphertext must be full 16-byte blocks in hex.");
        }

        byte[] plainBytes = new byte[cipherBytes.length];

        for (int i = 0; i < cipherBytes.length; i += 16) {
            int[] block = new int[16];
            for (int j = 0; j < 16; j++) {
                block[j] = cipherBytes[i + j];
            }

            int[] decrypted = decryptBlock(block, words);
            for (int j = 0; j < 16; j++) {
                plainBytes[i + j] = (byte) decrypted[j];
            }
        }

        return new String(removePadding(plainBytes), StandardCharsets.UTF_8);
    }

    private int[] readKey(KeyParams params) {
        String key = params.getString("key");
        if (key == null) {
            throw new IllegalArgumentException("AES key is required.");
        }
        if (key.length() != 16) {
            throw new IllegalArgumentException("AES-128 key must be exactly 16 characters.");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("AES key must use simple 1-byte characters only.");
        }

        int[] result = new int[16];
        for (int i = 0; i < 16; i++) {
            result[i] = keyBytes[i] & 0xFF;
        }
        return result;
    }

    private int[][] expandKey(int[] keyBytes) {
        int[][] words = new int[44][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                words[i][j] = keyBytes[i * 4 + j];
            }
        }

        for (int i = 4; i < 44; i++) {
            int[] temp = copyWord(words[i - 1]);

            if (i % 4 == 0) {
                temp = subWord(rotWord(temp));
                temp[0] = temp[0] ^ RCON[i / 4];
            }

            for (int j = 0; j < 4; j++) {
                words[i][j] = words[i - 4][j] ^ temp[j];
            }
        }

        return words;
    }

    private int[] encryptBlock(int[] block, int[][] words) {
        int[][] state = bytesToState(block);

        addRoundKey(state, words, 0);

        for (int round = 1; round <= 9; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, words, round);
        }

        subBytes(state);
        shiftRows(state);
        addRoundKey(state, words, 10);

        return stateToBytes(state);
    }

    private int[] decryptBlock(int[] block, int[][] words) {
        int[][] state = bytesToState(block);

        addRoundKey(state, words, 10);

        for (int round = 9; round >= 1; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, words, round);
            invMixColumns(state);
        }

        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, words, 0);

        return stateToBytes(state);
    }

    private void subBytes(int[][] state) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                state[row][col] = sBoxValue(state[row][col]);
            }
        }
    }

    private void invSubBytes(int[][] state) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                state[row][col] = inverseSBoxValue(state[row][col]);
            }
        }
    }

    private void shiftRows(int[][] state) {
        shiftRowLeft(state, 1, 1);
        shiftRowLeft(state, 2, 2);
        shiftRowLeft(state, 3, 3);
    }

    private void invShiftRows(int[][] state) {
        shiftRowRight(state, 1, 1);
        shiftRowRight(state, 2, 2);
        shiftRowRight(state, 3, 3);
    }

    private void mixColumns(int[][] state) {
        for (int col = 0; col < 4; col++) {
            int a0 = state[0][col];
            int a1 = state[1][col];
            int a2 = state[2][col];
            int a3 = state[3][col];

            state[0][col] = multiply(0x02, a0) ^ multiply(0x03, a1) ^ a2 ^ a3;
            state[1][col] = a0 ^ multiply(0x02, a1) ^ multiply(0x03, a2) ^ a3;
            state[2][col] = a0 ^ a1 ^ multiply(0x02, a2) ^ multiply(0x03, a3);
            state[3][col] = multiply(0x03, a0) ^ a1 ^ a2 ^ multiply(0x02, a3);
        }
    }

    private void invMixColumns(int[][] state) {
        for (int col = 0; col < 4; col++) {
            int a0 = state[0][col];
            int a1 = state[1][col];
            int a2 = state[2][col];
            int a3 = state[3][col];

            state[0][col] = multiply(0x0E, a0) ^ multiply(0x0B, a1) ^ multiply(0x0D, a2) ^ multiply(0x09, a3);
            state[1][col] = multiply(0x09, a0) ^ multiply(0x0E, a1) ^ multiply(0x0B, a2) ^ multiply(0x0D, a3);
            state[2][col] = multiply(0x0D, a0) ^ multiply(0x09, a1) ^ multiply(0x0E, a2) ^ multiply(0x0B, a3);
            state[3][col] = multiply(0x0B, a0) ^ multiply(0x0D, a1) ^ multiply(0x09, a2) ^ multiply(0x0E, a3);
        }
    }

    private void addRoundKey(int[][] state, int[][] words, int round) {
        int start = round * 4;
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                state[row][col] = state[row][col] ^ words[start + col][row];
            }
        }
    }

    private int[][] bytesToState(int[] block) {
        int[][] state = new int[4][4];
        for (int i = 0; i < 16; i++) {
            state[i % 4][i / 4] = block[i];
        }
        return state;
    }

    private int[] stateToBytes(int[][] state) {
        int[] block = new int[16];
        for (int i = 0; i < 16; i++) {
            block[i] = state[i % 4][i / 4];
        }
        return block;
    }

    private void shiftRowLeft(int[][] state, int row, int count) {
        for (int step = 0; step < count; step++) {
            int first = state[row][0];
            state[row][0] = state[row][1];
            state[row][1] = state[row][2];
            state[row][2] = state[row][3];
            state[row][3] = first;
        }
    }

    private void shiftRowRight(int[][] state, int row, int count) {
        for (int step = 0; step < count; step++) {
            int last = state[row][3];
            state[row][3] = state[row][2];
            state[row][2] = state[row][1];
            state[row][1] = state[row][0];
            state[row][0] = last;
        }
    }

    private int multiply(int a, int b) {
        int result = 0;
        int left = a;
        int right = b;

        for (int i = 0; i < 8; i++) {
            if ((right & 1) != 0) {
                result = result ^ left;
            }

            boolean highBit = (left & 0x80) != 0;
            left = (left << 1) & 0xFF;
            if (highBit) {
                left = left ^ 0x1B;
            }
            right = right >> 1;
        }

        return result & 0xFF;
    }

    private int[] hexToBytes(String text) {
        String hex = removeWhiteSpace(text);

        if (hex.isEmpty()) {
            return new int[0];
        }
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("AES hex input must contain an even number of characters.");
        }

        int[] bytes = new int[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int start = i * 2;
            String part = hex.substring(start, start + 2);

            if (!isHexPart(part)) {
                throw new IllegalArgumentException("AES ciphertext must contain only hex characters.");
            }

            bytes[i] = Integer.parseInt(part, 16);
        }
        return bytes;
    }

    private String removeWhiteSpace(String text) {
        StringBuilder clean = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                clean.append(ch);
            }
        }
        return clean.toString();
    }

    private boolean isHexPart(String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean digit = ch >= '0' && ch <= '9';
            boolean lower = ch >= 'a' && ch <= 'f';
            boolean upper = ch >= 'A' && ch <= 'F';
            if (!digit && !lower && !upper) {
                return false;
            }
        }
        return true;
    }

    private byte[] addPadding(byte[] input) {
        int padding = 16 - (input.length % 16);
        if (padding == 0) {
            padding = 16;
        }

        byte[] output = new byte[input.length + padding];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        for (int i = input.length; i < output.length; i++) {
            output[i] = (byte) padding;
        }
        return output;
    }

    private byte[] removePadding(byte[] input) {
        if (input.length == 0 || input.length % 16 != 0) {
            throw new IllegalArgumentException("AES decrypted data is not valid.");
        }

        int padding = input[input.length - 1] & 0xFF;
        if (padding < 1 || padding > 16) {
            throw new IllegalArgumentException("AES padding is invalid.");
        }

        for (int i = input.length - padding; i < input.length; i++) {
            if ((input[i] & 0xFF) != padding) {
                throw new IllegalArgumentException("AES padding is invalid.");
            }
        }

        byte[] output = new byte[input.length - padding];
        for (int i = 0; i < output.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private int[] copyWord(int[] word) {
        int[] copy = new int[4];
        for (int i = 0; i < 4; i++) {
            copy[i] = word[i];
        }
        return copy;
    }

    private int[] rotWord(int[] word) {
        return new int[]{word[1], word[2], word[3], word[0]};
    }

    private int[] subWord(int[] word) {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = sBoxValue(word[i]);
        }
        return result;
    }

    private int sBoxValue(int value) {
        return S_BOX[value / 16][value % 16];
    }

    private int inverseSBoxValue(int value) {
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                if (S_BOX[row][col] == value) {
                    return row * 16 + col;
                }
            }
        }
        throw new IllegalArgumentException("Byte not found in AES S-Box.");
    }

    private String toHex(int value) {
        String text = Integer.toHexString(value & 0xFF).toUpperCase();
        if (text.length() == 1) {
            return "0" + text;
        }
        return text;
    }
}
