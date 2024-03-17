package url.shortener.Avocado.domain.statistic.util;

public class UserAgentParser {
    public static String getDevice(String userAgent) {
        if (userAgent == null) {
            return "Unknown-Unknown-Unknown";
        }
        userAgent = userAgent.toUpperCase();

        String device = "PC";
        if (userAgent.contains("MOBILE") || userAgent.contains("MOBI") || userAgent.contains("IPHONE")) {
            device = "MOBILE";
        }

        String os = "Other";
        if (userAgent.contains("WINDOWS")) {
            os = "Windows";
        } else if (userAgent.contains("MAC OS")) {
            os = "Mac OS";
        } else if (userAgent.contains("X11")) {
            os = "Unix";
        } else if (userAgent.contains("ANDROID")) {
            os = "Android";
        } else if (userAgent.contains("IPHONE")) {
            os = "iOS";
        }

        String browser = "Other";
        if (userAgent.contains("SAFARI") && userAgent.contains("CHROME")) {
            browser = "Safari";
        } else if (userAgent.contains("CHROME")) {
            browser = "Chrome";
        } else if (userAgent.contains("TRIDENT") || userAgent.contains("MSIE")) {
            browser = "IE";
        } else if (userAgent.contains("WHALE")) {
            browser = "Whale";
        } else if (userAgent.contains("OPERA") || userAgent.contains("OPR")) {
            browser = "Opera";
        } else if (userAgent.contains("FIREFOX")) {
            browser = "Firefox";
        }
        return device + "-" + os + "-" + browser;
    }
}
