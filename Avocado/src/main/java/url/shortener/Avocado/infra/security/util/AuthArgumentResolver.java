package url.shortener.Avocado.infra.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import url.shortener.Avocado.infra.security.annotation.AuthUser;
import url.shortener.Avocado.infra.security.application.AuthService;

import java.util.Arrays;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                  NativeWebRequest webRequest, WebDataBinderFactory factory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");

        Optional<Cookie> cookie = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies).filter(c -> c.getName().equals("refreshToken")).findFirst());

        String refresh = cookie.map(c -> c.getValue()).orElse("");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return authService.getMember(token, refresh);
        }
        // request without token 에러
        return null;
    }
}
