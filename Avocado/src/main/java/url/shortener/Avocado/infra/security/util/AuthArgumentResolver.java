package url.shortener.Avocado.infra.security.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import url.shortener.Avocado.domain.member.domain.Member;
import url.shortener.Avocado.infra.security.annotation.AuthUser;
import url.shortener.Avocado.infra.security.application.AuthService;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                  NativeWebRequest webRequest, WebDataBinderFactory factory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");
        String refresh = request.getHeader("Refresh");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String newToken = authService.checkToken(token, refresh);
            HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
            Objects.requireNonNull(response).setHeader("NewAccessToken", newToken);
            Member member = authService.getMember(newToken);
            if (member.isActivated()) {
                return member;
            } else {
                throw new AuthException(AuthErrorCode.USER_NOT_VERIFIED);
            }
        } else {
            throw new AuthException(AuthErrorCode.TOKEN_EMPTY);
        }

//        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//        String authHeader = request.getHeader("Authorization");
//        Optional<Cookie> cookie = Optional.ofNullable(request.getCookies())
//                .flatMap(cookies -> Arrays.stream(cookies).filter(c -> c.getName().equals("refreshToken")).findFirst());
//        String refresh = cookie.map(Cookie::getValue).orElse(null);
//
////        String refresh = cookie.map(c -> c.getValue()).orElse("");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            String newToken = authService.checkToken(token, refresh);
//            HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
//            Objects.requireNonNull(response).setHeader("NewAccessToken", newToken);
//            Member member = authService.getMember(newToken);
//            if(member.isActivated()) {
//                return member;
//            } else {
//                throw new AuthException(AuthErrorCode.USER_NOT_VERIFIED);
//            }
//        } else {
//            throw new AuthException(AuthErrorCode.TOKEN_EMPTY);
//        }
    }
}
