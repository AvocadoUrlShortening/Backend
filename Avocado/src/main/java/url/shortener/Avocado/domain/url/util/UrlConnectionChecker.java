package url.shortener.Avocado.domain.url.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionChecker {
    public static boolean isUrlReachable(String originalUrl) {
        try {
            String urlStr = ensureProtocol(originalUrl);
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception e) {
            return false;
        }
    }

    public static String ensureProtocol(String urlStr) {
        if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
            return "https://" + urlStr;
        }
        return urlStr;
    }
}
