import java.io.IOException;
import java.util.Scanner;

import ciphers.Aes128KeyExpansion;
import utils.FileUtils;

public class AesKeyExpansionProgram {
    private static final int FORMAT_TEXT = 1;
    private static final int FORMAT_HEX = 2;
    private static final int FORMAT_BINARY = 3;

    private static final int SOURCE_CONSOLE = 1;
    private static final int SOURCE_FILE = 2;

    private static final int OUTPUT_CONSOLE = 1;
    private static final int OUTPUT_FILE = 2;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("AES-128 Key Expansion Program");
            System.out.println();

            int inputFormat = chooseInputFormat(scanner);
            int inputSource = chooseInputSource(scanner);
            String rawInput = readInput(scanner, inputFormat, inputSource);
            int[] keyBytes = convertToKeyBytes(rawInput, inputFormat);
            String result = Aes128KeyExpansion.expandKeyBytes(keyBytes);
            int outputMethod = chooseOutputMethod(scanner);

            if (outputMethod == OUTPUT_CONSOLE) {
                System.out.println();
                System.out.println(result);
            } else {
                saveOutputToFile(scanner, result);
            }
        } catch (Exception e) {
            System.out.println();
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static int chooseInputFormat(Scanner scanner) {
        System.out.println("Choose input format:");
        System.out.println("1. TEXT (16 chars)");
        System.out.println("2. HEX (32 hex chars)");
        System.out.println("3. BINARY (128 bits)");
        return readChoice(scanner, 1, 3);
    }

    private static int chooseInputSource(Scanner scanner) {
        System.out.println();
        System.out.println("Choose input source:");
        System.out.println("1. Read from console");
        System.out.println("2. Read from file");
        return readChoice(scanner, 1, 2);
    }

    private static int chooseOutputMethod(Scanner scanner) {
        System.out.println();
        System.out.println("Choose output method:");
        System.out.println("1. Print to console");
        System.out.println("2. Save to file");
        return readChoice(scanner, 1, 2);
    }

    private static int readChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Enter choice: ");
            String line = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(line);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // Keep asking until the user enters a valid number.
            }

            System.out.println("Invalid choice. Try again.");
        }
    }

    private static String readInput(Scanner scanner, int inputFormat, int inputSource) throws IOException {
        if (inputSource == SOURCE_CONSOLE) {
            return readInputFromConsole(scanner, inputFormat);
        }
        return readInputFromFile(scanner);
    }

    private static String readInputFromConsole(Scanner scanner, int inputFormat) {
        System.out.println();

        if (inputFormat == FORMAT_TEXT) {
            System.out.print("Enter 16 text characters: ");
        } else if (inputFormat == FORMAT_HEX) {
            System.out.print("Enter 32 hex characters: ");
        } else {
            System.out.print("Enter 128 binary bits: ");
        }

        return scanner.nextLine();
    }

    private static String readInputFromFile(Scanner scanner) throws IOException {
        System.out.println();
        System.out.print("Enter input file path: ");
        String path = scanner.nextLine().trim();
        String text = FileUtils.readUtf8(path);
        return removeTrailingNewLines(text);
    }

    private static void saveOutputToFile(Scanner scanner, String result) throws IOException {
        System.out.println();
        System.out.print("Enter output file path: ");
        String path = scanner.nextLine().trim();
        FileUtils.writeUtf8(path, result);
        System.out.println("Output saved to: " + path);
    }

    private static String removeTrailingNewLines(String text) {
        while (text.endsWith("\n") || text.endsWith("\r")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    private static int[] convertToKeyBytes(String input, int format) {
        if (format == FORMAT_TEXT) {
            return convertTextToBytes(input);
        }
        if (format == FORMAT_HEX) {
            return convertHexToBytes(input);
        }
        return convertBinaryToBytes(input);
    }

    private static int[] convertTextToBytes(String text) {
        if (text.length() != 16) {
            throw new IllegalArgumentException("TEXT input must be exactly 16 characters.");
        }

        int[] bytes = new int[16];

        for (int i = 0; i < 16; i++) {
            char ch = text.charAt(i);

            if (ch > 255) {
                throw new IllegalArgumentException("TEXT input must use simple 1-byte characters only.");
            }

            bytes[i] = ch;
        }

        return bytes;
    }

    private static int[] convertHexToBytes(String text) {
        String hex = removeAllSpaces(text);

        if (hex.length() != 32) {
            throw new IllegalArgumentException("HEX input must be exactly 32 hex characters.");
        }

        for (int i = 0; i < hex.length(); i++) {
            if (!isHexCharacter(hex.charAt(i))) {
                throw new IllegalArgumentException("HEX input contains invalid characters.");
            }
        }

        int[] bytes = new int[16];
        for (int i = 0; i < 16; i++) {
            int start = i * 2;
            bytes[i] = Integer.parseInt(hex.substring(start, start + 2), 16);
        }
        return bytes;
    }

    private static int[] convertBinaryToBytes(String text) {
        String binary = removeAllSpaces(text);

        if (binary.length() != 128) {
            throw new IllegalArgumentException("BINARY input must be exactly 128 bits.");
        }

        for (int i = 0; i < binary.length(); i++) {
            char ch = binary.charAt(i);
            if (ch != '0' && ch != '1') {
                throw new IllegalArgumentException("BINARY input must contain only 0 and 1.");
            }
        }

        int[] bytes = new int[16];
        for (int i = 0; i < 16; i++) {
            int start = i * 8;
            bytes[i] = Integer.parseInt(binary.substring(start, start + 8), 2);
        }
        return bytes;
    }

    private static String removeAllSpaces(String text) {
        StringBuilder clean = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                clean.append(ch);
            }
        }

        return clean.toString();
    }

    private static boolean isHexCharacter(char ch) {
        return (ch >= '0' && ch <= '9')
                || (ch >= 'a' && ch <= 'f')
                || (ch >= 'A' && ch <= 'F');
    }
}
