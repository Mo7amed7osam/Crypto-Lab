package attacks;

import java.util.ArrayList;
import java.util.List;

import ciphers.CaesarSwingCipher;
import ciphers.KeyParams;

public class CaesarAttackHelper {
    public List<AttackResult> bruteForce(String cipherText) {
        String input = cipherText == null ? "" : cipherText;
        if (input.trim().isEmpty()) {
            throw new IllegalArgumentException("Ciphertext is empty.");
        }

        CaesarSwingCipher cipher = new CaesarSwingCipher();
        List<AttackResult> results = new ArrayList<AttackResult>();
        for (int shift = 0; shift < 26; shift++) {
            String preview = cipher.decrypt(input, new KeyParams().put("shift", shift));
            results.add(new AttackResult("Shift " + shift, "BRUTE_FORCE", preview));
        }
        return results;
    }
}
