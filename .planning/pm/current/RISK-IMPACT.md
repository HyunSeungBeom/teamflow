# Sprint 2 — 리스크 레지스터

---

## 리스크 목록

| ID | 리스크 | 심각도 | 확률 | 영향 | 완화 전략 | 상태 |
|----|--------|--------|------|------|----------|------|
| RISK-S2-001 | DnD 라이브러리 선택 | MEDIUM | 중 | 칸반 보드 UX 품질 | dnd-kit vs @hello-pangea/dnd 사전 PoC | OPEN |
| RISK-S2-002 | 이슈 순서(position) 동시성 | MEDIUM | 중 | 여러 사용자 동시 드래그 시 순서 꼬임 | 낙관적 업데이트 + 서버 정렬 기준 확보 | OPEN |
| RISK-S2-003 | 백엔드 CORS 미설정 | HIGH | 높 | 프론트-백 통합 불가 | Sprint 1에서 이미 발생. 백엔드에 localhost CORS 추가 요청 | OPEN |
| RISK-S2-004 | ticket_counter 동시성 | LOW | 낮 | 이슈 키 중복 가능성 | DB 레벨 UNIQUE 제약조건으로 방어 | RESOLVED |
| RISK-S2-005 | 워크스페이스 라우팅 복잡도 | LOW | 낮 | 중첩 라우트 관리 | react-router nested routes 패턴 적용 | OPEN |

---

## 브라운필드 영향 맵

### Sprint 1 코드에 영향 주는 변경

| 영역 | 영향 | 변경 사항 |
|------|------|----------|
| App.tsx 라우팅 | 변경 | 워크스페이스 중첩 라우트 추가 |
| ProjectsPage.tsx | 변경 | 프로젝트 카드 클릭 → 워크스페이스 라우팅 연결 |
| MainLayout.tsx | 유지 | 프로젝트 허브에서는 기존 레이아웃, 워크스페이스에서는 별도 레이아웃 |
| Backend: Project 엔티티 | 유지 | ticket_counter 이미 존재 |
| Backend: SecurityConfig | 변경 | 이슈 API 경로 인증 설정 추가 |

---

## 미결정 사항

| 항목 | 결정자 | 기한 |
|------|--------|------|
| DnD 라이브러리 최종 선택 | Frontend | Week 3 초 |
| 이슈 삭제 방식 (soft vs hard delete) | Backend | Week 3 초 |
| 이슈 상세 UI (사이드 패널 vs 모달 vs 전체 페이지) | Frontend + Design | Week 3 초 |
