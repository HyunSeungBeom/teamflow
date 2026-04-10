# State 스키마

## `careful.enabled`
- 타입: 텍스트 파일.
- 생성 커맨드: `/careful`, `/guard`.
- 권장 내용: ISO-8601 타임스탬프 및 선택적 actor 라인.
- 소비 훅: `check-careful.sh`.
- 동작: 파일이 존재하면 파괴적인 bash 명령어 실행 시 확인 프롬프트가 표시됩니다.

## `freeze-dir.txt`
- 타입: 절대 디렉토리 경로를 포함하는 텍스트 파일 (후행 슬래시 권장).
- 생성 커맨드: `/freeze`, `/guard`.
- 소비 훅: `check-freeze.sh`.
- 동작: 파일이 존재하면 경계 외부의 edit/write 작업이 거부됩니다.

## 정합성 규칙
- `freeze-dir.txt`는 존재하는 디렉토리를 가리켜야 합니다.
- 두 파일이 모두 존재하면 안전 모드는 `guarded`로 간주됩니다.
- 파일을 제거하면 해당 보호 기능이 즉시 비활성화됩니다.
