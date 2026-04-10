package com.dookia.teamflow.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * auth-design.md §4에 정의된 인증 에러 코드 목록.
 */
@Getter
public enum AuthErrorCode {

    AUTH_MISSING_CODE(HttpStatus.BAD_REQUEST, "code 파라미터가 필요합니다."),
    AUTH_INVALID_CODE(HttpStatus.BAD_REQUEST, "Authorization code가 유효하지 않습니다."),
    AUTH_GOOGLE_ERROR(HttpStatus.BAD_GATEWAY, "Google 서버 통신에 실패했습니다."),

    AUTH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "인증 토큰이 없습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    AUTH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "사용된 토큰이 재사용되었습니다. 모든 세션이 무효화되었습니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String code() {
        return this.name();
    }
}
