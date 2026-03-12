package ciphers;

public class RailFenceSimpleCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        int rails = getRails(params);
        if (rails <= 1 || text.length() <= 1) {
            return text;
        }

        StringBuilder[] lines = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            lines[i] = new StringBuilder();
        }

        int row = 0;
        int dir = 1;
        for (int i = 0; i < text.length(); i++) {
            lines[row].append(text.charAt(i));
            if (row == 0) {
                dir = 1;
            } else if (row == rails - 1) {
                dir = -1;
            }
            row += dir;
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < rails; i++) {
            out.append(lines[i]);
        }
        return out.toString();
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int rails = getRails(params);
        if (rails <= 1 || text.length() <= 1) {
            return text;
        }

        boolean[][] marks = new boolean[rails][text.length()];
        int row = 0;
        int dir = 1;
        for (int c = 0; c < text.length(); c++) {
            marks[row][c] = true;
            if (row == 0) {
                dir = 1;
            } else if (row == rails - 1) {
                dir = -1;
            }
            row += dir;
        }

        char[][] grid = new char[rails][text.length()];
        int idx = 0;
        for (int r = 0; r < rails; r++) {
            for (int c = 0; c < text.length(); c++) {
                if (marks[r][c]) {
                    grid[r][c] = text.charAt(idx++);
                }
            }
        }

        StringBuilder out = new StringBuilder();
        row = 0;
        dir = 1;
        for (int c = 0; c < text.length(); c++) {
            out.append(grid[row][c]);
            if (row == 0) {
                dir = 1;
            } else if (row == rails - 1) {
                dir = -1;
            }
            row += dir;
        }
        return out.toString();
    }

    private int getRails(KeyParams params) {
        Integer rails = params.getInt("rails");
        if (rails == null || rails < 2) {
            throw new IllegalArgumentException("Rails must be >= 2.");
        }
        return rails;
    }
}
