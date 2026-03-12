package ciphers;

public class RowTranspositionSimpleCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        int[] order = parseOrder(params.getString("order"));
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
            int col = findCol(order, rank);
            for (int r = 0; r < rows; r++) {
                out.append(grid[r][col]);
            }
        }
        return out.toString();
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int[] order = parseOrder(params.getString("order"));
        int cols = order.length;
        if (text.length() % cols != 0) {
            throw new IllegalArgumentException("Cipher text length must be divisible by key size.");
        }
        int rows = text.length() / cols;

        char[][] grid = new char[rows][cols];
        int idx = 0;
        for (int rank = 1; rank <= cols; rank++) {
            int col = findCol(order, rank);
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

        while (out.length() > 0 && out.charAt(out.length() - 1) == 'X') {
            out.deleteCharAt(out.length() - 1);
        }
        return out.toString();
    }

    private int[] parseOrder(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Row order is required. Example: 3 1 4 2");
        }
        String[] parts = text.trim().split("\\s+");
        int[] order = new int[parts.length];

        boolean[] used = new boolean[parts.length + 1];
        for (int i = 0; i < parts.length; i++) {
            int v = Integer.parseInt(parts[i]);
            if (v < 1 || v > parts.length || used[v]) {
                throw new IllegalArgumentException("Row order must be numbers 1..n without duplicates.");
            }
            used[v] = true;
            order[i] = v;
        }
        return order;
    }

    private int findCol(int[] order, int rank) {
        for (int i = 0; i < order.length; i++) {
            if (order[i] == rank) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid row order.");
    }
}
