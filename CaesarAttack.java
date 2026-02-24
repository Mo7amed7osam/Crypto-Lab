public class CaesarAttack {

    public static void bruteForce(String cipherText) {

        for (int shift = 0; shift < 26; shift++) {

            String decrypted = CaesarCipher.decrypt(cipherText, shift);

            System.out.println("Shift " + shift + ": " + decrypted);
        }
    }

    public static void main(String[] args) {

        String cipherText = "KHOOR ZRUOG";

        bruteForce(cipherText);
    }
}
