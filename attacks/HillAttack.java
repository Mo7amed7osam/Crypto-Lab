package attacks;

import java.util.ArrayList;
import java.util.List;

import utils.MathUtils;
import utils.TextUtils;

public class HillAttack {
    public static class HillAttackResult {
        private final boolean success;
        private final String message;
        private final int[][] keyMatrix;

        public HillAttackResult(boolean success, String message, int[][] keyMatrix) {
            this.success = success;
            this.message = message;
            this.keyMatrix = keyMatrix;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int[][] getKeyMatrix() {
            return keyMatrix;
        }
    }

    public HillAttackResult knownPlaintextAttack(String knownPlaintext, String knownCiphertext, int n) {
        if (n < 2) {
            throw new IllegalArgumentException("Matrix size N must be >= 2.");
        }

        String p = TextUtils.lettersOnly(knownPlaintext);
        String c = TextUtils.lettersOnly(knownCiphertext);
        if (p.length() != c.length()) {
            return new HillAttackResult(false, "Plaintext and ciphertext chunks must have equal letter lengths.", null);
        }
        if (p.length() < n * n) {
            return new HillAttackResult(false, "Need at least N*N letters in both chunks.", null);
        }

        List<int[]> pBlocks = toBlocks(p, n);
        List<int[]> cBlocks = toBlocks(c, n);
        int blocks = Math.min(pBlocks.size(), cBlocks.size());

        for (int start = 0; start + n <= blocks; start++) {
            int[][] pMat = new int[n][n];
            int[][] cMat = new int[n][n];
            for (int col = 0; col < n; col++) {
                int[] pBlock = pBlocks.get(start + col);
                int[] cBlock = cBlocks.get(start + col);
                for (int row = 0; row < n; row++) {
                    pMat[row][col] = pBlock[row];
                    cMat[row][col] = cBlock[row];
                }
            }

            try {
                int[][] pInv = MathUtils.inverseMatrixMod(pMat, 26);
                int[][] key = MathUtils.multiplyMatricesMod(cMat, pInv, 26);
                if (validatesAll(key, pBlocks, cBlocks, blocks)) {
                    String msg = "Recovered Hill key matrix using blocks " + start + ".." + (start + n - 1);
                    return new HillAttackResult(true, msg, key);
                }
            } catch (IllegalArgumentException ignored) {
                // try next window
            }
        }

        return new HillAttackResult(false, "Could not derive a valid invertible key matrix from provided chunks.", null);
    }

    private List<int[]> toBlocks(String text, int n) {
        List<int[]> blocks = new ArrayList<>();
        int usable = text.length() - (text.length() % n);
        for (int i = 0; i < usable; i += n) {
            int[] block = new int[n];
            for (int j = 0; j < n; j++) {
                block[j] = text.charAt(i + j) - 'A';
            }
            blocks.add(block);
        }
        return blocks;
    }

    private boolean validatesAll(int[][] key, List<int[]> pBlocks, List<int[]> cBlocks, int blocks) {
        for (int i = 0; i < blocks; i++) {
            int[] enc = MathUtils.multiplyMatrixVectorMod(key, pBlocks.get(i), 26);
            int[] expected = cBlocks.get(i);
            for (int j = 0; j < enc.length; j++) {
                if (enc[j] != expected[j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
