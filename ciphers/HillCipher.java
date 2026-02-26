package ciphers;

import utils.MathUtils;
import utils.TextUtils;

public class HillCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        int[][] key = getMatrix(params);
        validateKey(key);
        String input = TextUtils.lettersOnly(text);
        return process(input, key);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        int[][] key = getMatrix(params);
        validateKey(key);
        int[][] inverse = MathUtils.inverseMatrixMod(key, 26);
        printMatrix("Hill inverse matrix (mod 26):", inverse);
        String input = TextUtils.lettersOnly(text);
        return process(input, inverse);
    }

    private int[][] getMatrix(KeyParams params) {
        int[][] matrix = params.getMatrix("matrix");
        if (matrix == null) {
            throw new IllegalArgumentException("Hill matrix key is required.");
        }
        return matrix;
    }

    private void validateKey(int[][] matrix) {
        int n = matrix.length;
        if (n < 2 || n != matrix[0].length) {
            throw new IllegalArgumentException("Hill key must be a square matrix of size >= 2.");
        }
        int det = MathUtils.mod(MathUtils.determinant(matrix), 26);
        if (MathUtils.gcd(det, 26) != 1) {
            throw new IllegalArgumentException("Hill matrix is not invertible mod 26.");
        }
    }

    private String process(String text, int[][] matrix) {
        int n = matrix.length;
        StringBuilder input = new StringBuilder(text);
        while (input.length() % n != 0) {
            input.append('X');
        }

        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i += n) {
            int[] block = new int[n];
            for (int j = 0; j < n; j++) {
                block[j] = input.charAt(i + j) - 'A';
            }
            int[] result = MathUtils.multiplyMatrixVectorMod(matrix, block, 26);
            for (int value : result) {
                out.append((char) ('A' + value));
            }
        }
        return out.toString();
    }

    private void printMatrix(String title, int[][] matrix) {
        System.out.println(title);
        for (int r = 0; r < matrix.length; r++) {
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < matrix[r].length; c++) {
                if (c > 0) {
                    row.append(' ');
                }
                row.append(matrix[r][c]);}
            System.out.println(row);
        }
        System.out.println();
    }
}
