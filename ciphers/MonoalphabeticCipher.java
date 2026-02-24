package ciphers;

import utils.TextUtils;

public class MonoalphabeticCipher implements Cipher {
    @Override
    public String encrypt(String text, KeyParams params) {
        String map = validateMap(params.getString("map"));
        return translate(text.toUpperCase(), map, false);
    }

    @Override
    public String decrypt(String text, KeyParams params) {
        String map = validateMap(params.getString("map"));
        return translate(text.toUpperCase(), map, true);
    }

    private String translate(String text, String map, boolean decrypt) {
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (!decrypt) {
                    out.append(map.charAt(c - 'A'));
                } else {
                    out.append((char) ('A' + map.indexOf(c)));
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String validateMap(String map) {
        TextUtils.validateSubstitutionMap(map);
        return map.toUpperCase();
    }
}
