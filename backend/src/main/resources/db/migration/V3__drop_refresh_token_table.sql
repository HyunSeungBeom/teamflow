-- ============================================================================
-- V3: refresh_token 테이블 제거
--   Sprint 2 PR #2 리뷰 Consider 반영 — RefreshToken 저장소를 PostgreSQL 에서 Redis 로 전환.
--   장점: TTL 자동 만료, 쓰기 처리량, 토큰 볼륨 증가에 강함, auth-design §2.2 의 원안과 일치.
--
--   되돌리기 전략: 롤백이 필요하면 V1__init_auth_schema.sql 의 refresh_token 블록을 그대로 재생성한다.
-- ============================================================================

DROP INDEX IF EXISTS idx_rt_user;
DROP INDEX IF EXISTS idx_rt_family;
DROP INDEX IF EXISTS idx_rt_expire;
DROP TABLE IF EXISTS refresh_token;
