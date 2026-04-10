# AI 딜리버리 파이프라인 (PM -> 빌드 -> QC)

이 레포지토리의 Claude 네이티브 계약은 OpenCode, Gemini CLI, Codex, Copilot, Cursor, Windsurf, Antigravity 어댑터가 설치된 경우 해당 런타임으로도 연결됩니다.

이 레포지토리는 멀티 에이전트 워크플로우로 구성되어 있습니다:

1. `brownfield-map-style`은 코드베이스를 매핑하고 승인된 패턴/안티패턴을 큐레이션합니다.
2. `discovery-ui-handoff`는 구조화된 디스커버리 -> UI 컨셉 -> UI 계약 -> 핸드오프를 실행합니다.
3. `pm-intake`는 요구사항 컨텍스트를 수집하고 PM 핸드오프 팩을 생성합니다.
4. `pm-to-build`는 해당 팩을 FAD 계획/실행 레인에 전달합니다.
5. `qc-verify-ui`는 브라우저 기반 기능 및 디자인 시스템 핵심 검증을 실행합니다.

고활용 오케스트레이션 명령어:
- `fad:pipeline` (통합 브레인스토밍 -> 계획 -> 실행 -> 리뷰 -> 최적화 -> 완료)
- `fad:optimize` (리뷰 후 필수 최적화 단계)
- `fad:quality-gate` (완료 또는 배포 전 엄격한 통과/불통과 게이트)
- `feature-swarm` (병렬 기능 실행)
- `fix-issue` (병렬 트리아지 + 타겟 수정)
- `pr-feedback-loop` (GitHub PR 코멘트 -> 수정 -> QC 재검증)
- `review` (심각도 우선 리뷰 워크플로우)
- `qa-only` (코드 변경 없는 QA 리포트)
- `code-quality-gate` (soft-skip 정책이 적용된 lint/타입체크/테스트 게이트)
- `setup-doctor` (CLI/MCP/자격증명 원샷 설정 검사기)
- `install-browser-skills` (agent-browser + playwright 스킬 설치)
- `security-scan` (로컬 우선 보안 게이트: 의존성 + 시크릿 + 선택적 SAST)
- `dependency-audit` (의존성 전용 취약점 게이트)
- `secrets-scan` (시크릿 유출 탐지 게이트)
- `health-check` (설정 가능한 검사 항목 기반 심층 진단)
- `setup-monitoring` (프로바이더 비종속 알림/대시보드 기본 구성)
- `incident-response` (인시던트 트리아지/격리/복구 워크플로우)
- `rollback` (롤백 준비 상태 점검 + 보호된 롤백 실행)
- `deploy` (게이트 기반 릴리스 실행)
- `autopilot-loop` (범위 제한 자율 딜리버리 사이클)
- `autoplan` (통합 의사결정이 포함된 자동 계획-리뷰 파이프라인)
- `pm-delivery-loop` (`fad:pipeline`의 레거시 래퍼)
- `discovery-ui-handoff` (그린필드 및 Figma 없는 브라운필드 인테이크 레인)
- `gen-doc-sheet` (선택적 스프레드시트 내보내기, EN/JA)
- `careful` / `freeze` / `guard` / `unfreeze` / `unguard` (안전 제어)

## 산출물 계약

PM 핸드오프 팩은 항상 `.planning/pm/current/`에 저장됩니다:

- `PRD.md` - 명시적 요구사항 ID가 포함된 제품 요구사항 문서.
- `SPRINT.md` - 현재 스프린트 범위 (하나의 스프린트는 하나의 페이즈에 매핑).
- `STORIES.md` - 구현 준비가 완료된 사용자 스토리 및 인수 기준.
- `HANDOFF.md` - 엔지니어링 제약사항, 디자인 입력, 리스크, 테스트 의도.
- `RISK-IMPACT.md` - 리스크 레지스터, 브라운필드 영향 맵, 완화 결정사항.
- `QC-REPORT.md` - 브라우저 검증의 QC 게이트 출력.

요구사항 ID 형식: `REQ-<DOMAIN>-<NNN>` (예: `REQ-AUTH-001`).

구조화된 인테이크를 위한 디스커버리 산출물은 `.planning/discovery/current/`에 저장됩니다:
- `IDEA-BRIEF.md`
- `PREMISE-CHECK.md`
- `ALTERNATIVES.md`
- `UI-CONCEPT.md`
- `UI-CONTRACT.md`

## 정합성 규칙

- 모든 구현 계획 태스크는 요구사항 ID를 참조해야 합니다.
- `PRD.md`의 모든 요구사항 ID는 최소 하나의 태스크에 의해 커버되어야 합니다.
- 연기됨/범위 외 요구사항은 구현 태스크에 나타나서는 안 됩니다.
- 새 코드는 `.planning/codebase/APPROVED-PATTERNS.md`를 따라야 합니다.
- 코딩 에이전트는 `.planning/codebase/ANTI-PATTERNS.md`를 피해야 합니다.
- 범위 내 미해결 `high`/`critical` 리스크는 사용자 결정이 기록될 때까지 실행을 차단합니다.

## TDD 규칙

- 도메인 및 API 로직에는 TDD가 필수입니다.
- 플래너는 관련 태스크에 `tdd="true"`를 표시해야 합니다.
- UI 중심 태스크는 엄격한 유닛 퍼스트 대신 인터랙션/E2E 검증을 사용할 수 있습니다.

## 디자인 + QC MCP 규칙

- 요구사항/핸드오프에 Figma 링크가 포함된 경우, 에이전트는 UI 구조 구현/검증 전에 반드시 Figma MCP를 호출해야 합니다.
- 브라운필드 요구사항에 Figma 입력이 없는 경우, 빌드 전에 구조화된 디스커버리/UI 계약 레인을 실행합니다.
- Figma MCP 증거는 핸드오프/감사 산출물에 기록되어야 하며, 증거 누락은 게이트 실패입니다.
- QC는 인터랙션 및 DS 핵심 검증에 Browser MCP를 사용해야 합니다.
- 배포 게이트: 기능 통과 + DS 핵심 이슈 없음.

## 외부 링크 수집 규칙

- 입력에 Jira 또는 Confluence 링크가 포함된 경우, `.claude/scripts/atlassian_cli.py`를 사용하여 컨텍스트를 수집합니다.
- 입력에 GitHub PR URL/번호가 참조된 경우, `.claude/scripts/github_pr_feedback.py`를 통해 코멘트를 수집합니다.
- 링크 수집 증거는 감사 로그에 기록되어야 합니다.
- Jira 상태 전환은 선택 사항이며 명시적인 사용자 확인이 필요합니다.

## 문서 내보내기 규칙

- 스프레드시트 내보내기는 선택 사항이며 사용자가 직접 트리거해야 합니다.
- `.xlsx` 출력에는 `.claude/commands/gen-doc-sheet.md`를 사용합니다.
- 기본 출력 모드는 `DELIVERY_EN` + `DELIVERY_JA` 시트가 포함된 단일 워크북입니다.

## 감사 로깅 규칙

- 모든 주요 단계(`pm-intake`, `pm-to-build`, `autoplan`, `qc-verify-ui`, `review`, `fad:optimize`, `fad:quality-gate`, `deploy`, 딜리버리/오토파일럿 루프)는 하나의 마크다운 감사 로그를 작성해야 합니다.
- 권장 레이아웃: 각 파이프라인 실행마다 `.planning/audit/runs/<run-id>/` (레거시 플랫 로그도 지원됨).
- `.claude/scripts/audit_log.py`를 기본 작성기로 사용하며 `.claude/templates/AUDIT-STEP-TEMPLATE.md` 템플릿을 적용합니다.
- 최소 감사 필드: 메타데이터, 입력, MCP 증거, 리스크 결정, 출력, 다음 액션.

## 컨텍스트 인덱스 규칙

- 설치 메타데이터는 `.planning/setup/superpower-agent-install.json`에 있습니다.
- 로컬 컨텍스트 인벤토리는 `.planning/setup/context-index.json`에 있습니다.
- 전체 워크스페이스 트리를 스캔하기 전에 이 인덱싱된 파일을 우선 참조합니다.

## 코드 품질 게이트 규칙

- 구현(`pm-to-build`, `feature-swarm`, `fix-issue`) 후 `.claude/scripts/code_quality_gate.py`를 실행합니다.
- 게이트 순서: lint -> 타입체크 (TS 감지 시) -> 테스트.
- 누락된 스크립트는 strict 모드가 명시적으로 요청되지 않은 한 감사 증거와 함께 soft-skip됩니다.
- 품질 게이트 실패 시 해결될 때까지 이후 릴리스/QC 진행이 차단됩니다.

## 보안 및 운영 게이트 규칙

- 릴리스 레인 진입 전, `security-scan` (또는 `dependency-audit` + `secrets-scan`)을 실행하고 발견 사항이 있으면 차단합니다.
- 배포에는 롤아웃 전후로 `health-check` 통과가 필요합니다.
- 배포 후 헬스 체크 실패 시 `incident-response` 및 롤백 평가를 트리거해야 합니다.
- 모니터링 기본 구성은 프로바이더 비종속 어댑터를 사용하여 `setup-monitoring`으로 설정해야 합니다.
- 인시던트, 롤백, 보안 결과는 `.planning/pm/current/RISK-IMPACT.md`를 업데이트해야 합니다.

## 필요 외부 에셋

- 제품 스킬 벤더 (로컬 동기화): `.claude/pm/`
- 소스 PM 스킬 레포 (동기화 업데이트용, `full` 번들 전용): `Product-Manager-Skills/`
- 레거시 참조 레포 (`full` 번들 전용): `get-shit-done/`

## 규칙

글로벌 관심사 규칙은 `.claude/rules/`에 있습니다:
- api-conventions
- code-style
- database
- error-handling
- git-workflow
- project-structure
- security
- testing
- agent-docs

## 에이전트 운영 에셋

- `.claude/AGENTS.md` - PM/빌드/QC/운영 에이전트의 운영 계약
- `AGENTS.md` / `GEMINI.md` / `CODEX.md` - 인스톨러가 생성하는 선택적 크로스 런타임 브릿지 문서
- `.claude/instructions/ORCHESTRATION.md` - 조정 로직
- `.claude/instructions/EXPERIMENTS.md` - 인스트럭션 튜닝 로그
- `.claude/memory/` - 연속 루프 메모리 상태
- `.claude/templates/` - 재사용 가능한 리뷰 + QA 체크리스트/리포트 형식
- `.claude/state/` - 훅에서 사용하는 careful/freeze 상태 파일
- `.claude/config/health-check.json.example` - 기본 헬스 진단 스키마
- `.claude/config/monitoring.json.example` - 프로바이더 비종속 모니터링 스키마
