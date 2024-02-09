package url.shortener.Avocado.infra.security.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import url.shortener.Avocado.domain.member.entity.AuthProvider;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.security.dto.response.NaverResponseDto;
import url.shortener.Avocado.infra.security.dto.response.TokenResponseDto;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NaverAuthService {
    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;
    @Value("${naver.redirect-uri}")
    private String redirectUri;
    @Value("${naver.token-uri}")
    private String tokenUri;
    @Value("${naver.user-info-uri}")
    private String userinfoUri;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    public String getAccessToken(String code) {
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
        ResponseEntity<TokenResponseDto> responseEntity = restTemplate.postForEntity(tokenUri, requestEntity, TokenResponseDto.class);
        return Objects.requireNonNull(responseEntity.getBody()).accessToken();
    }
    public NaverResponseDto getNaverInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<NaverResponseDto> response = restTemplate.postForEntity(userinfoUri, request, NaverResponseDto.class);
        return response.getBody();
    }

    public Member getMember(NaverResponseDto naverResponseDto) {

        String id = naverResponseDto.getId();
        String email = naverResponseDto.getEmail();
        String profile = naverResponseDto.getProfile();

        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent() ) {
            if (member.get().getAuthprovider().equals(AuthProvider.NAVER) && member.get().getOAuth2Id().equals(id)) {
                return member.get();
            } else {
                throw new AuthException(AuthErrorCode.LOCAL_USER);
            }
        }
        Member newMember = Member.builder()
                .email(email)
                .provider(AuthProvider.NAVER)
                .oAuth2Id(id)
                .profile(profile)
                .activated(true)
                .build();
        memberRepository.save(newMember);
        return newMember;
    }
}
