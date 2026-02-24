package ciphers;

import utils.TextUtils;

public class RowTranspositionCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        int[] order = getOrder(params);
        int cols = order.length;
        int rows = (int) Math.ceil((double) text.length() / cols);
        char[][] grid = new char[rows][cols];

        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = idx < text.length() ? text.charAt(idx++) : 'X';
            }
        }

        StringBuilder out = new StringBuilder();
        for (int rank = 1; rank <= cols; rank++) {
            int col = findColumnByRank(order, rank);
            for (int r = 0; r < rows; r++) {
                out.append(grid[r][col]);
            }
        }
        return out.toString();
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int[] order = getOrder(params);
        int cols = order.length;
        if (text.length() % cols != 0) {
            throw new IllegalArgumentException("Ciphertext length must be divisible by key length for row transposition decryption.");
        }
        int rows = text.length() / cols;
        char[][] grid = new char[rows][cols];

        int idx = 0;
        for (int rank = 1; rank <= cols; rank++) {
            int col = findColumnByRank(order, rank);
            for (int r = 0; r < rows; r++) {
                grid[r][col] = text.charAt(idx++);
            }
        }

        StringBuilder out = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                out.append(grid[r][c]);
            }
        }
        return out.toString().replaceAll("X+$", "");
    }

    private int[] getOrder(KeyParams params) {
        Object key = params.get("order");
        if (key instanceof int[]) {
            return (int[]) key;
        }
        return TextUtils.parseNumericKeyOrder(params.getString("order"));
    }

    private int findColumnByRank(int[] order, int rank) {
        for (int i = 0; i < order.length; i++) {
            if (order[i] == rank) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid row transposition key order.");
    }
}
