# 에이전트 메모리

이 디렉토리는 자율 루프를 위한 운영 메모리를 저장합니다.

- `LOOP-STATE.md` - 현재 실행 사이클 상태
- `DECISIONS.md` - 확정된 의사결정 및 근거
- `BLOCKERS.md` - 개입이 필요한 미해결 블로커

이 파일들은 기계 판독 가능성을 최우선으로 합니다. 항목을 간결하고 구조화된 상태로 유지하세요.

## 업데이트 담당

- `pm-intake`, `pm-delivery-loop`: 주요 범위 결정사항을 `DECISIONS.md`에 추가.
- `fix-issue`, `incident-response`, `pm-delivery-loop`: 미해결 블로커를 `BLOCKERS.md`에 추가.
- `deploy`: 릴리스 게이트 결과/상태를 `LOOP-STATE.md`에 추가.
