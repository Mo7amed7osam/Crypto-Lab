import java.util.Scanner;

public class CaesarCipher {

    public static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isLetter(ch)) {

                if (Character.isUpperCase(ch)) {
                    char encrypted = (char) ((ch - 'A' + shift) % 26 + 'A');
                    result.append(encrypted);
                } else {
                    char encrypted = (char) ((ch - 'a' + shift) % 26 + 'a');
                    result.append(encrypted);
                }

            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static String decrypt(String text, int shift) {
        return encrypt(text, 26 - shift);
    }



        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter text: ");
            String text = sc.nextLine();

            System.out.print("Enter shift value: ");
            int shift = sc.nextInt();

            String encrypted = encrypt(text, shift);
            System.out.println("Encrypted: " + encrypted);

            String decrypted = decrypt(encrypted, shift);
            System.out.println("Decrypted: " + decrypted);

            sc.close();

    }
}
