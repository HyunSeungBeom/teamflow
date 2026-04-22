# QC Report — Sprint 2

> **일시:** 2026-04-22
> **대상:** Sprint 2 프론트-백 통합 테스트
> **환경:** Docker (docker-compose.frontend.yml) + Vite dev server (localhost:5173)

---

## 자동 검증 (CLI)

| # | 항목 | 결과 | 비고 |
|---|------|------|------|
| 1 | 백엔드 Health | PASS | `{"status":"UP"}` |
| 2 | 인증 보호 | PASS | 미인증 시 403 |
| 3 | API 엔드포인트 | PASS | 13개 전체 노출 확인 |
| 4 | CORS Preflight | PASS | localhost:5173 허용, PATCH 포함 |
| 5 | DB 스키마 | PASS | Flyway v1+v2, ticket 테이블 정상 |
| 6 | 프론트-백 API 경로 매칭 | PASS | 7개 ticket API 전부 일치 |
| 7 | TypeScript 타입체크 | PASS | 에러 0 |
| 8 | Vite 프로덕션 빌드 | PASS | 188ms, 번들 508KB |

## 브라우저 검증 (수동)

| # | 시나리오 | 결과 | 비고 |
|---|---------|------|------|
| 1 | Google OAuth 로그인 | - | 로컬 Docker JWT_SECRET 연동 필요 |
| 2 | 워크스페이스 생성 → 프로젝트 페이지 전환 | - | |
| 3 | 프로젝트 생성 → 보드 진입 | - | |
| 4 | 티켓 생성 → 카드 표시 | - | |
| 5 | 티켓 클릭 → 상세 패널 수정 | - | |
| 6 | 드래그 앤 드롭 컬럼 이동 | - | |
| 7 | 토스트 메시지 표시 | - | |

> `-` = 미실행. 브라우저 테스트는 로컬 OAuth 연동 완료 후 수행 예정.

---

## 발견 이슈 및 조치

| # | 이슈 | 심각도 | 상태 | 조치 |
|---|------|--------|------|------|
| 1 | Strict Mode + RTR 충돌 → refresh token 이중 소비 | HIGH | FIXED | useRef 플래그로 단일 호출 보장 (e56a995) |
| 2 | ticketApi 직접 import (FSD 위반) | HIGH | FIXED | useUpdateTicketStatus/Position hook 생성 (d78a3c8) |
| 3 | overId unsafe type assertion | HIGH | FIXED | validStatuses Set 검증 추가 (d78a3c8) |
| 4 | 워크스페이스 생성 후 페이지 전환 안 됨 | MEDIUM | FIXED | onSuccess 콜백 + workspaces 쿼리 invalidation (d11e66a) |
| 5 | useEffect 내 setState (lint 에러) | MEDIUM | FIXED | key prop 리마운트 패턴 전환 (0027a62) |
| 6 | 드래그/클릭 충돌 | MEDIUM | FIXED | isDraggingRef 플래그 (d78a3c8) |
| 7 | 티켓 수정 시 즉시 저장 → UX 개선 필요 | LOW | FIXED | 수정하기/취소 버튼 + react-hook-form (c351d1e) |
| 8 | 인라인 에러 메시지 → 토스트 통일 | LOW | FIXED | 전역 토스트 시스템 구축 (68942f2) |
| 9 | pnpm-lock.yaml 미동기화 → CI 빌드 실패 | LOW | FIXED | lockfile 동기화 (c048ad7) |
| 10 | docker-compose.frontend.yml .env 미연동 → 403 | HIGH | FIXED | env_file 추가 (17cd08b) |

---

## Sprint 2 프론트엔드 커밋 이력

| 커밋 | 내용 |
|------|------|
| bf38d94 | feat: Sprint 2 Phase 1~2 프론트엔드 기반 구축 + TDS 디자인 시스템 적용 |
| fb42c08 | refactor: Issue → Ticket 리네이밍 |
| 0027a62 → 2c5eeab | feat: 칸반 보드 DnD + 티켓 상세/삭제 UI |
| d78a3c8 → 4efd9c4 | refactor: 코드 리뷰 피드백 — FSD 레이어 정리 + 타입 안전성 |
| e56a995 → 499cefc | fix: Strict Mode + RTR 충돌 수정 |
| d11e66a | fix: 워크스페이스 생성 후 프로젝트 페이지 전환 |
| fcd9d36 | feat: 워크스페이스 사이드바 멤버 목록 |
| c351d1e | refactor: TicketDetailPanel react-hook-form + 수정 버튼 |
| 68942f2 | feat: 전역 토스트 시스템 + 버튼 색상 연하게 |
| 6a3e07f | fix: 토스트 위치 상단 중앙 |
| c048ad7 | chore: pnpm-lock.yaml 동기화 |
| 17cd08b | fix: docker-compose.frontend.yml .env 자동 로드 |
