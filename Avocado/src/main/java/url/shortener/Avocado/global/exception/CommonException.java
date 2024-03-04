package url.shortener.Avocado.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException{
    private final CommonErrorCode CommonErrorCode;
}