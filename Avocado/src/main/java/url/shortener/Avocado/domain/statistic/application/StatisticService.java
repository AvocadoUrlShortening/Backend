package url.shortener.Avocado.domain.statistic.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import url.shortener.Avocado.domain.statistic.domain.Statistic;
import url.shortener.Avocado.domain.statistic.domain.StatisticInfo;
import url.shortener.Avocado.domain.statistic.domain.StatisticVO;
import url.shortener.Avocado.domain.statistic.dto.response.GeoLocationResponse;
import url.shortener.Avocado.domain.statistic.repository.StatisticRepository;
import url.shortener.Avocado.domain.statistic.util.UserAgentParser;
import url.shortener.Avocado.domain.url.application.UrlService;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.domain.url.exception.UrlErrorCode;
import url.shortener.Avocado.domain.url.exception.UrlException;
import url.shortener.Avocado.domain.url.repository.UrlRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final RestTemplate restTemplate;
    private final UrlRepository urlRepository;
    private final StatisticRepository statisticRepository;
    @Value("${GEO_API_KEY}")
    private String api_key;
    @Value("${GEO_URL}")
    private String geo_url;
    @Value("${GEO_FIELD}")
    private String geo_field;

    @Async("statistic")
    public CompletableFuture<StatisticInfo> processHeader(StatisticVO statisticVO) {
        log.info("[Thread]: processHeader- " + Thread.currentThread().getName());
        CompletableFuture<StatisticInfo> future = new CompletableFuture<>();

        try {
            log.info("[ASYNC]: ProcessHeader Start!");
            String agent = UserAgentParser.getDevice(statisticVO.getUserAgent());
            String[] parts = agent.split("-");
            String language = Optional.ofNullable(statisticVO.getAcceptLanguage())
                    .map(header -> header.split(",")[0])
                    .orElse("Unknown");
//            String endpoint = geo_url + api_key + "&ip=" + "175.113.145.29" + geo_field;
            String endpoint = geo_url + api_key + "&ip=" + statisticVO.getIp() + geo_field;
            GeoLocationResponse response = restTemplate.getForObject(endpoint, GeoLocationResponse.class);
            assert response != null;
            String country = response.country_name() != null ? response.country_name() : "Unknown";
            String city = response.city() != null ? response.city() : "Unknown";

            StatisticInfo info = new StatisticInfo(statisticVO.getShortUrl(), country, city, parts[0], parts[1], parts[2], language);

            future.complete(info);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
    @Async
    @Transactional
    public void updateStatistic(StatisticInfo statisticinfo) {
        Url url = urlRepository.findByShortUrl(statisticinfo.getShortUrl())
                .orElseThrow(() -> new UrlException(UrlErrorCode.URL_NOT_EXIST));
        Statistic statistic = new Statistic(url, statisticinfo);
        statisticRepository.save(statistic);
        url.addStatistic(statistic);
    }
}
