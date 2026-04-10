package com.dookia.teamflow.auth.dto;

import com.dookia.teamflow.user.entity.User;
import jakarta.validation.constraints.NotBlank;

/**
 * 인증 도메인 요청/응답 DTO 모음. backend-conventions.md 규칙에 따라 {Domain}Dto.java 한 파일에 inner record로 선언한다.
 */
public class AuthDto {

    private AuthDto() {
    }

    public record GoogleLoginRequest(
        @NotBlank(message = "code는 필수입니다.") String code,
        @NotBlank(message = "redirectUri는 필수입니다.") String redirectUri
    ) {}

    public record AuthResponse(
        String accessToken,
        UserInfo user,
        boolean isNewUser
    ) {
        public static AuthResponse of(String accessToken, User user, boolean isNewUser) {
            return new AuthResponse(accessToken, UserInfo.from(user), isNewUser);
        }
    }

    public record TokenRefreshResponse(String accessToken) {
    }

    public record UserInfo(
        String id,
        String email,
        String name,
        String avatarUrl,
        String statusMessage
    ) {
        public static UserInfo from(User user) {
            return new UserInfo(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                user.getStatusMessage()
            );
        }
    }
}
