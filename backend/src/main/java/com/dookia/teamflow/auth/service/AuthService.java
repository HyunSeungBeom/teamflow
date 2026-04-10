package com.dookia.teamflow.auth.service;

import com.dookia.teamflow.auth.config.JwtProperties;
import com.dookia.teamflow.auth.dto.AuthDto;
import com.dookia.teamflow.auth.exception.AuthErrorCode;
import com.dookia.teamflow.auth.exception.AuthException;
import com.dookia.teamflow.token.entity.RefreshToken;
import com.dookia.teamflow.token.repository.RefreshTokenRepository;
import com.dookia.teamflow.user.entity.User;
import com.dookia.teamflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * 인증 핵심 비즈니스 로직. auth-design.md §1~§2 를 구현한다.
 *
 * <ul>
 *   <li>{@link #login(AuthDto.GoogleLoginRequest, String, String)} — Google OAuth 로그인/회원가입</li>
 *   <li>{@link #refresh(String, String, String)} — Token Rotation + Replay Detection</li>
 *   <li>{@link #logout(String)} — Refresh Token 폐기</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_RAW_BYTES = 48; // base64url 64자

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleOAuthService googleOAuthService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public LoginResult login(AuthDto.GoogleLoginRequest request, String userAgent, String ipAddress) {
        var googleUser = googleOAuthService.exchangeCodeForUser(request.code(), request.redirectUri());

        Optional<User> existing = userRepository.findByGoogleId(googleUser.sub());
        boolean isNewUser = existing.isEmpty();

        User user = existing.orElseGet(() -> userRepository.save(
            User.createFromGoogle(
                googleUser.sub(),
                googleUser.email(),
                googleUser.name(),
                googleUser.picture()
            )
        ));

        // 기존 사용자 프로필 갱신 (Google 쪽 변경 반영)
        if (!isNewUser) {
            user.updateProfile(googleUser.name(), googleUser.picture());
        }
        user.markLogin(OffsetDateTime.now());

        String accessToken = jwtService.issueAccessToken(user);
        IssuedRefreshToken refresh = issueRefreshToken(user, UUID.randomUUID(), userAgent, ipAddress);

        return new LoginResult(accessToken, refresh.plainToken(), user, isNewUser, refresh.expiresAt());
    }

    public RefreshResult refresh(String plainRefreshToken, String userAgent, String ipAddress) {
        if (plainRefreshToken == null || plainRefreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_MISSING);
        }

        String hash = sha256Hex(plainRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_TOKEN_INVALID));

        if (stored.isExpired()) {
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_EXPIRED);
        }

        if (stored.isUsed()) {
            // Replay Detection: family 전체 무효화 (auth-design §2.3)
            log.warn("Refresh token replay detected. familyId={}, userId={}",
                stored.getFamilyId(), stored.getUserId());
            refreshTokenRepository.deleteByFamilyId(stored.getFamilyId());
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_REUSED);
        }

        User user = userRepository.findById(stored.getUserId())
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_TOKEN_INVALID));

        // Rotation: 기존 토큰 used 마킹 후 새 토큰 발급 (같은 family 유지)
        stored.markUsed();

        String accessToken = jwtService.issueAccessToken(user);
        IssuedRefreshToken newRefresh = issueRefreshToken(user, stored.getFamilyId(), userAgent, ipAddress);

        return new RefreshResult(accessToken, newRefresh.plainToken(), newRefresh.expiresAt());
    }

    public void logout(String plainRefreshToken) {
        if (plainRefreshToken == null || plainRefreshToken.isBlank()) {
            return;
        }
        String hash = sha256Hex(plainRefreshToken);
        refreshTokenRepository.findByTokenHash(hash)
            .ifPresent(refreshTokenRepository::delete);
    }

    // --- internal helpers ---------------------------------------------------

    private IssuedRefreshToken issueRefreshToken(User user, UUID familyId, String userAgent, String ipAddress) {
        byte[] randomBytes = new byte[REFRESH_TOKEN_RAW_BYTES];
        RANDOM.nextBytes(randomBytes);
        String plain = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String hash = sha256Hex(plain);

        OffsetDateTime expiresAt = OffsetDateTime.now()
            .plus(Duration.ofSeconds(jwtProperties.refreshTokenTtlSeconds()));

        RefreshToken entity = RefreshToken.builder()
            .userId(user.getId())
            .tokenHash(hash)
            .familyId(familyId)
            .used(false)
            .expiresAt(expiresAt)
            .userAgent(userAgent)
            .ipAddress(ipAddress)
            .build();

        refreshTokenRepository.save(entity);
        return new IssuedRefreshToken(plain, expiresAt);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    // --- result records -----------------------------------------------------

    public record LoginResult(
        String accessToken,
        String refreshTokenPlain,
        User user,
        boolean isNewUser,
        OffsetDateTime refreshTokenExpiresAt
    ) {
    }

    public record RefreshResult(
        String accessToken,
        String refreshTokenPlain,
        OffsetDateTime refreshTokenExpiresAt
    ) {
    }

    private record IssuedRefreshToken(String plainToken, OffsetDateTime expiresAt) {
    }
}
