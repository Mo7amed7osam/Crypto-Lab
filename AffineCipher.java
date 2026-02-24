import java.util.Scanner;
public class AffineCipher {
    public static String encrypt(String text, int k1,int k2) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isLetter(ch)) {

                if (Character.isUpperCase(ch)) {
                    char encrypted = (char) (((ch - 'A' * k1)+k2) % 26 + 'A');
                    result.append(encrypted);
                } else {
                    char encrypted = (char) (((ch - 'a' * k1)+k2) % 26 + 'a');
                    result.append(encrypted);
                }

            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static String decrypt(String text, int k1,int k2) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            s.append ((char) ((26 - k1) % 26 *ch-k2));
        }
        return s.toString();

    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter text: ");
        String text = sc.nextLine();

        System.out.print("Enter k1: ");
        int k1 = sc.nextInt();

        System.out.print("Enter k2: ");
        int k2 = sc.nextInt();

        String encrypted = encrypt(text, k1,k2);
        System.out.println("Encrypted: " + encrypted);

        String decrypted = decrypt(encrypted, k1 ,k2);
        System.out.println("Decrypted: " + decrypted);

        sc.close();

    }
}
