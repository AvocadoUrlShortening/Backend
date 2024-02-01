package url.shortener.Avocado.domain.url.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import url.shortener.Avocado.infra.security.exception.ErrorCode;

@Getter
public enum UrlErrorCode implements ErrorCode {

    URL_EXIST(HttpStatus.BAD_REQUEST, "E401", "URL Exists"),
    URL_NOT_EXIST(HttpStatus.BAD_REQUEST, "E402", "URL Not Exists"),
    URL_NOT_REACHABLE(HttpStatus.BAD_REQUEST, "E403", "URL Not Reachable");
    private final HttpStatus status;
    private final String code;
    private final String message;

    UrlErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;

    }
}
