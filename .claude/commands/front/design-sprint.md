---
name: design-sprint
description: Sprint PRD/STORIES/HANDOFF를 읽어 Figma Sprint 페이지에 모든 페이지 프레임과 Description 블록을 일괄 생성한다.
argument-hint: "[sprint-number] — 생략 시 .planning/pm/current/ 기준 현재 스프린트"
allowed-tools:
  - Read
  - Write
  - Glob
  - Grep
  - Bash
  - AskUserQuestion
  - mcp__figma__*
---

<objective>
Sprint PM 산출물(PRD, STORIES, HANDOFF) 전체를 읽어 해당 스프린트의 모든 UI 페이지를 파악하고,
Figma Sprint 페이지에 Description 블록 + Page 프레임(와이어프레임 스켈레톤 포함)을 수평 일괄 배치한다.
한 번의 커맨드로 Figma Sprint 페이지가 완성된 구조를 갖추도록 한다.
</objective>

<context>
입력: $ARGUMENTS (스프린트 번호, 선택)

참조:
- @.claude/rules/figma-conventions.md (섹션 6: Sprint 페이지 규칙)
- @.planning/pm/current/PRD.md
- @.planning/pm/current/STORIES.md
- @.planning/pm/current/HANDOFF.md

출력:
- Figma "📱 Sprint NN" 페이지: 모든 페이지의 Description + Page 프레임 수평 배치
- `.planning/design/pages/` 디렉토리: 페이지별 디자인 결정 스텁 파일
</context>

<process>

## Phase 0: 스프린트 소스 결정

```
IF $ARGUMENTS 비어있음:
    source_dir = ".planning/pm/current/"
    SPRINT.md 첫 줄에서 "Sprint N" 추출 → sprint_no
ELSE:
    sprint_no = $ARGUMENTS (예: "2")
    source_dir = ".planning/pm/sprint-{NN}/" (zero-padded)
    디렉토리 없으면 → "current/" 폴백
```

## Phase 1: PM 문서 읽기 → 페이지 매니페스트 구성

세 문서를 순서대로 읽어 페이지 목록을 추출한다:

**1-1. PRD.md 파싱:**
- § 2. 요구사항 → REQ-ID 테이블에서 ID, 기능명, 설명 추출
- § 3. 화면 구성 → 각 `### 3.N` 서브섹션이 하나의 페이지
  - 페이지명, 와이어프레임 ASCII 아트, 연관 REQ-ID 추출

**1-2. STORIES.md 파싱:**
- 각 `## US-S{N}-{NNN}` → 연관 REQ-ID 확인
- 인수 기준 `- [ ]` 항목 → 사용자 동작 목록

**1-3. HANDOFF.md 파싱:**
- § 1. 기술 제약사항 → Frontend 상태관리 라이브러리
- § 2. API 계약 → 엔드포인트 (Method, URL, Req/Res 요약)
- § 3. DB 스키마 → 필요 데이터 형태

**1-4. 페이지 매니페스트 조합:**

PRD 화면 구성 + STORIES를 기준으로 고유 페이지 목록을 확정한다.
각 페이지에 7개 Description 섹션 매핑:

| 섹션 | 소스 | 내용 |
|------|------|------|
| ① 페이지명 + REQ-IDs | PRD §2, §3 | `01. Project Hub (REQ-CARRY-001, 002)` |
| ② Route + 접근 권한 | PRD §2, STORIES | `/workspace/:wsNo/project/:projectNo (PrivateRoute)` |
| ③ 필요 데이터 | HANDOFF §2 응답, §3 | `project: { no, name, key }` |
| ④ 컴포넌트 구성 | PRD §3 와이어프레임 | `01-1 Sidebar (Organism)` |
| ⑤ 사용자 동작 | STORIES 인수기준 | `카드 클릭 → 워크스페이스 이동` |
| ⑥ API 호출 | HANDOFF §2 | `POST /api/projects/{no}/issues` |
| ⑦ 상태 관리 | HANDOFF §1 | `react-query: useIssues` |

AskUserQuestion으로 추출된 페이지 목록을 사용자에게 확인받는다:
"다음 {N}개 페이지로 Sprint 페이지를 생성합니다. 맞나요?"

## Phase 2: Figma Sprint 페이지 확인 / 생성

```
1. use_figma로 현재 Figma 파일의 페이지 목록 확인
2. "📱 Sprint {NN}" 페이지 존재 여부 확인
3. 없으면:
   - 현재 페이지 수 확인 (Starter 플랜 = 3페이지 제한)
   - 3개 이미 있으면 AskUserQuestion: "어떤 페이지를 교체/삭제할까요?"
   - 여유 있으면 새 페이지 생성
4. Sprint 페이지로 전환
```

## Phase 3: 좌표 사전 계산

```
상수:
  FRAME_W       = 1440
  FRAME_H       = 900
  FRAME_GAP     = 60
  DESC_GAP      = 40     (Description → Page 프레임 간격)
  DESC_INIT_H   = 400    (초기 Description 높이, Phase 4에서 조정)
  ROW_ORIGIN_X  = 100    (좌측 마진)
  ROW_ORIGIN_Y  = 100    (상단 마진)

각 페이지 i (0-based):
  frame_x   = ROW_ORIGIN_X + i × (FRAME_W + FRAME_GAP)
  desc_y    = ROW_ORIGIN_Y
  page_y    = desc_y + DESC_INIT_H + DESC_GAP  (Phase 4에서 실제 높이로 재조정)
```

## Phase 4: 페이지별 Figma 객체 일괄 생성

각 페이지에 대해 반복:

### Step A: Description 프레임 생성
```
use_figma:
  createFrame:
    name: "{NN} Description"
    x: frame_x, y: desc_y
    w: FRAME_W, h: DESC_INIT_H
    fills: [{ color: "#FFFFFF" }]
    strokes: [{ color: "#E5E7EB", weight: 1 }]
    cornerRadius: 16
```

### Step B: Description 좌측 컬럼 텍스트 (x = frame_x + 32)
```
use_figma: 순서대로 텍스트 생성
  "Page Description"              → 20px Bold, y=desc_y+20
  "① {페이지명} ({REQ-IDs})"      → 14px SemiBold, y+=40
  "② {route} ({auth})"           → 13px Regular, y+=24
  "③ 필요 데이터:\n{data}"        → 13px Regular, y+=24, 여러 줄
  "④ 컴포넌트 구성:\n{list}"      → 13px Regular, y+=auto, 여러 줄
```

### Step C: Description 우측 컬럼 텍스트 (x = frame_x + 740)
```
use_figma: 순서대로 텍스트 생성
  "⑤ 사용자 동작:\n{actions}"     → 13px Regular
  "⑥ API 호출:\n{apis}"          → 13px Regular
  "⑦ 상태 관리:\n{state}"        → 13px Regular
```

### Step D: Description 높이 조정
```
- 좌/우 컬럼 중 더 긴 쪽의 bottom edge 계산
- Description 프레임 높이 = max(좌_bottom, 우_bottom) + 30px (하단 패딩)
- Page 프레임 Y좌표 재계산: desc_y + 실제_DESC_H + DESC_GAP
```

### Step E: Page 프레임 생성
```
use_figma:
  createFrame:
    name: "{NN}. {Page Name}"
    x: frame_x, y: 재계산된 page_y
    w: 1440, h: 900
    fills: [{ color: "#F3F4F6" }]  (스켈레톤 배경)
```

### Step F: 와이어프레임 스켈레톤 배치
```
PRD §3 ASCII 와이어프레임을 파싱하여 주요 섹션을 회색 박스로 배치:
  - 각 박스: bg=#E5E7EB, cornerRadius=8
  - 섹션 레이블 텍스트: 13px Medium, #6B7280, 중앙 정렬
  - PRD에 명시된 크기 우선 사용 (예: Sidebar 240px)
  - 명시되지 않은 크기는 ASCII 비율로 추정

예시 (워크스페이스):
  Sidebar:     x=0, y=0, w=240, h=900
  Main:        x=240, y=0, w=1200, h=900
  칸반 컬럼 4개: x=240+간격, w=~285, h=800 (Main 내부)
```

## Phase 5: 스크린샷 검증

```
1. 각 페이지 프레임에 대해 get_screenshot 실행
2. Description이 위, Page 프레임이 아래에 정상 배치되었는지 확인
3. 텍스트 잘림이나 프레임 겹침 발견 시:
   - Description 프레임 높이 재조정
   - Page 프레임 Y좌표 재배치
4. (선택) 전체 Sprint 페이지 와이드 스크린샷으로 전체 배치 확인
```

## Phase 6: 디자인 결정 스텁 파일 생성

```
.planning/design/pages/ 디렉토리 생성 (없는 경우 mkdir -p)

각 페이지마다 Write:
  .planning/design/pages/{NN}-{kebab-page-name}.md

내용:
  # {NN}. {Page Name}
  > Sprint {N} | {REQ-IDs}

  ## Route
  {route}

  ## Status
  - [x] Skeleton created (design-sprint)
  - [ ] UI designed (design-page)
  - [ ] Code generated (design-to-code)
  - [ ] QC verified (design-review)

  ## Figma
  - File: {fileKey}
  - Frame node: {node_id}
  - Description node: {node_id}

  ## Description 요약
  ① {페이지명 + REQ-IDs}
  ② {Route}
  ③ {필요 데이터}
  ④ {컴포넌트 구성}
  ⑤ {사용자 동작}
  ⑥ {API 호출}
  ⑦ {상태 관리}

  ## Components Used
  (design-page에서 채움)

  ## Design Decisions
  (design-page에서 채움)
```

## Phase 7: 완료 요약 + 다음 단계 안내

```
출력:
  Sprint {N}: {페이지수}개 페이지 스캐폴딩 완료

  | # | 페이지명 | REQ-IDs | 스텁 파일 |
  |---|---------|---------|----------|
  | 01 | ... | ... | .planning/design/pages/01-... |
  | ... |

  다음 단계 — 개별 페이지 UI 디자인:
    /front:design-page 01. Project Hub
    /front:design-page 02. Workspace Layout
    ...
```

</process>
