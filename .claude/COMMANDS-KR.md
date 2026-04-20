# 커맨드 한글 가이드

> 이 프로젝트에서 사용 가능한 슬래시 커맨드(/) 목록.
> 사용법: Claude Code에서 `/커맨드명` 입력.

---

## 📋 PM / 기획

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/pm-intake` | 요구사항 → PM 산출물 팩 생성 (PRD, 스프린트, 스토리, 핸드오프) | 새 스프린트 시작할 때 |
| `/pm-write-prd` | PRD(제품 요구사항 문서)만 빠르게 작성 | PRD만 필요할 때 |
| `/pm-story` | 유저 스토리 + 분할 판단 | 스토리 세분화할 때 |
| `/pm-plan-roadmap` | 로드맵 + 스프린트 순서 수립 | 전체 일정 잡을 때 |
| `/pm-prioritize` | 우선순위 판단 (RICE, ICE 등) | 뭘 먼저 할지 결정할 때 |
| `/pm-strategy` | 제품 전략 워크플로우 | 큰 방향 잡을 때 |
| `/pm-discover` | 디스커버리 워크플로우 | 문제 정의/리서치할 때 |
| `/pm-to-build` | PM 핸드오프 → 개발 계획 전환 | PM 산출물 → 개발 넘길 때 |
| `/pm-delivery-loop` | 통합 딜리버리 파이프라인 (레거시 래퍼) | fad:pipeline 대신 사용 |

---

## 🖥️ 프론트엔드

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/front:new-feature` | 프론트엔드 새 기능 구현 (FSD 구조) | 새 페이지/기능 만들 때 |
| `/front:design-system` | 디자인 시스템 초기화 (Figma + 토큰) | 디자인 시스템 처음 만들 때 |
| `/front:design-to-code` | Figma 디자인 → React 코드 변환 | Figma 보고 코드 짤 때 |
| `/front:design-page` | Figma에 페이지 디자인 생성 | 새 페이지 디자인할 때 |
| `/front:design-tokens` | Figma Variables → Tailwind/CSS 동기화 | 토큰 업데이트할 때 |
| `/front:design-review` | Figma vs 구현 비교 → QC 리포트 | 디자인 검수할 때 |
| `/front:discovery-ui-handoff` | 요구사항 → UI 계약 핸드오프 | Figma 없이 UI 잡을 때 |
| `/front:qc-verify-ui` | 브라우저 기반 QC 검증 | 배포 전 UI 체크할 때 |

---

## ⚙️ 백엔드

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/back:init-project` | Spring Boot 프로젝트 초기 셋업 | 프로젝트 처음 만들 때 |
| `/back:new-feature` | 백엔드 새 기능 구현 (TDD 순서 포함) | 새 API/도메인 만들 때 |
| `/back:test` | 테스트 생성 또는 실행 | 테스트 돌릴 때 |

**`/back:test` 세부 사용법:**
```
/back:test run              → 전체 테스트 실행
/back:test run auth         → 인증 도메인 테스트만
/back:test service/auth     → AuthService 테스트 생성
/back:test controller/auth  → AuthController 테스트 생성
/back:test auth repository  → Repository 슬라이스 테스트 생성
```

---

## 🔄 통합 파이프라인 (FAD)

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/fad:pipeline` | **메인 파이프라인**: 요구사항 → 계획 → 구현 → 리뷰 → 최적화 → 완료 | 기능 하나를 처음부터 끝까지 |
| `/fad:help` | FAD 커맨드 목록 보기 | 뭐 쓸지 모를 때 |
| `/fad:map-codebase` | 코드베이스 분석 (패턴/안티패턴 파악) | 기존 코드 이해할 때 |
| `/fad:optimize` | 리뷰 후 최적화 (성능/유지보수성) | 코드 리뷰 받은 후 |
| `/fad:quality-gate` | 엄격한 머지 게이트 (lint+타입+테스트+보안) | PR 전 최종 체크 |
| `/fad:pr-branch` | PR용 브랜치 정리 (불필요 파일 필터링) | PR 올리기 전 |
| `/fad:ship` | 최종 출시 준비 | 배포 직전 |

---

## 🔍 코드 리뷰 / 품질

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/review` | 심각도 우선 코드 리뷰 | 코드 리뷰 받고 싶을 때 |
| `/code-review` | PR 코드 리뷰 | PR 리뷰할 때 |
| `/code-quality-gate` | lint + 타입체크 + 테스트 게이트 | 커밋 전 체크 |
| `/qa-only` | 브라우저 QA 리포트 (코드 변경 없음) | QA만 하고 싶을 때 |
| `/fix-issue` | 이슈 조사 + 수정 + 검증 | 버그 잡을 때 |
| `/pr` | PR 생성 | PR 올릴 때 |
| `/pr-feedback-loop` | PR 코멘트 → 수정 → QC 재검증 | PR 피드백 반영할 때 |

---

## 🔒 보안

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/security-scan` | 의존성 + 시크릿 + SAST 보안 스캔 | 보안 점검할 때 |
| `/dependency-audit` | 의존성 취약점 검사 | 패키지 업데이트 후 |
| `/secrets-scan` | 시크릿(비밀번호/키) 유출 탐지 | 커밋 전 체크 |

---

## 🚀 배포 / 운영

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/deploy` | 프로덕션 배포 (게이트 기반) | 배포할 때 |
| `/health-check` | 서비스 상태 진단 | 서버 상태 확인할 때 |
| `/incident-response` | 인시던트 대응 (트리아지+격리+복구) | 장애 발생 시 |
| `/rollback` | 롤백 준비 + 실행 | 배포 되돌릴 때 |
| `/setup-monitoring` | 모니터링 설정 (알림/대시보드) | 모니터링 구축할 때 |
| `/setup-doctor` | CLI/MCP/설정 원샷 점검 | 환경 문제 확인할 때 |
| `/install-browser-skills` | 브라우저 테스트 도구 설치 (Playwright) | E2E 테스트 환경 세팅 |

---

## 🛡️ 안전 제어

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/careful` | 위험 명령 경고 모드 활성화 | 중요 작업 전 |
| `/guard` | 완전 안전 모드 (careful + freeze) | 프로덕션 작업 시 |
| `/freeze` | 특정 디렉토리만 편집 가능하도록 제한 | 범위 제한할 때 |
| `/unfreeze` | freeze 해제 | |
| `/unguard` | guard 해제 | |

---

## 📊 기타

| 커맨드 | 설명 | 사용 시점 |
|--------|------|----------|
| `/autoplan` | 자동 계획 파이프라인 (PM+아키텍처+디자인+테스트) | 계획 자동화할 때 |
| `/autopilot-loop` | 자율 딜리버리 사이클 (범위 제한) | 반복 작업 자동화 |
| `/feature-swarm` | 병렬 기능 구현 (여러 에이전트) | 큰 기능을 빠르게 |
| `/brownfield-map-style` | 기존 코드 패턴 분석 + 승인/안티패턴 정리 | 레거시 코드 파악 |
| `/gen-doc-sheet` | 스프레드시트 내보내기 (EN/JA) | 문서 엑셀로 뽑을 때 |

---

## 🔰 자주 쓰는 조합

### 새 스프린트 시작
```
/pm-intake → /front:design-page → /fad:pipeline
```

### 기능 하나 구현
```
/fad:pipeline 또는 /front:new-feature 또는 /back:new-feature
```

### 커밋 전 체크
```
/code-quality-gate
```

### PR 올리기
```
/pr
```

### 버그 수정
```
/fix-issue
```
