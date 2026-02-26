package ciphers;

public final class CipherFactory {
    private CipherFactory() {
    }

    public static Cipher getCipher(CipherType type) {
        switch (type) {
            case CAESAR:
                return new CaesarSwingCipher();
            case PLAYFAIR:
                return new PlayfairCipher();
            case HILL:
                return new HillCipher();
            default:
                throw new IllegalArgumentException("Unsupported cipher type: " + type);
        }
    }
}
