package url.shortener.Avocado.infra.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E201", "User Not Found"),
    USER_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "E202", "Email Not Verified"),
    USER_NOT_REGISTERED(HttpStatus.NOT_FOUND, "E203", "User Doesn't Exist"),
    USER_EXIST(HttpStatus.BAD_REQUEST, "E204", "User Exist"),
    OAUTH_USER(HttpStatus.BAD_REQUEST, "E205", "OAuth User"),
    LOCAL_USER(HttpStatus.BAD_REQUEST, "E206", "Local User"),
    PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "E207", "Password Invalid"),
    PROVIDER_INVALID(HttpStatus.BAD_REQUEST, "E208", "Invalid Provider"),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E301", "Token has Expired"),
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "E302", "Token is Empty"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "E303", "Invalid Token"),
    VERIFY_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E304", "Password Invalid");



    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;

    }
}
