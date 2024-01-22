package url.shortener.Avocado.infra.security.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.security.dto.request.LoginRequestDto;
import url.shortener.Avocado.infra.security.dto.response.GoogleInfo;
import url.shortener.Avocado.infra.security.dto.response.GoogleResponseDto;
import url.shortener.Avocado.infra.security.dto.response.NaverResponseDto;
import url.shortener.Avocado.infra.security.dto.response.TokenResponseDto;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleAuthService googleAuthService;
    private final NaverAuthService naverAuthService;
    private final LocalAuthService localAuthService;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    public TokenResponseDto login(String provider, String accessCode) {
        if (provider.equals("google")) {
            GoogleResponseDto googleResponseDto = googleAuthService.getAccessToken(accessCode);
            GoogleInfo googleInfo = googleAuthService.getGoogleInfo(googleResponseDto.accesToken());
            System.out.println(googleInfo.email());
            Member member = googleAuthService.getMember(googleInfo);
            return issueToken(member);
        } else if(provider.equals("naver")) {
            String token = naverAuthService.getAccessToken(accessCode);
            NaverResponseDto naverResponseDto = naverAuthService.getNaverInfo(token);
            Member member = naverAuthService.getMember(naverResponseDto);
            return issueToken(member);
        } else {
            // throw error
        }
        return null;
    }

    public TokenResponseDto signUp(LoginRequestDto loginRequestDto) {
        return issueToken(localAuthService.signUp(loginRequestDto));
    }

    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        return issueToken(localAuthService.findMember(loginRequestDto));
    }

    public void getCode(String provider) {
        if(provider.equals("google")) {
            googleAuthService.getCode();
        } else if(provider.equals("naver")) {
            naverAuthService.getCode();
        } else {
            // throw error

        }
    }
    private TokenResponseDto issueToken(Member member) {
        String accessToken = tokenService.createAccessToken(member.getEmail());
        String refreshToken = tokenService.createRefreshToken(member.getEmail());
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public Member getMember(String accessToken, String refreshToken) {
        String email = tokenService.extractEmail(accessToken);
        String token = checkToken(accessToken, refreshToken, email);
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()) {
            return member.get();
        }
        // 없으면 error
        return null;
    }


    public String checkToken(String accessToken, String refreshToken, String email) {
        if(!tokenService.validate(accessToken)) {
            if(tokenService.validate(refreshToken)) {
                // access token만 발급
                return tokenService.createAccessToken(email);
            }
            // 재 로그인 필요
            return null;
        }
        // token 유효
        return accessToken;
    }
}
