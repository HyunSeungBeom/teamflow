# 테스팅

- 도메인 및 API 로직 변경에는 TDD가 필수다.
- 모든 작업에는 측정 가능한 검증 명령어 또는 체크가 포함되어야 한다.
- 광범위한 테스트 스위트보다 변경된 동작 근처의 타겟 테스트를 선호한다.
- UI 흐름의 경우 상호작용 체크와 QC 브라우저 검증을 결합한다.
- 수정된 결함에 대해서는 회귀 테스트가 필수다.

---

## TDD 엄격 순서 (백엔드 도메인·API 구현 시 **반드시 준수**)

### 철칙 (Iron Law)

> **프로덕션 코드는 실패하는 테스트 없이 한 줄도 작성하지 않는다.**
> 테스트 먼저 작성 → RED 확인 → 최소 구현으로 GREEN → 리팩터.
> 테스트가 없는 상태로 코드부터 쓴 경우, 해당 코드를 삭제하고 테스트부터 다시 시작한다.

### 계층별 작성 순서 (역방향 의존 순)

백엔드 기능은 **바깥쪽(Controller)부터가 아니라 안쪽(Repository)부터** 테스트-우선으로 쌓아 올린다:

```
1. RepositoryTest  (@DataJpaTest)                         ─ 실패 확인
2. Repository + Entity                                    ─ 최소 구현 → GREEN
3. ServiceTest     (@ExtendWith(MockitoExtension.class))  ─ 실패 확인
4. Service                                                ─ 최소 구현 → GREEN
5. ControllerTest  (@WebMvcTest)                          ─ 실패 확인
6. Controller + DTO                                       ─ 최소 구현 → GREEN
7. (선택) IntegrationTest (@SpringBootTest)               ─ 계층 간 통합 검증
```

- 순서를 건너뛰지 않는다. Controller → Service → Repository 흐름으로 "위에서 아래로" 구현하지 않는다.
- 한 사이클에 한 기능만 추가한다. 여러 엔드포인트/메서드를 한 번에 몰아 쓰지 않는다.
- 테스트가 첫 실행에서 통과하면 테스트가 잘못된 것이다. 실패(RED)를 먼저 확인한다.

### 금지 사항

- 테스트 없이 Entity / Repository / Service / Controller / DTO 를 새로 만드는 것
- "일단 다 만들고 테스트는 나중에" 식의 접근
- 프로덕션 코드 작성 후 그 코드의 동작을 그대로 검증하는 "커버리지 맞추기 테스트"

### 위반 시 복구 절차

1. 위반을 인지하는 즉시 작업을 중단한다.
2. 테스트 없이 작성된 프로덕션 코드를 식별한다.
3. (원칙) 해당 코드를 삭제하고 RED 테스트부터 다시 시작한다.
4. (예외 — 삭제가 비현실적인 경우) 누락된 테스트를 즉시 보충하되, 기존 코드가 테스트를 통과하지 못하면 프로덕션 코드를 수정해 GREEN 으로 맞춘다. 이때도 다음 사이클부터는 원칙을 복원한다.

### 테스트 인프라 체크리스트 (Spring Boot + PostgreSQL 프로젝트)

- `backend/build.gradle` 에 `testRuntimeOnly 'com.h2database:h2'` 포함.
- `backend/src/test/resources/application.yml` 에 다음을 포함:
  - H2(`MODE=PostgreSQL`, `NON_KEYWORDS=USER,KEY` 등 예약어 회피)
  - `spring.jpa.hibernate.ddl-auto: create-drop`
  - `spring.flyway.enabled: false`
  - Redis/Cache AutoConfiguration 제외 (`spring.autoconfigure.exclude`)
  - 테스트용 `app.jwt.*` / `app.google-oauth.*` 더미 값
- `@DataJpaTest` 에는 `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)` 적용.
