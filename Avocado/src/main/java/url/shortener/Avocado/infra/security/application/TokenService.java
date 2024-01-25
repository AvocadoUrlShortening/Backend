package url.shortener.Avocado.infra.security.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import url.shortener.Avocado.infra.security.exception.AuthErrorCode;
import url.shortener.Avocado.infra.security.exception.AuthException;

import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${secret-key}")
    private String jwtKey;
    private byte[] secret;

    @PostConstruct
    public void init() {
        secret = Base64.getDecoder().decode(jwtKey);
    }
    private final Long ACCESS_EXP = 1800L * 1000; // 30 Miniute, 1000 for milisec
    private final Long REFRESH_EXP = 3 * 3600L * 1000; // 3 hour
    private final Long VERIFY_EXP = 3 * 1800L * 100; // 3 Miniute

    public String createVerifyToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(Keys.hmacShaKeyFor(secret))
                .setExpiration(new Date(System.currentTimeMillis() + VERIFY_EXP))
                .compact();
    }
    public String createAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(Keys.hmacShaKeyFor(secret))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(Keys.hmacShaKeyFor(secret))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            return getClaims(token)
                    .getExpiration()
                    .after(new Date());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // 서명이 일치하지 않는 경우
            System.err.println("Invalid signature!");
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰이 만료된 경우
            System.err.println("Token has expired!");
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // 토큰의 형식이 잘못된 경우
            System.err.println("Malformed token!");
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        } catch (io.jsonwebtoken.UnsupportedJwtException | io.jsonwebtoken.security.SecurityException e) {
            // 지원되지 않는 JWT 형식 또는 보안 예외
            System.err.println("Unsupported JWT or security exception!");
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}


