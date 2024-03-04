package url.shortener.Avocado.global.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import url.shortener.Avocado.domain.url.exception.UrlException;
import url.shortener.Avocado.infra.security.exception.AuthException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ErrorResponseDto> AuthException(
            final AuthException e,
            final HttpServletRequest request
    ) {
        log.error("AuthException: {} {}", e.getAuthErrorCode(), request.getRequestURL());
        return ResponseEntity
                .status(e.getAuthErrorCode().getStatus().value())
                .body(new ErrorResponseDto(e.getAuthErrorCode()));
    }

    @ExceptionHandler(UrlException.class)
    protected ResponseEntity<ErrorResponseDto> UrlException(
            final UrlException e,
            final HttpServletRequest request
    ) {
        log.error("AuthException: {} {}", e.getUrlErrorCode(), request.getRequestURL());
        return ResponseEntity
                .status(e.getUrlErrorCode().getStatus().value())
                .body(new ErrorResponseDto(e.getUrlErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseDto> MethodArgumentNotValidException(final MethodArgumentNotValidException e,
                                                                               final HttpServletRequest request){
        log.error("MethodArgumentNotValidException: {} {}", e.getMessage(), request.getRequestURL());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(CommonErrorCode.METHOD_NOT_VALID.getCode(),
                e.getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity
                .status(CommonErrorCode.METHOD_NOT_VALID.getStatus().value())
                .body(errorResponseDto);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> InternalServerErrorException(
            final Exception e,
            final HttpServletRequest request
    ) {
        log.error("Exception: {} {}", e.getMessage(), request.getRequestURL());
        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .body(new ErrorResponseDto(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

}
