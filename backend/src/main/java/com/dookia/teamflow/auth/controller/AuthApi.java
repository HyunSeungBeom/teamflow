package com.dookia.teamflow.auth.controller;

import com.dookia.teamflow.auth.dto.AuthDto;
import com.dookia.teamflow.user.entity.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Auth 컨트롤러의 OpenAPI 계약. 실제 라우팅/구현은 {@link AuthController} 가 담당한다.
 * springdoc-openapi 가 인터페이스의 어노테이션을 구현체 메서드와 매칭해 문서를 생성한다.
 */
@Tag(name = "Auth", description = "OAuth 로그인 · 토큰 갱신 · 로그아웃")
public interface AuthApi {

    @Operation(
        summary = "OAuth 로그인/회원가입",
        description = "OAuth Authorization Code Flow: 프론트가 전달한 code 를 path 의 provider(google/naver/…) "
            + "token endpoint 에 교환해 사용자 정보를 얻고 로그인 또는 신규 가입을 처리한다. "
            + "Access Token 은 응답 JSON 으로, Refresh Token 은 httpOnly Cookie(teamflow_rt) 로 전달된다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "AUTH_MISSING_CODE / AUTH_INVALID_CODE / AUTH_UNSUPPORTED_PROVIDER"),
        @ApiResponse(responseCode = "502", description = "AUTH_GOOGLE_ERROR — 외부 OAuth 서버 통신 실패")
    })
    @SecurityRequirements
    ResponseEntity<AuthDto.AuthResponse> oauthLogin(
        @PathVariable UserProvider provider,
        @Valid @RequestBody AuthDto.OAuthLoginRequest request,
        HttpServletRequest httpRequest
    );

    @Operation(
        summary = "Access Token 갱신 (Token Rotation)",
        description = "Cookie 의 teamflow_rt 를 검증해 새 Access Token 과 새 Refresh Token 을 발급한다. "
            + "이전 Refresh Token 은 used=true 로 마킹되며, 재사용 감지 시 family 전체가 무효화된다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "갱신 성공"),
        @ApiResponse(responseCode = "401",
            description = "AUTH_TOKEN_MISSING / AUTH_TOKEN_EXPIRED / AUTH_TOKEN_INVALID / AUTH_TOKEN_REUSED")
    })
    @SecurityRequirements
    ResponseEntity<AuthDto.TokenRefreshResponse> refresh(
        @CookieValue(name = AuthController.REFRESH_COOKIE_NAME, required = false) String refreshToken,
        HttpServletRequest httpRequest
    );

    @Operation(
        summary = "로그아웃",
        description = "Cookie 의 teamflow_rt 에 해당하는 Refresh Token 을 DB 에서 삭제하고, "
            + "Set-Cookie 로 Cookie 를 즉시 만료시킨다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "로그아웃 처리 완료")
    })
    ResponseEntity<Void> logout(
        @CookieValue(name = AuthController.REFRESH_COOKIE_NAME, required = false) String refreshToken
    );
}
