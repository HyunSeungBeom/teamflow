# TeamFlow API — Sprint 02

> **Base URL**
> - 로컬: `http://localhost:8082`
> - 운영(Render): `https://teamflow-6szr.onrender.com`
>
> **버전**: v1
> **출처**: `/v3/api-docs` (OpenAPI 3.0.1) + 실제 DTO 코드 보정
> **범위**: Sprint 2 (프로젝트 워크스페이스 + 티켓 관리 / 칸반 보드)
> **전제**: 인증·공통 응답 포맷·에러 처리는 [sprint-01.md](./sprint-01.md) §1~§2 그대로 상속한다.

---

## 1. 스프린트 2 요약

### 1.1 추가된 엔드포인트

| # | Method | Path | 용도 |
|---|--------|------|------|
| 1 | `POST`   | `/api/projects/{projectNo}/tickets`          | 티켓 생성 (티켓 키 자동 발급) |
| 2 | `GET`    | `/api/projects/{projectNo}/tickets`          | 프로젝트 내 활성 티켓 목록 (칸반 보드 로드) |
| 3 | `GET`    | `/api/tickets/{ticketNo}`                     | 티켓 상세 |
| 4 | `PATCH`  | `/api/tickets/{ticketNo}`                     | 티켓 부분 수정 |
| 5 | `DELETE` | `/api/tickets/{ticketNo}`                     | 티켓 삭제 (soft) |
| 6 | `PATCH`  | `/api/tickets/{ticketNo}/status`              | 드래그 앤 드롭: 컬럼 간 이동 |
| 7 | `PATCH`  | `/api/tickets/{ticketNo}/position`            | 같은 컬럼 내 순서 변경 |

모두 **Bearer 토큰 필수**. `{userNo}` 는 Access Token의 `sub` 에서 추출된다.

### 1.2 변경된 정책

- **Soft delete 확정** — `DELETE /api/tickets/{ticketNo}` 는 실제 행을 삭제하지 않고 `delete_date` 타임스탬프를 채운다. 이후 `GET`/`LIST` 는 해당 티켓를 제외한다.
- **슬림 응답** — 드래그 앤 드롭 경로(`/status`, `/position`)는 payload 최소화를 위해 `{ no, status }` / `{ no, position }` 형태로만 돌려준다. CRUD 경로는 Response 전체.
- **권한 모델** — 티켓 도메인의 모든 권한 체크는 "워크스페이스 멤버" 단일 기준으로 일원화. `project_member` 는 Sprint 1에서 등록만 해두고, 티켓 단계에서는 `workspace_member` 에 존재하면 통과.

---

## 2. 공통 사항 (Sprint 1 상속)

### 2.1 인증

모든 티켓 엔드포인트: `Authorization: Bearer {accessToken}` 헤더 필수. 인증 실패/토큰 만료 응답은 [sprint-01.md §3](./sprint-01.md) 참조.

### 2.2 성공 응답 — `ApiResponse<T>`

```json
{
  "success": true,
  "data": { /* T */ },
  "message": null,
  "timestamp": "2026-04-20T17:00:00.000"
}
```

### 2.3 HTTP 상태 매핑 (티켓 도메인 관련)

| 예외 | HTTP | 상황 |
|------|------|------|
| Bean Validation 실패 | 400 | `title` 길이, `status`/`priority` enum 값, `position` 음수 등 |
| `IllegalArgumentException` | 400 | enum 역직렬화 실패 |
| `WorkspaceAccessDeniedException` | 403 | 워크스페이스 비멤버 |
| `EntityNotFoundException` | 404 | 프로젝트/티켓 없음 (soft delete 포함) |
| `RuntimeException` | 500 | 그 외 |

---

## 3. 티켓 도메인

### 3.1 `POST /api/projects/{projectNo}/tickets` — 티켓 생성

**Path**

| 필드 | 타입 | 설명 |
|------|------|------|
| `projectNo` | long | 프로젝트 PK |

**요청 Body** — `TicketDto.CreateRequest`

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `title` | string | ✓ | 2~200자 |
| `description` | string |  | 길이 제약 없음 (TEXT) |
| `status` | enum |  | `BACKLOG`\|`TODO`\|`IN_PROGRESS`\|`DONE` (기본 `BACKLOG`) |
| `priority` | enum |  | `LOW`\|`MEDIUM`\|`HIGH`\|`CRITICAL` (기본 `MEDIUM`) |
| `assigneeUserNo` | long |  | 담당자 user.no |
| `dueDate` | date (ISO-8601) |  | 예: `"2026-04-25"` |

```json
{
  "title": "로그인 화면 구현",
  "description": "Google OAuth 로그인 화면을 디자인 시스템에 맞게 구현",
  "status": "BACKLOG",
  "priority": "HIGH",
  "assigneeUserNo": 1,
  "dueDate": "2026-04-25"
}
```

**응답 201 Created** — `ApiResponse<TicketDto.Response>`

```json
{
  "success": true,
  "data": {
    "no": 101,
    "projectNo": 50,
    "ticketKey": "TF-1",
    "title": "로그인 화면 구현",
    "description": "Google OAuth 로그인 화면을 디자인 시스템에 맞게 구현",
    "status": "BACKLOG",
    "priority": "HIGH",
    "assigneeUserNo": 1,
    "position": 0,
    "dueDate": "2026-04-25"
  },
  "timestamp": "..."
}
```

- **티켓 키 자동 발급** — `{project.key}-{ticket_counter}` 형식. `ticket_counter` 는 프로젝트 당 원자 증가 (`Project.nextTicketNumber()`).
- `position` 은 생성 시 `0` 으로 고정. 클라이언트가 필요 시 즉시 `/position` 호출로 위치 확정.

**에러**

| HTTP | 상황 |
|------|------|
| 400 | `title` 누락/길이 위반, enum 값 오류 |
| 403 | 워크스페이스 비멤버 |
| 404 | 프로젝트 없음 |

---

### 3.2 `GET /api/projects/{projectNo}/tickets` — 프로젝트 내 티켓 목록

**Path**: `projectNo: long`

**응답 200** — `ApiResponse<List<TicketDto.Response>>`

```json
{
  "success": true,
  "data": [
    {
      "no": 101, "projectNo": 50, "ticketKey": "TF-1",
      "title": "로그인 화면 구현", "description": "...",
      "status": "BACKLOG", "priority": "HIGH",
      "assigneeUserNo": 1, "position": 0, "dueDate": "2026-04-25"
    },
    {
      "no": 102, "projectNo": 50, "ticketKey": "TF-2",
      "title": "대시보드 레이아웃", "description": null,
      "status": "IN_PROGRESS", "priority": "MEDIUM",
      "assigneeUserNo": null, "position": 1, "dueDate": null
    }
  ]
}
```

- **soft delete 제외** — `delete_date IS NULL` 인 티켓만 반환.
- **정렬** — `position ASC`. 프론트는 `status` 별로 그룹핑하여 4컬럼 칸반을 렌더.

**에러**

| HTTP | 상황 |
|------|------|
| 403 | 워크스페이스 비멤버 |
| 404 | 프로젝트 없음 |

---

### 3.3 `GET /api/tickets/{ticketNo}` — 티켓 상세

**Path**: `ticketNo: long`

**응답 200**: 3.1 응답과 동일 구조 (`TicketDto.Response`).

**에러**

| HTTP | 상황 |
|------|------|
| 403 | 해당 프로젝트의 워크스페이스 비멤버 |
| 404 | 티켓 없음 또는 이미 soft delete 됨 |

---

### 3.4 `PATCH /api/tickets/{ticketNo}` — 티켓 부분 수정

**요청 Body** — `TicketDto.UpdateRequest`
모든 필드 선택. `null` 인 필드는 기존 값 유지. `description`/`dueDate` 는 `null` 입력 시 "값 지우기" 의미로 사용되므로 주의.

| 필드 | 타입 | 동작 |
|------|------|------|
| `title` | string (2~200) | `null` 이면 유지, 값이면 교체 |
| `description` | string | 현재는 "title/description/dueDate 중 하나라도 지정되면 세 필드가 함께 갱신" 되는 구현. 프론트는 세 필드를 세트로 취급할 것. (§5 Note 1 참조) |
| `dueDate` | date | 동일 |
| `status` | enum | 값 있으면 교체 |
| `priority` | enum | 값 있으면 교체 |
| `assigneeUserNo` | long | 값 있으면 교체 |

```json
{
  "status": "IN_PROGRESS",
  "priority": "CRITICAL",
  "assigneeUserNo": 7,
  "dueDate": "2026-04-30"
}
```

**응답 200** — 3.1 응답과 동일 구조

**에러**

| HTTP | 상황 |
|------|------|
| 400 | `title` 길이 위반, enum 값 오류 |
| 403 | 워크스페이스 비멤버 |
| 404 | 티켓 없음 |

---

### 3.5 `DELETE /api/tickets/{ticketNo}` — 티켓 삭제 (soft)

**응답 204 No Content**

- 실제 행은 유지. `delete_date = NOW()` 로 마킹.
- 삭제된 티켓는 이후 `GET`/`LIST`/`PATCH` 에서 404.
- **되돌리기(undelete) API는 Sprint 2 범위 외** — 필요 시 별도 티켓.

**에러**

| HTTP | 상황 |
|------|------|
| 403 | 워크스페이스 비멤버 |
| 404 | 티켓 없음 (이미 삭제 포함) |

---

### 3.6 `PATCH /api/tickets/{ticketNo}/status` — 상태 변경 (DnD 컬럼 간)

> 칸반 컬럼 간 드래그 앤 드롭 전용. 페이로드 최소화.

**요청 Body** — `TicketDto.StatusChangeRequest`

| 필드 | 타입 | 제약 |
|------|------|------|
| `status` | enum | `BACKLOG`\|`TODO`\|`IN_PROGRESS`\|`DONE` (필수) |

```json
{ "status": "IN_PROGRESS" }
```

**응답 200** — `ApiResponse<TicketDto.StatusResponse>`

```json
{
  "success": true,
  "data": { "no": 101, "status": "IN_PROGRESS" },
  "timestamp": "..."
}
```

**에러**

| HTTP | 상황 |
|------|------|
| 400 | `status` 누락/enum 오류 |
| 403 | 워크스페이스 비멤버 |
| 404 | 티켓 없음 |

---

### 3.7 `PATCH /api/tickets/{ticketNo}/position` — 순서 변경 (같은 컬럼 내)

> 같은 status 컬럼 내 이동 전용. 다른 티켓의 position 은 백엔드가 자동 rebalancing 하지 **않는다** — 프론트가 드롭 대상 이전/이후 티켓의 `position` 사이 값을 계산해 보내거나, 컬럼 재로딩으로 정합성 유지.

**요청 Body** — `TicketDto.PositionChangeRequest`

| 필드 | 타입 | 제약 |
|------|------|------|
| `position` | integer | 0 이상 (필수) |

```json
{ "position": 5 }
```

**응답 200** — `ApiResponse<TicketDto.PositionResponse>`

```json
{
  "success": true,
  "data": { "no": 101, "position": 5 },
  "timestamp": "..."
}
```

**에러**

| HTTP | 상황 |
|------|------|
| 400 | `position` 누락/음수 |
| 403 | 워크스페이스 비멤버 |
| 404 | 티켓 없음 |

---

## 4. DB 스키마 (Sprint 2 추가분)

> 상세 및 결정 이력은 [HANDOFF.md §3/§6](../../.planning/pm/current/HANDOFF.md) 참조.

```sql
-- backend/src/main/resources/db/migration/V2__sprint2_ticket_schema.sql
CREATE TABLE ticket (
    no              BIGSERIAL    PRIMARY KEY,
    project_no      BIGINT       NOT NULL REFERENCES project(no) ON DELETE CASCADE,
    ticket_key       VARCHAR(20)  NOT NULL,                    -- "TF-1"
    title           VARCHAR(200) NOT NULL,
    description     TEXT             NULL,
    status          VARCHAR(15)  NOT NULL DEFAULT 'BACKLOG',
    priority        VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM',
    assignee_user_no     BIGINT           NULL REFERENCES "user"(no) ON DELETE SET NULL,
    position        INT          NOT NULL DEFAULT 0,
    due_date        DATE             NULL,
    create_date     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_date     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    delete_date     TIMESTAMPTZ      NULL,                    -- soft delete 마커
    CONSTRAINT uk_ticket_key UNIQUE (project_no, ticket_key)
);

CREATE INDEX idx_ticket_project_status ON ticket(project_no, status);
CREATE INDEX idx_ticket_assignee       ON ticket(assignee_user_no);
CREATE INDEX idx_ticket_project_active ON ticket(project_no) WHERE delete_date IS NULL;
```

- 테이블명은 V1 컨벤션(`user`/`project`/`workspace` 단수형)을 따라 `ticket` 로 통일.
- `"user"` 는 PostgreSQL 예약어이므로 FK 참조 시 따옴표 필수.

---

## 5. Enum 레퍼런스 (Sprint 2 신규)

### 5.1 `TicketStatus`

| 값 | 의미 | 칸반 컬럼 순서 |
|----|------|----------------|
| `BACKLOG` | 백로그 (미착수) | 1 |
| `TODO` | 할 일 (예정) | 2 |
| `IN_PROGRESS` | 진행 중 | 3 |
| `DONE` | 완료 | 4 |

### 5.2 `TicketPriority`

| 값 | 의미 | 제안 아이콘 |
|----|------|-------------|
| `LOW` | 낮음 | 🟢 |
| `MEDIUM` | 보통 (기본) | 🟡 |
| `HIGH` | 높음 | 🟠 |
| `CRITICAL` | 긴급 | 🔴 |

---

## 6. 프론트엔드 연동 체크리스트

- [ ] 칸반 보드 초기 로드는 `GET /api/projects/{projectNo}/tickets` 1회 + 클라이언트에서 `status` 별 그룹핑 + `position ASC` 유지
- [ ] 드래그 앤 드롭 컬럼 간 이동: **낙관적 업데이트** 로 UI 즉시 반영 후 `PATCH /status` 호출. 실패 시 원위치 롤백 + 토스트 (RISK-S2-002)
- [ ] 같은 컬럼 내 순서 변경: 드롭 타겟 전/후 티켓의 `position` 중간값을 계산해 `PATCH /position` 전송. 너무 촘촘해지면 컬럼 재정렬 API 가 필요 → Sprint 2 이후 티켓
- [ ] 티켓 생성 직후 서버가 돌려준 `ticketKey`, `no`, `position` 을 반드시 반영 (클라이언트 임시 키 교체)
- [ ] 티켓 상세 패널은 `GET /api/tickets/{ticketNo}` 로 별도 로드하거나, 목록 응답을 캐시로 사용 (react-query `setQueryData`)
- [ ] 부분 수정은 바뀐 필드만 포함해 전송. `description`/`dueDate` 를 동시에 `null` 로 보내면 모두 지워진다는 점 주의 (§3.4 Note)
- [ ] 삭제 확인 다이얼로그 후 `DELETE /api/tickets/{ticketNo}` → 204. 화면에서 카드 즉시 제거
- [ ] 403/404 응답은 "권한이 없거나 삭제된 티켓" 한 줄 토스트 + 목록 재조회

### Note 1 — `PATCH /api/tickets/{ticketNo}` 의 세 필드 묶음

현재 구현은 `title | description | dueDate` 중 하나라도 전송되면 세 필드를 함께 세팅한다 (지정되지 않은 필드는 `null` 로 교체). 프론트는 이 3개 필드를 항상 세트로 묶어 보내거나, 단건 수정이 필요하면 상세 패널 상태에서 "변경 없는 값" 을 함께 재전송할 것.

→ 다른 단일 필드 수정은 해당 전용 엔드포인트(`/status`, `/position`) 또는 `priority` / `assigneeUserNo` 는 `PATCH /api/tickets/{ticketNo}` 에 단독으로 실어도 안전.

---

## 7. 참고 문서

- 스프린트 범위 / 태스크: [.planning/pm/current/SPRINT.md](../../.planning/pm/current/SPRINT.md)
- PRD: [.planning/pm/current/PRD.md](../../.planning/pm/current/PRD.md)
- 핸드오프 + 구현 정렬: [.planning/pm/current/HANDOFF.md](../../.planning/pm/current/HANDOFF.md)
- 리스크: [.planning/pm/current/RISK-IMPACT.md](../../.planning/pm/current/RISK-IMPACT.md)
- 백엔드 컨벤션: [.claude/rules/backend-conventions.md](../../.claude/rules/backend-conventions.md)
- Sprint 1 API: [sprint-01.md](./sprint-01.md)

Swagger UI
- 로컬: http://localhost:8082/swagger-ui/index.html
- 운영: https://teamflow-6szr.onrender.com/swagger-ui/index.html

OpenAPI JSON
- 로컬: http://localhost:8082/v3/api-docs
- 운영: https://teamflow-6szr.onrender.com/v3/api-docs
