package url.shortener.Avocado.infra.security.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import url.shortener.Avocado.infra.security.dto.response.ErrorResponseDto;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ErrorResponseDto> handleBusinessException(
            final AuthException e,
            final HttpServletRequest request
    ) {
        log.error("BusinessException: {} {}", e.getAuthErrorCode(), request.getRequestURL());
        return ResponseEntity
                .status(e.getAuthErrorCode().getStatus().value())
                .body(new ErrorResponseDto(e.getAuthErrorCode()));
    }
}
