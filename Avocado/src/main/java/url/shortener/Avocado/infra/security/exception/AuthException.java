package url.shortener.Avocado.infra.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class AuthException extends RuntimeException {
    private final AuthErrorCode authErrorCode;
}
