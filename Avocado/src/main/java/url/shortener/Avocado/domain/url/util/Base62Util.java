package url.shortener.Avocado.domain.url.util;

import java.util.HashMap;
import java.util.Map;

public class Base62Util {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Map<Character, Integer> CHAR_INDEX_MAP = new HashMap<>();
    static {
        for (int i = 0; i < BASE62.length(); i++) {
            CHAR_INDEX_MAP.put(BASE62.charAt(i), i);
        }
    }

    public static String encode(Long id) {
        StringBuilder encoded = new StringBuilder();
        do {
            encoded.append(BASE62.charAt((int) (id % 62)));
            id /= 62;
        } while (id > 0);
        return encoded.toString();
    }

    public static Long decode(String encoded) {
        long id = 0;
        for (int i = 0; i < encoded.length(); i++) {
            id = id * 62 + CHAR_INDEX_MAP.get(encoded.charAt(i));
        }
        return id;
    }
}
