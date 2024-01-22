package url.shortener.Avocado.infra.security.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverResponseDto {
    @JsonProperty("response")
    public Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        private String email;
        private String profile_image;
        private String id;
    }

    public String getEmail() {
        return response.getEmail();
    }
    public String getProfile() {
        return response.getProfile_image();
    }
    public String getId() {
        return response.getId();
    }

}
