package ciphers;

public enum CipherType {
    VIGENERE("Vigenere"),
    OTP("One-Time Pad (OTP)"),
    RAIL_FENCE("Rail Fence"),
    ROW_TRANSPOSITION("Row Transposition"),
    MONOALPHABETIC("Monoalphabetic Substitution"),
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
