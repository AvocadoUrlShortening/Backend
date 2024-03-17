package url.shortener.Avocado.domain.statistic.domain;


import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticInfo {
    private String shortUrl;
    private String country;
    private String city;
    private String device;
    private String os;
    private String browser;
    private String language;

    public StatisticInfo(String shortUrl, String country, String city, String device, String os, String browser, String language){
        this.shortUrl = shortUrl;
        this.country = country;
        this.city = city;
        this.device = device;
        this.os = os;
        this.browser = browser;
        this.language = language;
    }
}
