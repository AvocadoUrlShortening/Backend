package url.shortener.Avocado.infra.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpResponseDto(String verifyToken) {
}
