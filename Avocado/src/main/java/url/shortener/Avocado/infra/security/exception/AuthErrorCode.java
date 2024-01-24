package url.shortener.Avocado.infra.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E201", "Token has Expired"),
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "E202", "Token is Empty"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "E203", "Invalid Token"),
    USER_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "E204", "Email Not Verified"),
    PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "E205", "Password Invalid"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E301", "User Not Found"),
    PROVIDER_INVALID(HttpStatus.BAD_REQUEST, "E302", "Invalid Provider"),
    USER_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "E303", "Corrupt User Data"),

    OAUTH_USER(HttpStatus.BAD_REQUEST, "E304", "OAuth User"),
    LOCAL_USER(HttpStatus.BAD_REQUEST, "E305", "Local User"),
    USER_NOT_REGISTERD(HttpStatus.NOT_FOUND, "E306", "User Doesn't Exist"),
    USER_EXIST(HttpStatus.BAD_REQUEST, "E307", "User Exist"),
    VERIFY_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E308", "Password Invalid");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;

    }
}
