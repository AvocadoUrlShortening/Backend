package url.shortener.Avocado.infra.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
public record GoogleResponseDto(@JsonProperty("access_token") String accesToken,
                               @JsonProperty("id_token") String idToken) {
}