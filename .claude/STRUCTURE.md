# Claude 워크스페이스 구조

## 디렉터리 레이아웃

```text
.claude/
├── agents/                 # 고급/레거시 에이전트 자산 (풀 번들, 선택 사항)
├── commands/               # 슬래시 커맨드 (fad 주력 + 소규모 gsd 호환 + pm/qc/ops)
├── get-shit-done/          # 레거시 GSD 런타임 자산 (풀 번들)
├── hooks/                  # 런타임 훅 (GSD + 안전 훅)
├── instructions/           # 코드로서의 지시사항 플레이북 및 실험
├── memory/                 # 루프 상태, 의사결정, 차단 사항
├── pm/                     # PM 저장소 자산의 로컬 벤더링
│   ├── commands/
│   └── skills/
├── rules/                  # 관심사별 가드레일 규칙
├── scripts/                # 로컬 헬퍼 스크립트 (자산 동기화)
├── state/                  # 세션 안전 상태 (careful/freeze)
├── templates/              # 재사용 가능한 리뷰/QA 템플릿
├── AGENTS.md               # 에이전트 운영 계약서
├── settings.json           # 공유 Claude 설정
├── settings.local.json     # 로컬 사용자 설정 오버라이드
└── CLAUDE.local.md         # 개인 오버라이드 컨텍스트
```

## 이 구조를 채택한 이유

- PM 프레임워크를 `.claude/pm`에 로컬로 유지하여 커맨드의 안정성을 확보합니다.
- 재사용 가능한 `rules/`와 역할별 `skills/`를 분리합니다.
- 기본적으로 무거운 GSD 벤더 트리를 제외하여 번들을 가볍게 유지합니다.
- 브라운필드 가드레일과 PM-to-코드 워크플로우를 한 곳에서 지원합니다.
- 장기 실행 자율 루프를 지원하기 위한 지시사항 튜닝과 메모리를 추가합니다.
- 풀 런타임을 가져오지 않고 추출된 gstack 패턴(리뷰/QA/안전)을 추가합니다.
- 지속적 에이전트 운영을 위한 설정 닥터 및 품질/PR 피드백 헬퍼 스크립트를 추가합니다.
- P0 운영 강화 레이어(보안 스캔, 상태 진단, 인시던트/롤백 워크플로우)를 추가합니다.
- 필수 리뷰 → 최적화 → 엄격한 품질 게이트 단계를 포함하는 통합 `/fad:pipeline`을 추가합니다.

## 기본 제안 대비 개선 사항

1. 커맨드/스킬 충돌을 방지하기 위해 `.claude/pm/` 네임스페이스를 추가했습니다.
2. 동기화 스크립트 `.claude/scripts/sync-pm-assets.sh`를 추가했습니다.
3. 브라운필드 가드레일(`APPROVED-PATTERNS`, `ANTI-PATTERNS`)과 규칙을 연결했습니다.
4. 공유 `settings.json`을 유지하면서 `settings.local.json`을 오버라이드로 보존했습니다.
5. 에이전트 운영 계약서와 지시사항 실험 루프를 추가했습니다.
