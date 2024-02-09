package url.shortener.Avocado.infra.security.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import url.shortener.Avocado.domain.member.entity.AuthProvider;
import url.shortener.Avocado.domain.member.entity.Member;
import url.shortener.Avocado.domain.member.repository.MemberRepository;
import url.shortener.Avocado.infra.security.dto.request.LoginRequestDto;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalAuthService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public Member findMember(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_REGISTERED));

        if (!member.getAuthprovider().equals(AuthProvider.LOCAL)) {
            throw new AuthException(AuthErrorCode.OAUTH_USER);
        }

        if (!member.isActivated()) {
            // 인증메일 재전송
            throw new AuthException(AuthErrorCode.USER_NOT_VERIFIED);
        }

        if (!checkPassword(member, loginRequestDto.password())) {
            // password 오류
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID);
        }

        return member;
    }

    public Member signUp(LoginRequestDto loginRequestDto) {
        Optional<Member> member = memberRepository.findByEmail(loginRequestDto.email());
        if(member.isPresent()) {
            throw new AuthException(AuthErrorCode.USER_EXIST);
        } else {
            Member newMember = Member.builder()
                    .email(loginRequestDto.email())
                    .password(encoder.encode(loginRequestDto.password()))
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
    public boolean checkPassword(Member member, String password) {
        return encoder.matches(password, member.getPassword());
    }


}
