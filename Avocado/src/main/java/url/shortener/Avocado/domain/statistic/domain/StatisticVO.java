package url.shortener.Avocado.domain.statistic.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticVO {
    String shortUrl;
    String ip;
    String userAgent;
    String acceptLanguage;

    public StatisticVO(String shortUrl, String ip, String userAgent, String acceptLanguage){
        this.shortUrl = shortUrl;
        this.ip = ip;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
    }
}
