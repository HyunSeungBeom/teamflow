-- ============================================================================
-- V1: 인증 도메인 초기 스키마 (auth-design.md §3)
--   - users         : Google OAuth 기반 사용자
--   - refresh_tokens: Token Rotation + Replay Detection용 저장소
-- ============================================================================

-- users ----------------------------------------------------------------------
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_id       VARCHAR(255) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    avatar_url      VARCHAR(500),
    status_message  VARCHAR(200),
    provider        VARCHAR(20)  NOT NULL DEFAULT 'google',
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- refresh_tokens -------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(128) NOT NULL UNIQUE,
    family_id   UUID NOT NULL,
    used        BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    user_agent  VARCHAR(500),
    -- auth-design 문서는 INET이지만 운영 편의를 위해 VARCHAR(45)로 저장한다.
    -- IPv4/IPv6 모두 최대 45자 이내에 들어오며, JPA 매핑이 단순해진다.
    ip_address  VARCHAR(45)
);

-- 인덱스 (PK/UNIQUE는 자동 생성되므로 보조 인덱스만)
CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_family_id  ON refresh_tokens(family_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
