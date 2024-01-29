package url.shortener.Avocado.domain.url.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionChecker {
    public static boolean isUrlReachable(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            // 200 내 코드는 URL이 유효
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception e) {
            return false;
        }
    }
}
