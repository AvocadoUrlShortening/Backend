package url.shortener.Avocado.infra.security.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleInfo(@JsonProperty("id") String id,
                         @JsonProperty("email") String email,
                         @JsonProperty("picture") String profile) {
}
