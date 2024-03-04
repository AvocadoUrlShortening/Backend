package url.shortener.Avocado.domain.statistic.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import url.shortener.Avocado.domain.statistic.dto.response.GeoLocationResponse;
import url.shortener.Avocado.domain.statistic.repository.StatisticRepository;
import url.shortener.Avocado.domain.statistic.util.UserAgentParser;
import url.shortener.Avocado.domain.url.domain.Url;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final RestTemplate restTemplate;
    private final StatisticRepository statisticRepository;
    @Value("${GEO_API_KEY}")
    private String api_key;
    @Value("${GEO_URL}")
    private String geo_url;
    @Value("${GEO_FIELD}")
    private String geo_field;
    @Async("statistic")
    public void processHeader(Url url, String ip, String userAgent, String acceptLanguage) {
//        if (url.getStatistic() == null) {
//            url.setStatistic(new Statistic());
//        }
//        Statistic statistic = url.getStatistic();

        String endpoint = geo_url + api_key + "&ip=" + ip + geo_field;
//        String endpoint = geo_url + api_key + "&ip=" + "175.113.145.29" + geo_field;
        GeoLocationResponse response = null;
        try {
            response = restTemplate.getForObject(endpoint, GeoLocationResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.LOCKED) {
                log.error("HttpClientErrorException while calling GeoLocation API: {} - Status code: {}", e.getMessage(), e.getStatusCode());
            }
        }


        String device = UserAgentParser.getDevice(userAgent);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM:dd:HH:mm:ss");
        String time = formatter.format(date);
        String info = Optional.ofNullable(response)
                .map(r -> String.join("-",
                        Optional.ofNullable(r.country_name()).orElse("Unknown"),
                        Optional.ofNullable(r.city()).orElse("Unknown"),
                        Optional.ofNullable(device).orElse("Unknown"),
                        acceptLanguage,
                        time
                ))
                .orElse(String.join("-", "Unknown", "Unknown",
                        Optional.ofNullable(device).orElse("Unknown"),
                        acceptLanguage,
                        time));

//        updateStatistic(statistic, info);
        url.updateStatistic(info);
    }


//    @Transactional
//    public void updateStatistic(Statistic statistic, String info) {
//        statistic.updateStatistic(info);
//        statisticRepository.save(statistic);
//    }

}
