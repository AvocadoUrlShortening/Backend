package url.shortener.Avocado.infra.security.dto.response;

import url.shortener.Avocado.infra.security.exception.AuthErrorCode;

public record ErrorResponseDto(String code, String message) {

    public ErrorResponseDto(AuthErrorCode authErrorCode) {
        this(authErrorCode.getCode(), authErrorCode.getMessage());
    }
}