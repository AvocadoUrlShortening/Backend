package url.shortener.Avocado.domain.url.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import url.shortener.Avocado.domain.member.domain.Member;
import url.shortener.Avocado.domain.statistic.application.StatisticService;

import url.shortener.Avocado.domain.url.application.UrlService;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.domain.url.dto.request.ShortenRequestDto;
import url.shortener.Avocado.domain.url.exception.UrlErrorCode;
import url.shortener.Avocado.domain.url.exception.UrlException;
import url.shortener.Avocado.domain.url.util.UrlConnectionChecker;
import url.shortener.Avocado.infra.security.annotation.AuthUser;


@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final StatisticService statisticService;

    @PostMapping("/shortener/random")
    public ResponseEntity<String> urlShorten(@RequestBody ShortenRequestDto shortenRequestDto) {
        if (!UrlConnectionChecker.isUrlReachable(shortenRequestDto.originalUrl())) {
            throw new UrlException(UrlErrorCode.URL_NOT_REACHABLE);
        }
        String rand = urlService.createRandomUrl(shortenRequestDto);
        return ResponseEntity.ok(rand);
    }

    @PostMapping("/shortener/custom")
    public ResponseEntity<String> urlShorten(@AuthUser Member member, @RequestBody ShortenRequestDto shortenRequestDto) {
        if (!UrlConnectionChecker.isUrlReachable(shortenRequestDto.originalUrl())) {
            throw new UrlException(UrlErrorCode.URL_NOT_REACHABLE);
        }
        String shortUrl = urlService.createCustomUrl(member, shortenRequestDto);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public String getOriginalUrl(@PathVariable String shortUrl, HttpServletRequest request) {
//        urlService.processHeader(request);
        return urlService.getUrl(shortUrl);
//        Url url = urlService.getUrl(shortUrl);
//        statisticService.processHeader(url, request.getRemoteAddr(), request.getHeader("User-Agent"), request.getHeader("Accept-Language"));
//        return url.getOriginalUrl();

    }

}
