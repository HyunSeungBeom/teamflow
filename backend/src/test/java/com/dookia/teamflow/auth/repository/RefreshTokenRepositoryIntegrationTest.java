package com.dookia.teamflow.auth.repository;

import com.dookia.teamflow.auth.entity.RefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RefreshTokenRepository 통합 테스트. 실제 Redis 컨테이너(Testcontainers) 에 대해
 * 저장/조회/family 삭제/TTL 동작을 검증한다.
 *
 * Docker 가 없는 환경(CI 제외)에서는 {@link Testcontainers#disabledWithoutDocker()} 로 자동 skip.
 */
@Testcontainers(disabledWithoutDocker = true)
class RefreshTokenRepositoryIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);

    private static LettuceConnectionFactory connectionFactory;
    private static StringRedisTemplate redisTemplate;
    private static ObjectMapper objectMapper;
    private static RefreshTokenRepository repository;

    @BeforeAll
    static void setUpAll() {
        connectionFactory = new LettuceConnectionFactory(REDIS.getHost(), REDIS.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate(connectionFactory);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        repository = new RefreshTokenRepository(redisTemplate, objectMapper);
    }

    @AfterEach
    void flushRedis() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }

    @Test
    @DisplayName("save → findByTokenHash 로 저장한 토큰을 동일 값으로 복원")
    void save_thenFindByTokenHash_returnsSameToken() {
        RefreshToken token = sampleToken("hash-1", "fam-1", 10L, false);

        repository.save(token, Duration.ofMinutes(10));

        Optional<RefreshToken> loaded = repository.findByTokenHash("hash-1");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getFamilyId()).isEqualTo("fam-1");
        assertThat(loaded.get().getUserNo()).isEqualTo(10L);
        assertThat(loaded.get().isUsed()).isFalse();
    }

    @Test
    @DisplayName("save → Redis key 에 TTL 이 설정되어 있다")
    void save_setsTtlOnKey() {
        RefreshToken token = sampleToken("hash-ttl", "fam-ttl", 1L, false);

        repository.save(token, Duration.ofSeconds(30));

        Long ttl = redisTemplate.getExpire("refresh:token:hash-ttl");
        assertThat(ttl).isNotNull();
        assertThat(ttl).isBetween(1L, 30L);
    }

    @Test
    @DisplayName("save → family Set 에 tokenHash 가 멤버로 추가된다")
    void save_addsMemberToFamilySet() {
        repository.save(sampleToken("hash-a", "fam-shared", 1L, false), Duration.ofMinutes(10));
        repository.save(sampleToken("hash-b", "fam-shared", 1L, false), Duration.ofMinutes(10));

        Boolean hasA = redisTemplate.opsForSet().isMember("refresh:family:fam-shared", "hash-a");
        Boolean hasB = redisTemplate.opsForSet().isMember("refresh:family:fam-shared", "hash-b");
        Long size = redisTemplate.opsForSet().size("refresh:family:fam-shared");

        assertThat(hasA).isTrue();
        assertThat(hasB).isTrue();
        assertThat(size).isEqualTo(2L);
    }

    @Test
    @DisplayName("save 재호출 → 같은 tokenHash 에 대해 값 덮어쓰기 (markUsed 반영)")
    void save_overwritesExistingTokenForMarkUsed() {
        RefreshToken token = sampleToken("hash-over", "fam-over", 1L, false);
        repository.save(token, Duration.ofMinutes(10));

        token.markUsed();
        repository.save(token, Duration.ofMinutes(5));

        RefreshToken loaded = repository.findByTokenHash("hash-over").orElseThrow();
        assertThat(loaded.isUsed()).isTrue();
    }

    @Test
    @DisplayName("delete → token key 와 family 멤버가 함께 제거된다")
    void delete_removesTokenAndFamilyMember() {
        RefreshToken a = sampleToken("hash-del-a", "fam-del", 1L, false);
        RefreshToken b = sampleToken("hash-del-b", "fam-del", 1L, false);
        repository.save(a, Duration.ofMinutes(10));
        repository.save(b, Duration.ofMinutes(10));

        repository.delete(a);

        assertThat(repository.findByTokenHash("hash-del-a")).isEmpty();
        assertThat(repository.findByTokenHash("hash-del-b")).isPresent();
        Boolean hasA = redisTemplate.opsForSet().isMember("refresh:family:fam-del", "hash-del-a");
        assertThat(hasA).isFalse();
    }

    @Test
    @DisplayName("deleteByFamilyId → family 전체 토큰 + family Set 모두 제거")
    void deleteByFamilyId_wipesEntireFamily() {
        repository.save(sampleToken("h1", "fam-wipe", 1L, false), Duration.ofMinutes(10));
        repository.save(sampleToken("h2", "fam-wipe", 1L, false), Duration.ofMinutes(10));
        repository.save(sampleToken("h3", "fam-other", 1L, false), Duration.ofMinutes(10));

        repository.deleteByFamilyId("fam-wipe");

        assertThat(repository.findByTokenHash("h1")).isEmpty();
        assertThat(repository.findByTokenHash("h2")).isEmpty();
        assertThat(repository.findByTokenHash("h3")).isPresent(); // 다른 family 는 영향 없음
        Boolean familyExists = redisTemplate.hasKey("refresh:family:fam-wipe");
        assertThat(familyExists).isFalse();
    }

    @Test
    @DisplayName("findByTokenHash → 존재하지 않는 해시는 Optional.empty")
    void findByTokenHash_missingIsEmpty() {
        assertThat(repository.findByTokenHash("nonexistent-hash")).isEmpty();
    }

    // --- helpers ------------------------------------------------------------

    private RefreshToken sampleToken(String hash, String familyId, long userNo, boolean used) {
        return RefreshToken.builder()
            .tokenHash(hash)
            .familyId(familyId)
            .userNo(userNo)
            .used(used)
            .userAgent("UA")
            .ipAddress("127.0.0.1")
            .expireDate(LocalDateTime.now().plusMinutes(10))
            .createDate(LocalDateTime.now())
            .build();
    }

    @SuppressWarnings("unused")
    private static String random() {
        return UUID.randomUUID().toString();
    }
}
