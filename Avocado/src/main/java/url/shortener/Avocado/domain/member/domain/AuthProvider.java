package url.shortener.Avocado.domain.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    GOOGLE, NAVER, LOCAL
}
