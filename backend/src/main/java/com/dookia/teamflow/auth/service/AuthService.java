package com.dookia.teamflow.auth.service;

import com.dookia.teamflow.auth.config.JwtProperties;
import com.dookia.teamflow.auth.dto.AuthDto;
import com.dookia.teamflow.auth.entity.RefreshToken;
import com.dookia.teamflow.exception.AuthErrorCode;
import com.dookia.teamflow.exception.AuthException;
import com.dookia.teamflow.auth.oauth.OAuthProvider;
import com.dookia.teamflow.auth.oauth.OAuthProviderRegistry;
import com.dookia.teamflow.auth.oauth.OAuthUserInfo;
import com.dookia.teamflow.auth.repository.RefreshTokenRepository;
import com.dookia.teamflow.user.entity.User;
import com.dookia.teamflow.user.entity.UserProvider;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * 인증 핵심 비즈니스 로직. auth-design.md §1~§2, ERD v0.1 §1 을 구현한다.
 *
 * <ul>
 *   <li>{@link #oauthLogin} — OAuth 제공자(Google/Naver/Kakao …) 로그인/회원가입</li>
 *   <li>{@link #refresh} — Token Rotation + Replay Detection</li>
 *   <li>{@link #logout} — Refresh Token 폐기</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_RAW_BYTES = 48;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthProviderRegistry oauthProviderRegistry;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public LoginResult oauthLogin(UserProvider provider, AuthDto.OAuthLoginRequest request, String userAgent, String ipAddress) {
        OAuthProvider oauth = oauthProviderRegistry.get(provider);
        OAuthUserInfo oauthUser = oauth.exchangeCodeForUser(request.code(), request.redirectUri());

        Optional<User> existing = userRepository.findByProviderAndProviderId(provider, oauthUser.providerId());
        boolean isNewUser = existing.isEmpty();

        User user = existing.orElseGet(() -> userRepository.save(
            User.createFromOAuth(
                provider,
                oauthUser.providerId(),
                oauthUser.email(),
                oauthUser.name(),
                oauthUser.picture()
            )
        ));

        if (!isNewUser) {
            user.updateProfile(oauthUser.name(), oauthUser.picture());
        }

        String accessToken = jwtService.issueAccessToken(user);
        IssuedRefreshToken refresh = issueRefreshToken(user, UUID.randomUUID().toString(), userAgent, ipAddress);

        return new LoginResult(accessToken, refresh.plainToken(), user, isNewUser, refresh.expiresAt());
    }

    public RefreshResult refresh(String plainRefreshToken, String userAgent, String ipAddress) {
        if (plainRefreshToken == null || plainRefreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_MISSING);
        }

        String hash = sha256Hex(plainRefreshToken);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash)
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_TOKEN_INVALID));

        if (refreshToken.isExpired()) {
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_EXPIRED);
        }

        if (refreshToken.isUsed()) {
            log.warn("Refresh token replay detected. familyId={}, userNo={}", refreshToken.getFamilyId(), refreshToken.getUserNo());
            refreshTokenRepository.deleteByFamilyId(refreshToken.getFamilyId());
            throw new AuthException(AuthErrorCode.AUTH_TOKEN_REUSED);
        }

        User user = userRepository.findById(refreshToken.getUserNo())
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_TOKEN_INVALID));

        // 새 토큰 발급을 먼저 완료한 뒤 기존 토큰을 markUsed 로 마감한다.
        // 이 순서 덕분에 중간 예외 시에는 기존 토큰이 여전히 유효 → 사용자가 재시도 가능 (강제 로그아웃 방지).
        String accessToken = jwtService.issueAccessToken(user);
        IssuedRefreshToken newRefresh = issueRefreshToken(user, refreshToken.getFamilyId(), userAgent, ipAddress);

        refreshToken.markUsed();
        Duration remainingTtl = Duration.between(LocalDateTime.now(), refreshToken.getExpireDate());
        if (!remainingTtl.isNegative() && !remainingTtl.isZero()) {
            refreshTokenRepository.save(refreshToken, remainingTtl);
        }

        return new RefreshResult(accessToken, newRefresh.plainToken(), user, newRefresh.expiresAt());
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

    private IssuedRefreshToken issueRefreshToken(User user, String familyId, String userAgent, String ipAddress) {
        byte[] randomBytes = new byte[REFRESH_TOKEN_RAW_BYTES];
        RANDOM.nextBytes(randomBytes);
        String plain = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String hash = sha256Hex(plain);

        Duration ttl = Duration.ofSeconds(jwtProperties.refreshTokenTtlSeconds());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plus(ttl);

        RefreshToken refreshToken = RefreshToken.builder()
            .userNo(user.getNo())
            .tokenHash(hash)
            .familyId(familyId)
            .used(false)
            .userAgent(userAgent != null ? userAgent : "unknown")
            .ipAddress(ipAddress)
            .expireDate(expiresAt)
            .createDate(now)
            .build();

        refreshTokenRepository.save(refreshToken, ttl);
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
        LocalDateTime refreshTokenExpiresAt
    ) {}

    public record RefreshResult(
        String accessToken,
        String refreshTokenPlain,
        User user,
        LocalDateTime refreshTokenExpiresAt
    ) {}

    private record IssuedRefreshToken(String plainToken, LocalDateTime expiresAt) {}
}
