package ciphers;

public interface Cipher {
    String encrypt(String text, KeyParams params);

    String decrypt(String text, KeyParams params);
}
