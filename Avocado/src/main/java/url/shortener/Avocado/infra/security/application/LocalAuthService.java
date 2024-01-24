package url.shortener.Avocado.infra.security.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import url.shortener.Avocado.domain.member.entity.AuthProvider;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.security.dto.request.LoginRequestDto;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalAuthService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public Member findMember(LoginRequestDto loginRequestDto) {
        Optional<Member> member = memberRepository.findByEmail(loginRequestDto.email());
        if(member.isPresent()) {
            if (member.get().getAuthprovider().equals(AuthProvider.LOCAL)) {
                if (!member.get().isActivated()) {
                    // 인증메일 재전송
                    throw new AuthException(AuthErrorCode.USER_NOT_VERIFIED);
                } else {
                    if (verifyMember(member.get(), loginRequestDto.password())) {
                        return member.get();
                    } else {
                        // password 오류
                        throw new AuthException(AuthErrorCode.PASSWORD_INVALID);
                    }
                }
            } else{
                // 소셜 로그인 아이디 에러
                throw new AuthException(AuthErrorCode.OAUTH_USER);
            }
        } else {
            throw new AuthException(AuthErrorCode.USER_NOT_REGISTERD);
            // 아이디 없음 -> register로 이동
        }
    }

    public Member signUp(LoginRequestDto loginRequestDto) {
        Optional<Member> member = memberRepository.findByEmail(loginRequestDto.email());
        if(member.isPresent()) {
            throw new AuthException(AuthErrorCode.USER_EXIST);
        } else {
            Member newMember = Member.builder()
                    .email(loginRequestDto.email())
                    .provider(AuthProvider.LOCAL)
                    .oAuth2Id("")
                    .profile("")
                    .activated(false)
                    .build();
            memberRepository.save(newMember);
            return newMember;
            // email verify
        }
    }
    public boolean verifyMember(Member member, String password) {
        return encoder.matches(password, member.getPassword());
    }


}
