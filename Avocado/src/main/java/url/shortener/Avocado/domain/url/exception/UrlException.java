package url.shortener.Avocado.domain.url.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class UrlException extends RuntimeException {
    private final UrlErrorCode urlErrorCode;
}