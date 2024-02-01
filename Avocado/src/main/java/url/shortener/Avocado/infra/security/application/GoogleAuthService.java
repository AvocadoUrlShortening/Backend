package url.shortener.Avocado.infra.security.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import url.shortener.Avocado.domain.member.entity.AuthProvider;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.security.dto.response.GoogleInfo;
import url.shortener.Avocado.infra.security.dto.response.GoogleResponseDto;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Value("${google.token-uri}")
    private String tokenEndpoint;
    @Value("${google.user-info-uri}")
    private String userinfoUri;

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    public GoogleResponseDto getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<GoogleResponseDto> responseEntity = restTemplate.postForEntity(tokenEndpoint, requestEntity, GoogleResponseDto.class);
        return responseEntity.getBody();
    }

    public GoogleInfo getGoogleInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<GoogleInfo> response = restTemplate.postForEntity(userinfoUri, request, GoogleInfo.class);
        return response.getBody();
    }

    public Member getMember(GoogleInfo googleInfo) {
        Optional<Member> member = memberRepository.findByEmail(googleInfo.email());
        if (member.isPresent()) {
            if (member.get().getAuthprovider().equals(AuthProvider.GOOGLE) && member.get().getOAuth2Id().equals(googleInfo.id())) {
                return member.get();
            } else {
                // 이메일은 존재하는데, provider가 google이 아닌 경우
                throw new AuthException(AuthErrorCode.LOCAL_USER);
            }
        }
        Member newMember = Member.builder()
                .email(googleInfo.email())
                .provider(AuthProvider.GOOGLE)
                .oAuth2Id(googleInfo.id())
                .profile(googleInfo.profile())
                .activated(true)
                .build();
        memberRepository.save(newMember);
        return newMember;
    }

}
