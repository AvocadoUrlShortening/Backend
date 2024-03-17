package url.shortener.Avocado.domain.url.application;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import url.shortener.Avocado.domain.member.domain.Member;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.domain.url.dto.request.ShortenRequestDto;
import url.shortener.Avocado.domain.url.exception.UrlErrorCode;
import url.shortener.Avocado.domain.url.exception.UrlException;
import url.shortener.Avocado.domain.url.repository.UrlRepository;
import url.shortener.Avocado.domain.url.util.Base62Util;
import url.shortener.Avocado.domain.url.util.SnowflakeIdGenerator;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final SnowflakeIdGenerator idGenerator;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ValueOperations<String, String> valueOperations;

    @Transactional
    public String createCustomUrl(Member member, ShortenRequestDto requestDto) {
        return customUrl(member, requestDto);
    }

    @Transactional
    public String createRandomUrl(ShortenRequestDto requestDto) {
        return randomUrl(requestDto.originalUrl());
    }


    public String customUrl(Member member, ShortenRequestDto requestDto) {
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
                build();
        url.updateOwner(member, false);
        member.addUrl(url);
        urlRepository.save(url);
        valueOperations.set(requestDto.shortUrl(), requestDto.originalUrl());
        return requestDto.shortUrl();
    }
    public String randomUrl(String originalUrl) {
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
                build();
        url.updateOwner(null, true);
        urlRepository.save(url);

        valueOperations.set(encoded, originalUrl);
        redisTemplate.expire(encoded,7*60*60*24, TimeUnit.SECONDS);
        return encoded;
    }

    public String getUrl(String shortUrl) {
        return Optional.ofNullable(valueOperations.get(shortUrl))
                .orElseGet(() -> {
                    Url url = urlRepository.findByShortUrl(shortUrl)
                            .orElseThrow(() -> new UrlException(UrlErrorCode.URL_NOT_EXIST));
                    valueOperations.set(shortUrl, url.getOriginalUrl());
                    return url.getOriginalUrl();
                });
    }
}
