# Sprint 2 — 리스크 레지스터

---

## 리스크 목록

| ID | 리스크 | 심각도 | 확률 | 영향 | 완화 전략 | 상태 |
|----|--------|--------|------|------|----------|------|
| RISK-S2-001 | DnD 라이브러리 선택 | MEDIUM | 중 | 칸반 보드 UX 품질 | dnd-kit vs @hello-pangea/dnd 사전 PoC | OPEN |
| RISK-S2-002 | 티켓 순서(position) 동시성 | MEDIUM | 중 | 여러 사용자 동시 드래그 시 순서 꼬임 | 낙관적 업데이트 + 서버 정렬 기준 확보 | OPEN |
| RISK-S2-003 | 백엔드 CORS 미설정 | HIGH | 높 | 프론트-백 통합 불가 | Sprint 1에서 이미 발생. 백엔드에 localhost CORS 추가 요청 | OPEN |
| RISK-S2-004 | ticket_counter 동시성 | LOW | 낮 | 티켓 키 중복 가능성 | DB 레벨 UNIQUE 제약조건으로 방어 | RESOLVED |
| RISK-S2-005 | 워크스페이스 라우팅 복잡도 | LOW | 낮 | 중첩 라우트 관리 | react-router nested routes 패턴 적용 | OPEN |
| RISK-S2-006 | V3 migration — refresh_token 테이블 DROP | HIGH | 높 | 배포 시점 전체 사용자 강제 로그아웃, DROP 이후 롤백 불가 | (1) Redis 가용 확인 → (2) 앱 배포 → (3) Flyway V3 실행. 롤백 필요 시 V1 block 재생성 + 이전 앱 이미지 rollout. | MITIGATED |

---

## 브라운필드 영향 맵

### Sprint 1 코드에 영향 주는 변경

| 영역 | 영향 | 변경 사항 |
|------|------|----------|
| App.tsx 라우팅 | 변경 | 워크스페이스 중첩 라우트 추가 |
| ProjectsPage.tsx | 변경 | 프로젝트 카드 클릭 → 워크스페이스 라우팅 연결 |
| MainLayout.tsx | 유지 | 프로젝트 허브에서는 기존 레이아웃, 워크스페이스에서는 별도 레이아웃 |
| Backend: Project 엔티티 | 유지 | ticket_counter 이미 존재 |
| Backend: SecurityConfig | 변경 | 티켓 API 경로 인증 설정 추가 |

---

## 미결정 사항

| 항목 | 결정자 | 기한 | 결정 |
|------|--------|------|------|
| DnD 라이브러리 최종 선택 | Frontend | Week 3 초 | — |
| 티켓 삭제 방식 (soft vs hard delete) | Backend | Week 3 초 | **Soft delete** (2026-04-20 확정). `delete_date` TIMESTAMPTZ NULL 컬럼 + `delete_date IS NULL` 필터. 복구/감사 대비 + 외부 참조(담당자, 댓글 등 후속 스프린트) 안정성. |
| 티켓 상세 UI (사이드 패널 vs 모달 vs 전체 페이지) | Frontend + Design | Week 3 초 | — |

---

## V3 마이그레이션 런북 — refresh_token RDB → Redis 전환

### 배경
- PR #2 코드 리뷰 Consider 반영 — RefreshToken 저장소를 PostgreSQL 에서 Redis(RedisTemplate 기반)로 전환
- 관련 파일: `V3__drop_refresh_token_table.sql`, `RefreshToken.java`, `RefreshTokenRepository.java`, `RedisConfig.java`
- 참조 리스크: **RISK-S2-006**

### 배포 순서 (엄수)

1. **사전 점검**
   - Redis 인스턴스 가용성 확인: `redis-cli -h <host> -p <port> ping` → `PONG`
   - 환경 변수 확인: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` 설정
   - 앱 Actuator 헬스 체크 엔드포인트 준비
2. **앱 배포** (Flyway V3 실행 전에 새 코드를 먼저 넣는다)
   - 새 AuthService 활성화 (Redis 기반 저장/조회)
   - 배포 후 `POST /api/auth/refresh` 로 정상 동작 smoke test
   - Redis 에 `refresh:token:*` 키가 생성되는지 `redis-cli KEYS 'refresh:token:*'` 로 확인
3. **Flyway V3 실행**
   - `./gradlew flywayMigrate` 또는 앱 기동 시 자동 실행
   - `refresh_token` 테이블 DROP. 이후 JPA 스키마 validate 가 통과해야 정상
4. **사후 검증**
   - 모든 인증 요청 200/401 응답 정상
   - 에러율 대시보드 5분 관찰

### 사용자 영향

| 시점 | 영향 | 완화 |
|------|------|------|
| 앱 배포 직후 | 기존 PostgreSQL 저장 토큰 **미이관** → 기존 사용자 전원 재로그인 필요 | 배포 공지. 저트래픽 시간대 배포 권장 |
| V3 실행 이후 | refresh_token 테이블 영구 삭제. 마이그레이션 되돌리기 불가 | V1 script 의 refresh_token 블록을 V4 로 재생성 가능 (스키마만, 데이터 복구 X) |

### 롤백 전략

- **코드 레벨**: 이전 앱 이미지로 rollout 되돌리기 (Redis 코드 제거된 버전). 단 V3 가 이미 실행되었다면 `refresh_token` 테이블이 없어 이전 버전이 기동 실패. 이 경우 V4 migration 으로 테이블 재생성 필요.
- **데이터 레벨**: refresh token 데이터 자체는 복구 불가. 모든 사용자 재로그인.
- **권장**: 배포 실패 감지 시 "V3 전 단계(앱 배포 직후)" 로 롤백. V3 실행 후 롤백은 사용자 경험 손실이 추가됨.

### 로그/관찰 포인트

- `AuthService` 의 `replay detection` 로그 평소 빈도 대비 급증 여부
- Redis `used_memory` / `keyspace.db0.keys` 추이 — 본래 대비 +N% 수준에서 안정화되는지
- `POST /api/auth/refresh` 의 p95 응답시간 (기존 JPA 대비 감소 예상)
