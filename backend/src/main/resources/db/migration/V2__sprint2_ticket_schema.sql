-- ============================================================================
-- V2: Sprint 2 — 티켓 관리 (칸반 보드)
--   [1] ticket — 프로젝트 내 티켓 레코드 (ERD v0.1 §7 TICKET)
--
-- PK      = no BIGINT AUTO_INCREMENT
-- FK      = {table}_no BIGINT (같은 USER 를 여러 역할로 참조하면 {role}_user_no)
-- 타임스탬프 = create_date / update_date / delete_date (TIMESTAMPTZ)
-- soft delete = delete_date IS NOT NULL 인 레코드는 조회에서 제외 (RISK-IMPACT 2026-04-20 결정)
-- 테이블명은 V1 컨벤션(단수형 user/project/workspace) + ERD §7 TICKET 을 따른다.
-- Sprint 2 MVP 범위 — ERD §7 의 sprint_no/type/reporter_user_no/parent_ticket_no/story_points 등은 후속 스프린트에서 ALTER 로 추가.
-- ============================================================================

-- [1] ticket -----------------------------------------------------------------
-- workspace_no 는 project.workspace_no 역정규화. 티켓 권한 검증 시 project join 을 생략하기 위함.
--   (티켓은 프로젝트 간 이동이 없으므로 불변 역정규화 키.)
CREATE TABLE ticket (
    no                 BIGSERIAL    PRIMARY KEY,
    workspace_no       BIGINT       NOT NULL REFERENCES workspace(no) ON DELETE CASCADE,
    project_no         BIGINT       NOT NULL REFERENCES project(no)   ON DELETE CASCADE,
    ticket_key         VARCHAR(20)  NOT NULL,                   -- "TF-1", "TF-2" 형태
    title              VARCHAR(200) NOT NULL,
    description        TEXT             NULL,
    status             VARCHAR(15)  NOT NULL DEFAULT 'BACKLOG', -- BACKLOG|TODO|IN_PROGRESS|DONE (Sprint 2 MVP, ERD 원안 5상태는 후속 확장)
    priority           VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM',  -- LOW|MEDIUM|HIGH|CRITICAL
    assignee_user_no   BIGINT           NULL REFERENCES "user"(no) ON DELETE SET NULL,
    position           INT          NOT NULL DEFAULT 0,         -- 같은 status 컬럼 내 오름차순 정렬 키
    due_date           DATE             NULL,
    create_date        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_date        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    delete_date        TIMESTAMPTZ      NULL,                   -- soft delete 마커
    CONSTRAINT uk_ticket_key UNIQUE (project_no, ticket_key)
);

CREATE INDEX idx_ticket_project_status ON ticket(project_no, status);
CREATE INDEX idx_ticket_assignee       ON ticket(assignee_user_no);
CREATE INDEX idx_ticket_project_active ON ticket(project_no) WHERE delete_date IS NULL;
