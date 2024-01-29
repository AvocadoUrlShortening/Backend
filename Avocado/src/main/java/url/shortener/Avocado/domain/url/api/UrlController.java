package url.shortener.Avocado.domain.url.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import url.shortener.Avocado.domain.member.entity.Member;
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


    @PostMapping("/shortner/random")
    public ResponseEntity<Void> urlShorten(@RequestBody ShortenRequestDto shortenRequestDto) {
        if (!UrlConnectionChecker.isUrlReachable(shortenRequestDto.originalUrl())) {
            throw new UrlException(UrlErrorCode.URL_NOT_REACHABLE);
        }
        urlService.createRandomUrl(shortenRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shortner/custom")
    public ResponseEntity<Void> urlShorten(@AuthUser Member member, @RequestBody ShortenRequestDto shortenRequestDto) {
        if (!UrlConnectionChecker.isUrlReachable(shortenRequestDto.originalUrl())) {
            throw new UrlException(UrlErrorCode.URL_NOT_REACHABLE);
        }
        urlService.createCustomUrl(member, shortenRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortUrl}")
    public String getOriginalUrl(@PathVariable String shortUrl, HttpServletRequest request) {
        urlService.processHeader(request);
        Url url = urlService.getUrl(shortUrl);
        return url.getOriginalUrl();
    }

}
