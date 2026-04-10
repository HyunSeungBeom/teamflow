package com.dookia.teamflow.auth.service;

import com.dookia.teamflow.auth.config.JwtProperties;
import com.dookia.teamflow.auth.dto.AuthDto;
import com.dookia.teamflow.auth.exception.AuthErrorCode;
import com.dookia.teamflow.auth.exception.AuthException;
import com.dookia.teamflow.token.entity.RefreshToken;
import com.dookia.teamflow.token.repository.RefreshTokenRepository;
import com.dookia.teamflow.user.entity.User;
import com.dookia.teamflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private GoogleOAuthService googleOAuthService;
    @Mock private JwtService jwtService;

    private JwtProperties jwtProperties;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(
            "test-secret-key-must-be-at-least-256-bits-long-aaaaaaaaaa",
            900,     // 15m
            604800   // 7d
        );
        authService = new AuthService(
            userRepository, refreshTokenRepository, googleOAuthService, jwtService, jwtProperties
        );
    }

    // ---------- login ----------

    @Test
    @DisplayName("신규 사용자 Google 로그인 → users 저장 + isNewUser=true")
    void login_newUser_createsUserAndReturnsIsNewUserTrue() {
        var googleInfo = new GoogleOAuthService.GoogleUserInfo("g-123", "new@x.com", "신규", "http://pic");
        given(googleOAuthService.exchangeCodeForUser("code", "uri")).willReturn(googleInfo);
        given(userRepository.findByGoogleId("g-123")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(inv -> {
            User u = inv.getArgument(0);
            injectId(u, UUID.randomUUID());
            return u;
        });
        given(jwtService.issueAccessToken(any(User.class))).willReturn("access.jwt.token");

        var result = authService.login(
            new AuthDto.GoogleLoginRequest("code", "uri"),
            "UA", "1.2.3.4"
        );

        assertThat(result.isNewUser()).isTrue();
        assertThat(result.accessToken()).isEqualTo("access.jwt.token");
        assertThat(result.refreshTokenPlain()).isNotBlank();
        verify(userRepository, times(1)).save(any(User.class));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("기존 사용자 Google 로그인 → last_login_at 갱신 + isNewUser=false")
    void login_existingUser_updatesLastLogin() {
        UUID userId = UUID.randomUUID();
        User existing = userWithId(userId, "g-999", "old@x.com", "기존");
        var googleInfo = new GoogleOAuthService.GoogleUserInfo("g-999", "old@x.com", "기존 갱신", "http://pic2");
        given(googleOAuthService.exchangeCodeForUser("code", "uri")).willReturn(googleInfo);
        given(userRepository.findByGoogleId("g-999")).willReturn(Optional.of(existing));
        given(jwtService.issueAccessToken(existing)).willReturn("access.jwt.token");

        var result = authService.login(
            new AuthDto.GoogleLoginRequest("code", "uri"),
            "UA", "1.2.3.4"
        );

        assertThat(result.isNewUser()).isFalse();
        assertThat(existing.getLastLoginAt()).isNotNull();
        assertThat(existing.getName()).isEqualTo("기존 갱신");
        verify(userRepository, never()).save(any(User.class));
    }

    // ---------- refresh ----------

    @Test
    @DisplayName("유효한 refresh token → Rotation 수행, 기존 토큰 used=true, 새 토큰 저장")
    void refresh_validToken_rotates() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        User user = userWithId(userId, "g-1", "a@b.com", "A");

        String plain = "plain-refresh-token-xyz";
        String hash = sha256Hex(plain);
        RefreshToken stored = RefreshToken.builder()
            .userId(userId)
            .tokenHash(hash)
            .familyId(familyId)
            .used(false)
            .expiresAt(OffsetDateTime.now().plus(Duration.ofDays(3)))
            .build();

        given(refreshTokenRepository.findByTokenHash(hash)).willReturn(Optional.of(stored));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(jwtService.issueAccessToken(user)).willReturn("new.access.token");

        var result = authService.refresh(plain, "UA", "1.2.3.4");

        assertThat(stored.isUsed()).isTrue();
        assertThat(result.accessToken()).isEqualTo("new.access.token");
        assertThat(result.refreshTokenPlain()).isNotEqualTo(plain);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getFamilyId()).isEqualTo(familyId); // 같은 family 유지
        assertThat(captor.getValue().isUsed()).isFalse();
    }

    @Test
    @DisplayName("이미 used=true 인 refresh token → Replay 감지 + family 전체 삭제 + AUTH_TOKEN_REUSED")
    void refresh_reusedToken_triggersReplayDetection() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String plain = "plain-refresh-token";
        String hash = sha256Hex(plain);
        RefreshToken stored = RefreshToken.builder()
            .userId(userId)
            .tokenHash(hash)
            .familyId(familyId)
            .used(true)                                         // 이미 사용됨
            .expiresAt(OffsetDateTime.now().plus(Duration.ofDays(1)))
            .build();

        given(refreshTokenRepository.findByTokenHash(hash)).willReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refresh(plain, "UA", "1.2.3.4"))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_REUSED);

        verify(refreshTokenRepository).deleteByFamilyId(familyId);
    }

    @Test
    @DisplayName("만료된 refresh token → AUTH_TOKEN_EXPIRED")
    void refresh_expiredToken_throwsExpired() {
        String plain = "plain-refresh-token";
        String hash = sha256Hex(plain);
        RefreshToken stored = RefreshToken.builder()
            .userId(UUID.randomUUID())
            .tokenHash(hash)
            .familyId(UUID.randomUUID())
            .used(false)
            .expiresAt(OffsetDateTime.now().minus(Duration.ofDays(1))) // 만료
            .build();

        given(refreshTokenRepository.findByTokenHash(hash)).willReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refresh(plain, "UA", "1.2.3.4"))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("쿠키 없이 refresh 호출 → AUTH_TOKEN_MISSING")
    void refresh_nullToken_throwsMissing() {
        assertThatThrownBy(() -> authService.refresh(null, "UA", "1.2.3.4"))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_MISSING);
    }

    @Test
    @DisplayName("DB에 존재하지 않는 refresh token → AUTH_TOKEN_INVALID")
    void refresh_unknownToken_throwsInvalid() {
        given(refreshTokenRepository.findByTokenHash(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("ghost-token", "UA", "1.2.3.4"))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_INVALID);
    }

    // ---------- logout ----------

    @Test
    @DisplayName("유효한 refresh token 로그아웃 → DB에서 해당 토큰 삭제")
    void logout_existingToken_deletes() {
        String plain = "plain-refresh-token";
        String hash = sha256Hex(plain);
        RefreshToken stored = RefreshToken.builder()
            .userId(UUID.randomUUID())
            .tokenHash(hash)
            .familyId(UUID.randomUUID())
            .used(false)
            .expiresAt(OffsetDateTime.now().plus(Duration.ofDays(1)))
            .build();
        given(refreshTokenRepository.findByTokenHash(hash)).willReturn(Optional.of(stored));

        authService.logout(plain);

        verify(refreshTokenRepository).delete(stored);
    }

    @Test
    @DisplayName("쿠키 없이 로그아웃 → 예외 없이 조용히 통과")
    void logout_nullToken_noop() {
        authService.logout(null);
        verify(refreshTokenRepository, never()).delete(any());
    }

    // ---------- helpers ----------

    private static String sha256Hex(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static User userWithId(UUID id, String googleId, String email, String name) {
        User user = User.createFromGoogle(googleId, email, name, null);
        injectId(user, id);
        return user;
    }

    private static void injectId(User user, UUID id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
