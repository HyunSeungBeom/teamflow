package com.dookia.teamflow.dto;

import java.time.OffsetDateTime;

/**
 * auth-design.md §4.4 의 표준 에러 응답 포맷.
 *
 * <pre>
 * {
 *   "error": { "code": "AUTH_TOKEN_EXPIRED", "message": "..." },
 *   "timestamp": "2026-04-10T12:00:00Z"
 * }
 * </pre>
 */
public record ErrorResponse(ErrorBody error, String timestamp) {

    public record ErrorBody(String code, String message) {}

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(new ErrorBody(code, message), OffsetDateTime.now().toString());
    }
}
