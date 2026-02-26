package utils;

public final class TextUtils {
    private TextUtils() {
    }

    public static String normalize(String text, boolean lettersOnly) {
        if (text == null) {
            return "";
        }
        String upper = text.toUpperCase();
        if (!lettersOnly) {
            return upper;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String lettersOnly(String text) {
        return normalize(text, true);
    }
}
