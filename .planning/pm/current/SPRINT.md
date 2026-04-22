# Sprint 2 — 프로젝트 워크스페이스 + 티켓 관리 (칸반 보드)

> **기간:** 2주 (Week 3~4)
> **목표:** 프로젝트 카드 클릭 → 워크스페이스 진입 → 칸반 보드에서 티켓 관리 전체 플로우 완성

---

## Phase 1: 기반 구축 (Week 3 전반)

| 태스크 | 요구사항 ID | 담당 | TDD |
|--------|------------|------|-----|
| Ticket 엔티티 + Repository (tickets, ticket 관련 enum) | REQ-ISSUE-001 | Backend | true |
| DB 마이그레이션 (tickets 테이블) | REQ-ISSUE-001 | Backend | - |
| 워크스페이스 레이아웃 컴포넌트 (사이드바 + 메인) | REQ-WS-LAYOUT-001~003 | Frontend | - |
| 프로젝트 진입 라우팅 연결 | REQ-CARRY-002 | Frontend | - |
| 프로젝트 생성 모달 완성 | REQ-CARRY-001 | Frontend | - |

## Phase 2: 티켓 CRUD (Week 3 후반)

| 태스크 | 요구사항 ID | 담당 | TDD |
|--------|------------|------|-----|
| TicketService (생성/조회/수정/삭제) | REQ-ISSUE-001, 005, 006 | Backend | true |
| TicketController (REST API 5개) | REQ-ISSUE-001~006 | Backend | true |
| 티켓 생성 모달/폼 UI | REQ-ISSUE-001 | Frontend | - |
| 티켓 상세 사이드 패널 UI | REQ-ISSUE-004, 005 | Frontend | - |
| react-query: useTickets, useCreateTicket, useUpdateTicket 훅 | REQ-ISSUE-001~006 | Frontend | - |

## Phase 3: 칸반 보드 (Week 4 전반)

| 태스크 | 요구사항 ID | 담당 | TDD |
|--------|------------|------|-----|
| 칸반 보드 컴포넌트 (4컬럼 레이아웃) | REQ-ISSUE-002 | Frontend | - |
| 드래그 앤 드롭 (dnd-kit 또는 @hello-pangea/dnd) | REQ-ISSUE-003 | Frontend | - |
| 티켓 상태 변경 API (PATCH /tickets/:no/status) | REQ-ISSUE-003 | Backend | true |
| 티켓 순서 변경 API (PATCH /tickets/:no/position) | REQ-ISSUE-003 | Backend | true |
| 티켓 카드 컴포넌트 (키, 제목, 우선순위, 담당자, 기한) | REQ-ISSUE-002 | Frontend | - |

## Phase 4: 통합 + 검증 (Week 4 후반)

| 태스크 | 요구사항 ID | 담당 | TDD |
|--------|------------|------|-----|
| 프론트-백 통합 테스트 (허브 → 워크스페이스 → 티켓 관리 E2E) | 전체 | 공용 | - |
| 워크스페이스 사이드바 멤버 목록 | REQ-WS-LAYOUT-002 | Frontend | - |
| 티켓 삭제 확인 다이얼로그 | REQ-ISSUE-006 | Frontend | - |
| 빈 상태 UI (티켓 0개일 때) | REQ-ISSUE-002 | Frontend | - |
| 버그 수정 + 코드 리뷰 | 전체 | 공용 | - |

---

## 병렬 작업 가능 영역

```
Backend (친구)              Frontend (나)
─────────────────           ─────────────────
Week 3: DB + Ticket 엔티티    Week 3: 워크스페이스 레이아웃
        Ticket CRUD API               프로젝트 생성 모달
                                     티켓 생성 폼/상세 패널

Week 4: 상태/순서 변경 API    Week 4: 칸반 보드 + DnD
        통합 테스트                    프론트-백 통합
```
