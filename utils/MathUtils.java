package utils;

public final class MathUtils {
    private MathUtils() {
    }

    public static int mod(int a, int m) {
        int r = a % m;
        return r < 0 ? r + m : r;
    }

    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    public static int modInverse(int a, int m) {
        int t = 0;
        int newT = 1;
        int r = m;
        int newR = mod(a, m);

        while (newR != 0) {
            int q = r / newR;
            int tempT = t - q * newT;
            t = newT;
            newT = tempT;

            int tempR = r - q * newR;
            r = newR;
            newR = tempR;
        }

        if (r > 1) {
            throw new IllegalArgumentException("Value has no modular inverse under mod " + m);
        }
        if (t < 0) {
            t += m;
        }
        return t;
    }

    public static int[][] multiplyMatricesMod(int[][] a, int[][] b, int mod) {
        int rows = a.length;
        int cols = b[0].length;
        int common = b.length;
        int[][] result = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int sum = 0;
                for (int k = 0; k < common; k++) {
                    sum += a[r][k] * b[k][c];
                }
                result[r][c] = mod(sum, mod);
            }
        }
        return result;
    }

    public static int[] multiplyMatrixVectorMod(int[][] matrix, int[] vector, int mod) {
        int[] result = new int[matrix.length];
        for (int r = 0; r < matrix.length; r++) {
            int sum = 0;
            for (int c = 0; c < matrix[0].length; c++) {
                sum += matrix[r][c] * vector[c];
            }
            result[r] = mod(sum, mod);
        }
        return result;
    }

    public static int determinant(int[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        int det = 0;
        for (int col = 0; col < n; col++) {
            int sign = (col % 2 == 0) ? 1 : -1;
            det += sign * matrix[0][col] * determinant(minor(matrix, 0, col));
        }
        return det;
    }

    public static int[][] inverseMatrixMod(int[][] matrix, int mod) {
        int n = matrix.length;
        if (n == 0 || n != matrix[0].length) {
            throw new IllegalArgumentException("Matrix must be square.");
        }

        int det = mod(determinant(matrix), mod);
        int detInv = modInverse(det, mod);

        int[][] cofactors = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int sign = ((r + c) % 2 == 0) ? 1 : -1;
                cofactors[r][c] = sign * determinant(minor(matrix, r, c));
            }
        }

        int[][] adjugate = transpose(cofactors);
        int[][] inverse = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                inverse[r][c] = mod(adjugate[r][c] * detInv, mod);
            }
        }
        return inverse;
    }

    public static int[][] parseSquareMatrix(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrix key is required.");
        }
        String[] rows = text.trim().split(";");
        int n = rows.length;
        int[][] matrix = new int[n][n];
        for (int r = 0; r < n; r++) {
            String[] parts = rows[r].trim().split("\\s+");
            if (parts.length != n) {
                throw new IllegalArgumentException("Matrix must be square. Example: 3 3; 2 5");
            }
            for (int c = 0; c < n; c++) {
                matrix[r][c] = mod(Integer.parseInt(parts[c]), 26);
            }
        }
        return matrix;
    }

    public static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                if (c > 0) {
                    sb.append(' ');
                }
                sb.append(matrix[r][c]);
            }
            if (r < matrix.length - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    private static int[][] minor(int[][] matrix, int rowToRemove, int colToRemove) {
        int n = matrix.length;
        int[][] result = new int[n - 1][n - 1];
        int rIndex = 0;
        for (int r = 0; r < n; r++) {
            if (r == rowToRemove) {
                continue;
            }
            int cIndex = 0;
            for (int c = 0; c < n; c++) {
                if (c == colToRemove) {
                    continue;
                }
                result[rIndex][cIndex] = matrix[r][c];
                cIndex++;
            }
            rIndex++;
        }
        return result;
    }

    private static int[][] transpose(int[][] matrix) {
        int[][] result = new int[matrix[0].length][matrix.length];
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                result[c][r] = matrix[r][c];
            }
        }
        return result;
    }
}
