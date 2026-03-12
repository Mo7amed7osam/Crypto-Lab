package ciphers;

public enum CipherType {
    CAESAR("Caesar"),
    VIGENERE("Vigenere"),
    OTP("One Time Pad (Encrypt Only)"),
    RAIL_FENCE("Rail Fence"),
    ROW_TRANSPOSITION("Row Transposition"),
    PLAYFAIR("Playfair"),
    HILL("Hill Cipher");

    private final String label;

    CipherType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
