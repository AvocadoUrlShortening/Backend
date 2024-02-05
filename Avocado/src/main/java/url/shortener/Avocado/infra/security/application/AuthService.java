package url.shortener.Avocado.infra.security.application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import url.shortener.Avocado.domain.member.domain.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.mail.application.EmailService;
import url.shortener.Avocado.infra.security.dto.request.LoginRequestDto;
import url.shortener.Avocado.infra.security.dto.response.*;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;



@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleAuthService googleAuthService;
    private final NaverAuthService naverAuthService;
    private final LocalAuthService localAuthService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final MemberRepository memberRepository;

    public TokenResponseDto login(String provider, String accessCode) {
        if (provider.equals("google")) {
            GoogleResponseDto googleResponseDto = googleAuthService.getAccessToken(accessCode);
            GoogleInfo googleInfo = googleAuthService.getGoogleInfo(googleResponseDto.accesToken());
            Member member = googleAuthService.getMember(googleInfo);
            return issueToken(member);
        } else if(provider.equals("naver")) {
            String token = naverAuthService.getAccessToken(accessCode);
            NaverResponseDto naverResponseDto = naverAuthService.getNaverInfo(token);
            Member member = naverAuthService.getMember(naverResponseDto);
            return issueToken(member);
        } else {
            throw new AuthException(AuthErrorCode.PROVIDER_INVALID);
        }
    }
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        return issueToken(localAuthService.findMember(loginRequestDto));
    }

    public SignUpResponseDto signUp(LoginRequestDto loginRequestDto) {
        return verifyToken(localAuthService.signUp(loginRequestDto));
    }


    private TokenResponseDto issueToken(Member member) {
        String accessToken = tokenService.createAccessToken(member.getEmail());
        String refreshToken = tokenService.createRefreshToken(member.getEmail());
        return new TokenResponseDto(accessToken, refreshToken);
    }

    private SignUpResponseDto verifyToken(Member member) {
        String token = tokenService.createVerifyToken(member.getEmail());
        String verificatoinLink = "http://localhost:8080/verify?token=" + token;
        emailService.sendMail(member.getEmail(), "Verify your Email", "링크를 눌러 인증하세요" + verificatoinLink);
        return new SignUpResponseDto(token);
    }

    public Member getMember(String accessToken) {
        String email = tokenService.extractEmail(accessToken);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    public TokenResponseDto verifyMember(String token) {
        boolean verified = tokenService.validate(token);
        if (verified) {
            String email = tokenService.extractEmail(token);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
            member.activateMember();
            memberRepository.save(member);
            return issueToken(member);
        } else {
            throw new AuthException(AuthErrorCode.VERIFY_TOKEN_EXPIRED);
        }
    }

    public String checkToken(String accessToken, String refreshToken) {
        if(!tokenService.validate(accessToken)) {
            String email = tokenService.extractEmail(accessToken);
            if(tokenService.validate(refreshToken)) {
                // access token만 발급
                return tokenService.createAccessToken(email);
            }
            // 재 로그인 필요
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
        // token 유효
        return accessToken;
    }
}
