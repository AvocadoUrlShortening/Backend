package url.shortener.Avocado.domain.url.application;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.domain.url.dto.request.ShortenRequestDto;
import url.shortener.Avocado.domain.url.exception.UrlErrorCode;
import url.shortener.Avocado.domain.url.exception.UrlException;
import url.shortener.Avocado.domain.url.repository.UrlRepository;
import url.shortener.Avocado.domain.url.util.Base62Util;
import url.shortener.Avocado.domain.url.util.SnowflakeIdGenerator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final SnowflakeIdGenerator idGenerator;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public String createCustomUrl(Member member, ShortenRequestDto requestDto) {
        return customUrl(member, requestDto);
    }

    public String createRandomUrl(ShortenRequestDto requestDto) {
        return randomUrl(requestDto.originalUrl());
    }

    @Transactional
    public String customUrl(Member member, ShortenRequestDto requestDto) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(requestDto.shortUrl())) {
            throw new UrlException(UrlErrorCode.URL_EXIST);
        }
        if(urlRepository.existsByShortUrl(requestDto.shortUrl())) {
            throw new UrlException(UrlErrorCode.URL_EXIST);
        }

        long id = idGenerator.nextId();
        Url url = Url.builder().
                id(id).
                shortUrl(requestDto.shortUrl()).
                originalUrl(requestDto.originalUrl()).
                createdDate(new Date(System.currentTimeMillis())).
                build();
        url.updateOwner(member, false);
        urlRepository.save(url);

        valueOperations.set(requestDto.shortUrl(), requestDto.originalUrl());
        return requestDto.shortUrl();
    }
    @Transactional
    public String randomUrl(String originalUrl) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String encoded;
        long id;
        do {
            id = idGenerator.nextId();
            encoded = Base62Util.encode(id);
        } while (redisTemplate.hasKey(encoded) && urlRepository.existsByShortUrl(encoded));
        Url url = Url.builder().
                id(id).
                shortUrl(encoded).
                originalUrl(originalUrl).
                createdDate(new Date(System.currentTimeMillis())).
                build();
        url.updateOwner(null, true);
        urlRepository.save(url);

        valueOperations.set(encoded, originalUrl);
        redisTemplate.expire(encoded,7*60*60*24, TimeUnit.SECONDS);
        return encoded;
    }

    public String getUrl(String customUrl) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        return Optional.ofNullable(valueOperations.get(customUrl))
                .orElseGet(() -> {
                    Url url = urlRepository.findByShortUrl(customUrl)
                            .orElseThrow(() -> new UrlException(UrlErrorCode.URL_NOT_EXIST));
                    valueOperations.set(customUrl, url.getOriginalUrl());
                    return url.getOriginalUrl();
                });
    }

    @Async("statistic")
    public void processHeader(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("User-Agent", request.getHeader("User-Agent"));
        headersMap.put("Accept-Language", request.getHeader("Accept-Language"));
        headersMap.put("sec-ch-ua", request.getHeader("sec-ch-ua"));
        headersMap.put("sec-ch-ua-mobile", request.getHeader("sec-ch-ua-mobile"));
        headersMap.put("sec-ch-ua-platform", request.getHeader("sec-ch-ua-platform"));
        headersMap.put("ip", request.getRemoteAddr());
        // process statistic ~
    }


}
