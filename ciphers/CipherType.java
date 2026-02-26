package ciphers;

public enum CipherType {
    CAESAR("Caesar"),
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
