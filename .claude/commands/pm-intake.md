---
name: pm-intake
description: "요구사항 논의 후 PM 핸드오프 팩 생성 (PRD, 스프린트, 스토리, 핸드오프)."
argument-hint: "<requirement, feature request, or initiative>"
allowed-tools:
  - Read
  - Write
  - Edit
  - Bash
  - Glob
  - Grep
  - Task
  - AskUserQuestion
  - mcp__figma__*
---

<objective>
Run a PM discovery-to-spec flow and output a build-ready sprint pack.
</objective>

<context>
Requirement: $ARGUMENTS

Primary PM references:
- @.claude/pm/commands/write-prd.md
- @.claude/pm/commands/plan-roadmap.md
- @.claude/pm/skills/problem-statement/SKILL.md
- @.claude/pm/skills/prd-development/SKILL.md
- @.claude/pm/skills/user-story/SKILL.md
- @.claude/pm/skills/user-story-splitting/SKILL.md
- @.claude/rules/testing.md
- @.claude/templates/RISK-IMPACT-TEMPLATE.md
- @.claude/templates/AUDIT-STEP-TEMPLATE.md
- @.claude/scripts/atlassian_cli.py
- @.claude/memory/DECISIONS.md

Source-of-truth schema reference (ERD) — **MUST read before drafting PRD/HANDOFF**:
- @.planning/architecture/ERD.md

Output directory:
- @.planning/pm/current/
- @.planning/audit/
</context>

<process>
1. Ask clarifying questions until requirement scope, user segment, KPI, constraints, and timeline are concrete.
2. Parse external links from requirement context:
   - if Jira/Confluence links exist, fetch context via `.claude/scripts/atlassian_cli.py fetch`,
   - include extracted summary/acceptance details in PM discovery context.
3. Build requirement IDs using `REQ-<DOMAIN>-<NNN>`.
4. Extract all external design links from requirement context. If any link is Figma:
   - call Figma MCP for each unique link before finalizing artifacts,
   - capture evidence (`file/key`, page/frame names, key tokens/components used),
   - if Figma MCP fails, mark build readiness as blocked.
5. **ERD alignment gate (required before PRD/HANDOFF drafting).** Load `.planning/architecture/ERD.md` in full and confirm the following for every domain concept in scope:
   - [ ] Table name already exists in ERD — if yes, use that exact name; if no, propose an ADD to ERD in this sprint's RISK-IMPACT as an "ERD extension" decision requiring explicit approval.
   - [ ] Column naming follows ERD conventions — PK `no`, FK `{table}_no`, role-qualified FK (e.g. `assignee_user_no`, `reporter_user_no`) when the same USER is referenced in multiple roles.
   - [ ] Enum values (`status`, `priority`, `type`, etc.) match existing ERD values. Any new enum value must be listed in RISK-IMPACT with rationale.
   - [ ] Cross-cutting policies hold (e.g. `position` columns are **not** exposed as raw values in the API per ERD §2 — clients send `{ beforeNo, afterNo }`).
   - [ ] Soft-delete, timestamps, and audit columns follow ERD (`create_date`, `update_date`, `delete_date`).
   Record the alignment result (pass / extension-needed / conflict) in the audit log. If extension-needed or conflict, block build readiness until the user approves the ERD change.
6. Produce a concise PRD with measurable success criteria and explicit non-goals. Reuse the canonical domain names from the ERD — do **not** invent product-side synonyms that diverge from the schema.
7. Produce one sprint pack mapped to one implementation phase.
8. Generate implementation-ready stories and acceptance criteria with requirement trace.
9. Generate `.planning/pm/current/RISK-IMPACT.md` using `RISK-IMPACT-TEMPLATE`:
   - include risk register + brownfield impact map,
   - classify severity (`low/medium/high/critical`),
   - list required user decisions for unresolved high/critical risks,
   - include an **ERD alignment section** summarizing step 5 results — any "extension-needed" or "conflict" items must appear here with severity ≥ high and a named decision owner.
10. Update `.claude/memory/DECISIONS.md` with:
   - key scope decisions,
   - accepted/rejected options,
   - unresolved decision owners,
   - ERD alignment outcomes (referenced tables, proposed extensions, rejected divergences).
11. Write these files (use canonical ERD names only — e.g. `TICKET` not `ISSUE` if ERD defines `TICKET`):
   - `.planning/pm/current/PRD.md`
   - `.planning/pm/current/SPRINT.md`
   - `.planning/pm/current/STORIES.md`
   - `.planning/pm/current/HANDOFF.md`
   - `.planning/pm/current/RISK-IMPACT.md`
12. Write one step audit log in `.planning/audit/` using `AUDIT-STEP-TEMPLATE`:
   - include questions asked, requirement assumptions, link-ingest evidence, Figma MCP evidence, ERD alignment result, and risk gate state.
13. End with a short readiness report:
   - ready for build: yes/no
   - risk gate: pass/blocked
   - ERD alignment: pass / extension-needed / conflict
   - blockers
   - open questions
</process>
