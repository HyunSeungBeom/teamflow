package com.dookia.teamflow.auth.repository;

import com.dookia.teamflow.auth.entity.RefreshToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Redis 기반 RefreshToken 저장소. StringRedisTemplate 직접 호출로 키 구조/TTL/인덱스를 명시 관리한다.
 *
 * <ul>
 *   <li>토큰 본체     : {@code refresh:token:{tokenHash}} (String) — JSON 직렬화, TTL=save 호출자가 전달</li>
 *   <li>family 인덱스 : {@code refresh:family:{familyId}}  (Set)    — 멤버={tokenHash}, 동일 TTL</li>
 *   <li>삭제 흐름      : SMEMBERS → 다수 DEL (토큰) + DEL (family key) 조합으로 1회 전송</li>
 * </ul>
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String TOKEN_KEY_PREFIX = "refresh:token:";
    private static final String FAMILY_KEY_PREFIX = "refresh:family:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 토큰을 저장하고 family 인덱스를 갱신한다.
     *
     * @param token 저장할 refresh 토큰
     * @param ttl   Redis key TTL. markUsed 업데이트 시엔 잔여 만료 시간을,
     *              신규 발급 시엔 전체 refresh 토큰 수명을 전달한다.
     */
    public RefreshToken save(RefreshToken token, Duration ttl) {
        String tokenKey = tokenKey(token.getTokenHash());
        String familyKey = familyKey(token.getFamilyId());

        redisTemplate.opsForValue().set(tokenKey, serialize(token), ttl);
        redisTemplate.opsForSet().add(familyKey, token.getTokenHash());
        redisTemplate.expire(familyKey, ttl);
        return token;
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        String json = redisTemplate.opsForValue().get(tokenKey(tokenHash));
        if (json == null) {
            return Optional.empty();
        }
        return Optional.of(deserialize(json));
    }

    public void delete(RefreshToken token) {
        redisTemplate.delete(tokenKey(token.getTokenHash()));
        redisTemplate.opsForSet().remove(familyKey(token.getFamilyId()), token.getTokenHash());
    }

    /**
     * Replay Detection: family 전체 토큰을 일괄 제거한다.
     */
    public void deleteByFamilyId(String familyId) {
        String familyKey = familyKey(familyId);
        Set<String> hashes = redisTemplate.opsForSet().members(familyKey);
        if (hashes != null && !hashes.isEmpty()) {
            List<String> tokenKeys = hashes.stream().map(this::tokenKey).toList();
            redisTemplate.delete(tokenKeys);
        }
        redisTemplate.delete(familyKey);
    }

    // --- helpers ------------------------------------------------------------

    private String tokenKey(String tokenHash) {
        return TOKEN_KEY_PREFIX + tokenHash;
    }

    private String familyKey(String familyId) {
        return FAMILY_KEY_PREFIX + familyId;
    }

    private String serialize(RefreshToken token) {
        try {
            return objectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("RefreshToken 직렬화 실패", e);
        }
    }

    private RefreshToken deserialize(String json) {
        try {
            return objectMapper.readValue(json, RefreshToken.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("RefreshToken 역직렬화 실패", e);
        }
    }
}
