package ciphers;

public final class CipherFactory {
    private CipherFactory() {
    }

    public static Cipher getCipher(CipherType type) {
        switch (type) {
            case AES:
                return new AesCipher();
            case CAESAR:
                return new CaesarSwingCipher();
            case VIGENERE:
                return new VigenereSimpleCipher();
            case OTP:
                return new OneTimePadSimpleCipher();
            case RAIL_FENCE:
                return new RailFenceSimpleCipher();
            case ROW_TRANSPOSITION:
                return new RowTranspositionSimpleCipher();
            case PLAYFAIR:
                return new PlayfairCipher();
            case HILL:
                return new HillCipher();
            default:
                throw new IllegalArgumentException("Unsupported cipher type: " + type);
        }
    }
}
