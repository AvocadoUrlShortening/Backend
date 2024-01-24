package url.shortener.Avocado.infra.security.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import url.shortener.Avocado.infra.security.application.AuthService;
import url.shortener.Avocado.infra.security.dto.request.LoginRequestDto;
import url.shortener.Avocado.infra.security.dto.response.SignUpResponseDto;
import url.shortener.Avocado.infra.security.dto.response.TokenResponseDto;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login/oauth2/{provider}")
    public ResponseEntity<TokenResponseDto> login(@PathVariable String provider, @RequestParam("code") String accessCode) {
        TokenResponseDto response = authService.login(provider, accessCode);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.refreshToken())
                .maxAge(3600) // 1시간
                .path("/")
                .httpOnly(true)
//                .secure(true) https 설정 이후에
                .build();
        return ResponseEntity.ok().headers(headers -> headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString())).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody LoginRequestDto loginRequestDto) {
         SignUpResponseDto response = authService.signUp(loginRequestDto);
         return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<TokenResponseDto> verify(@RequestParam String token) {
        TokenResponseDto response = authService.verifyMember(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/local")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto response = authService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }
}