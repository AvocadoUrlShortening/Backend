package url.shortener.Avocado.infra.security.dto.response;

import url.shortener.Avocado.infra.security.exception.ErrorCode;

public record ErrorResponseDto(String code, String message) {

    public ErrorResponseDto(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}