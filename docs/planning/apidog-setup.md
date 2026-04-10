# Apidog + springdoc-openapi 셋업 가이드

> **목표**: TeamFlow 백엔드(Spring Boot)의 API 스펙을 Apidog에서 보고, Mock Server로 프론트 개발과 병행한다.
> **진실의 원천**: `backend/src/main/java` 의 컨트롤러 어노테이션 + `@Operation` 메타데이터 → `springdoc-openapi` 가 `/v3/api-docs` 로 자동 노출 → Apidog 가 import.

---

## 1. 백엔드 준비 확인

이미 적용된 항목:
- `build.gradle`: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.+`
- `application.yml`: `springdoc.api-docs.path`, `springdoc.swagger-ui.path`, `paths-to-match: /api/**`
- `SecurityConfig`: `/v3/api-docs/**`, `/swagger-ui/**` permit
- `OpenApiConfig`: API 메타(title, version, description) + Bearer JWT 보안 스키마
- `AuthController`: `@Tag`, `@Operation`, `@ApiResponse`, `@SecurityRequirements`

### 로컬 기동
```bash
cd backend
docker compose up -d postgres redis        # DB/Redis 띄우기
./gradlew bootRun                           # 앱 실행
```

### 확인 URL
| 용도 | URL |
|------|-----|
| OpenAPI JSON (Apidog 가 import 하는 소스) | http://localhost:8080/v3/api-docs |
| Swagger UI (브라우저 확인용) | http://localhost:8080/swagger-ui.html |

---

## 2. Apidog 설치 및 프로젝트 생성

### (권장) 데스크톱 앱
- Windows: https://apidog.com/download
- 데스크톱 앱은 **localhost 에 직접 접근 가능** → ngrok 불필요
- 무료 플랜: 팀 4명까지, Mock Server 무제한

### 프로젝트 만들기
1. Apidog 실행 → `New Project`
2. 프로젝트명: `TeamFlow`
3. 팀원 초대 → 프론트 친구 이메일 추가

---

## 3. OpenAPI 스펙 import

### 3-1. 최초 import
1. 프로젝트 좌측 메뉴 → `Settings` → `Import Data`
2. `Data Source` → `OpenAPI / Swagger`
3. 아래 두 가지 중 하나 선택:
   - **URL Import**: `http://localhost:8080/v3/api-docs`  ← 데스크톱 앱이면 이 방식
   - **File Import**: 브라우저에서 `/v3/api-docs` 를 연 후 JSON 저장 → 파일 업로드
4. `Import Mode` → `Overwrite existing data` (엔드포인트가 사라지거나 이름이 바뀌면 동기화)

### 3-2. 주기적 재동기화 (권장)
`Settings` → `Import Data` → `Auto Sync` 를 URL 기반으로 켜두면 주기적으로 재 import 한다.
- 주기: 백엔드가 변경되는 빈도에 맞춰 설정 (예: 1시간)
- 주의: **기존에 Apidog 내부에서 손으로 편집한 내용은 덮어써짐**. 스펙 편집은 백엔드 어노테이션에서만 수행.

---

## 4. 프론트 친구가 원격에서 보도록 공유 — ngrok

**데스크톱 Apidog 로 같은 PC 에서 볼 거면 이 단계는 불필요.** 프론트 친구가 자기 PC 에서 Apidog 로 실시간 import 를 받고 싶다면 ngrok 으로 백엔드 로컬 서버를 외부 공개한다.

### 4-1. ngrok 설치 (Windows)
**Chocolatey**:
```bash
choco install ngrok
```

**수동 설치**:
1. https://ngrok.com/download 에서 Windows 버전 다운로드
2. 압축 해제 → `ngrok.exe` 를 `C:\tools\ngrok\` 등 원하는 경로에 배치
3. 시스템 환경변수 `Path` 에 해당 경로 추가

### 4-2. 계정 및 authtoken
```bash
# 1. https://dashboard.ngrok.com 에서 무료 가입
# 2. 대시보드의 "Your Authtoken" 복사
# 3. 로컬에 등록 (한 번만)
ngrok config add-authtoken YOUR_AUTHTOKEN
```

### 4-3. 백엔드 공개
```bash
# 백엔드가 localhost:8080 에 떠있는 상태에서:
ngrok http 8080
```

출력 예시:
```
Forwarding   https://abcd-1234.ngrok-free.app -> http://localhost:8080
```

### 4-4. Apidog 에서 ngrok URL 로 import
- 프론트 친구에게 공유: `https://abcd-1234.ngrok-free.app/v3/api-docs`
- 친구의 Apidog → Import Data → URL → 위 주소 입력

### 4-5. ngrok 무료 플랜 주의점
| 제약 | 대응 |
|------|------|
| 재실행 시 URL 이 매번 바뀐다 | 공유할 때 URL 다시 알려주기. 또는 유료 플랜의 고정 도메인 사용. |
| 동시 터널 1개 | 백엔드 1개만 노출 — TeamFlow 는 단일 서비스라 문제 없음 |
| 접속 시 경고 페이지 | `ngrok http 8080 --request-header-add "ngrok-skip-browser-warning: true"` 로 API 호출 시 자동 우회 가능. Apidog 가 보는 `/v3/api-docs` 도 같은 경고 페이지가 뜨므로 필요 |
| 대역폭/시간 제한 | 소규모 개발엔 충분. 하루 종일 켜둬도 OK |

---

## 5. 일상 개발 플로우

```
[백엔드]                        [프론트(친구)]
 코드 변경 + @Operation 추가   │
      │                        │
 ./gradlew bootRun              │
      │                        │
 /v3/api-docs 자동 갱신 ───────▶ Apidog Auto Sync 또는 수동 Import
                                │
                                │  (본인 PC 에서 Apidog 열어서 문서 확인)
                                ▼
                         Mock Server 로 호출 테스트
                         또는 실제 ngrok URL 로 실제 호출
```

---

## 6. 트러블슈팅

| 증상 | 원인 | 해결 |
|------|------|------|
| `/v3/api-docs` 가 403 | SecurityConfig 에 permit 누락 | `SecurityConfig` 의 `authorizeHttpRequests` 에 `/v3/api-docs/**`, `/swagger-ui/**` 추가 확인 |
| `/v3/api-docs` 가 404 | springdoc 의존성 미포함 | `build.gradle` 에 `springdoc-openapi-starter-webmvc-ui` 확인 후 `./gradlew --refresh-dependencies bootRun` |
| Apidog 에서 "Import failed" | ngrok 경고 페이지가 JSON 대신 반환됨 | 브라우저로 먼저 한 번 열어서 "Visit Site" 클릭 → 이후 Apidog 에서 재시도. 또는 `--request-header-add "ngrok-skip-browser-warning: true"` 사용 |
| Swagger UI 에서 Authorize 해도 401 | Access Token 이 만료됨 (15분) | `/api/auth/google` 로 재로그인해 새 토큰 발급 |
| OpenAPI 스펙에 워크스페이스/프로젝트 API 안 보임 | 아직 Phase 3 미구현 | Sprint 1 Phase 3 에서 Controller 추가 시 자동 노출됨 |

---

## 7. 참고

- springdoc-openapi: https://springdoc.org/
- Apidog: https://apidog.com/
- ngrok: https://ngrok.com/docs
- TeamFlow auth 설계: `docs/planning/auth-design.md`
