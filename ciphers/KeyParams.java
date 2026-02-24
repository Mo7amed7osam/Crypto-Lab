package ciphers;

import java.util.HashMap;
import java.util.Map;

public class KeyParams {
    private final Map<String, Object> values = new HashMap<>();

    public KeyParams put(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public String getString(String key) {
        Object value = values.get(key);
        return value == null ? null : value.toString();
    }

    public Integer getInt(String key) {
        Object value = values.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return null;
    }

    public int[][] getMatrix(String key) {
        Object value = values.get(key);
        return (int[][]) value;
    }

    public Object get(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }
}
