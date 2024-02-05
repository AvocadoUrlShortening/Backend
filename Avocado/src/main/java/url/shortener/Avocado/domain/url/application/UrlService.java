package url.shortener.Avocado.domain.url.application;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final SnowflakeIdGenerator idGenerator;
    private final UrlRepository urlRepository;

    public String createCustomUrl(Member member, ShortenRequestDto requestDto) {
        return customUrl(member, requestDto);
    }

    public String createRandomUrl(ShortenRequestDto requestDto) {
        return randomUrl(requestDto.originalUrl());
    }

    @Transactional
    public String customUrl(Member member, ShortenRequestDto requestDto) {
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
        urlRepository.save(url);
        return requestDto.shortUrl();
    }
    @Transactional
    public String randomUrl(String originalUrl) {
        String encoded;
        long id;
        do {
            id = idGenerator.nextId();
            encoded = Base62Util.encode(id);
        } while (urlRepository.existsByShortUrl(encoded));
        Url url = Url.builder().
                id(id).
                shortUrl(encoded).
                originalUrl(originalUrl).
                build();
        url.updateOwner(null, true);
        urlRepository.save(url);
        return encoded;
    }

    public Url getUrl(String customUrl) {
        return urlRepository.findByShortUrl(customUrl)
                .orElseThrow(() -> new UrlException(UrlErrorCode.URL_NOT_EXIST));
    }
}
