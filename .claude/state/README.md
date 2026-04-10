# State 디렉토리

이 디렉토리는 안전 훅 및 세션 제어에서 사용하는 런타임 상태 파일을 저장합니다.

## 파일
- `careful.enabled`
- `freeze-dir.txt`

## 생명주기
1. `/careful`이 `careful.enabled`를 생성합니다.
2. `/freeze <dir>`이 `freeze-dir.txt`를 생성합니다.
3. `/guard <dir>`이 두 파일을 모두 생성합니다.
4. `/unfreeze`가 `freeze-dir.txt`를 제거합니다.
5. `/unguard`가 두 파일을 모두 제거합니다.

훅은 런타임에 이 파일들을 읽습니다:
- `.claude/hooks/check-careful.sh`
- `.claude/hooks/check-freeze.sh`

파일이 존재하지 않으면 훅은 설치된 상태로 유지되지만 비활성 상태입니다.
