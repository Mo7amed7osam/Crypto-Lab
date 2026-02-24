package ciphers;

public final class CipherFactory {
    private CipherFactory() {
    }

    public static Cipher getCipher(CipherType type) {
        switch (type) {
            case VIGENERE:
                return new VigenereCipher();
            case OTP:
                return new OneTimePadCipher();
            case RAIL_FENCE:
                return new RailFenceCipher();
            case ROW_TRANSPOSITION:
                return new RowTranspositionCipher();
            case MONOALPHABETIC:
                return new MonoalphabeticCipher();
            case PLAYFAIR:
                return new PlayfairCipher();
            case HILL:
                return new HillCipher();
            default:
                throw new IllegalArgumentException("Unsupported cipher type: " + type);
        }
    }
}
