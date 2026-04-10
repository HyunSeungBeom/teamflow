package com.dookia.teamflow.auth.service;

import com.dookia.teamflow.auth.config.JwtProperties;
import com.dookia.teamflow.auth.exception.AuthErrorCode;
import com.dookia.teamflow.auth.exception.AuthException;
import com.dookia.teamflow.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-must-be-at-least-256-bits-long-aaaaaaaaaa";
    private static final long ACCESS_TTL = 900;      // 15분
    private static final long REFRESH_TTL = 604800;  // 7일

    private final JwtService jwtService = new JwtService(
        new JwtProperties(SECRET, ACCESS_TTL, REFRESH_TTL)
    );

    @Test
    @DisplayName("access token 생성 후 subject 파싱 시 user.id 반환")
    void issueAccessToken_parseUserId_success() {
        User user = userWithId(UUID.randomUUID(), "a@b.com", "홍길동");

        String token = jwtService.issueAccessToken(user);
        UUID parsed = jwtService.parseUserId(token);

        assertThat(token).isNotBlank();
        assertThat(parsed).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("만료된 토큰은 AUTH_TOKEN_EXPIRED 예외")
    void parseUserId_expired_throwsExpired() {
        User user = userWithId(UUID.randomUUID(), "a@b.com", "홍길동");
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(Date.from(Instant.now().minus(Duration.ofHours(2))))
            .expiration(Date.from(Instant.now().minus(Duration.ofHours(1))))
            .signWith(key)
            .compact();

        assertThatThrownBy(() -> jwtService.parseUserId(expired))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("변조된 토큰은 AUTH_TOKEN_INVALID 예외")
    void parseUserId_tampered_throwsInvalid() {
        User user = userWithId(UUID.randomUUID(), "a@b.com", "홍길동");
        String token = jwtService.issueAccessToken(user);
        String tampered = token.substring(0, token.length() - 4) + "AAAA";

        assertThatThrownBy(() -> jwtService.parseUserId(tampered))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_INVALID);
    }

    @Test
    @DisplayName("다른 시크릿으로 서명된 토큰은 AUTH_TOKEN_INVALID 예외")
    void parseUserId_wrongSecret_throwsInvalid() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
            "different-secret-key-also-at-least-256-bits-long-bbbbbbbb".getBytes(StandardCharsets.UTF_8)
        );
        String foreign = Jwts.builder()
            .subject(UUID.randomUUID().toString())
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(10))))
            .signWith(otherKey)
            .compact();

        assertThatThrownBy(() -> jwtService.parseUserId(foreign))
            .isInstanceOf(AuthException.class)
            .extracting("errorCode")
            .isEqualTo(AuthErrorCode.AUTH_TOKEN_INVALID);
    }

    // Test helper: User 엔티티의 id는 DB 생성이므로 테스트에서는 reflection으로 주입한다.
    private static User userWithId(UUID id, String email, String name) {
        User user = User.createFromGoogle("google-" + id, email, name, null);
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return user;
    }
}
