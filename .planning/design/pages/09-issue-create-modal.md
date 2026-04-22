# 09. Issue Create Modal
> Sprint 2 | REQ-ISSUE-001

## Route
Modal overlay on Kanban Board

## Status
- [x] Skeleton created (design-sprint)
- [ ] UI designed (design-page)
- [ ] Code generated (design-to-code)
- [ ] QC verified (design-review)

## Figma
- Frame node: 94:3406
- Description node: 94:3401

## Description
- Page name: 09. Issue Create Modal
- Route: Modal overlay on Kanban Board
- Data: members (for assignee), IssueStatus enum, IssuePriority enum
- Components: Modal Backdrop, Modal Card (title input, description textarea, status/priority/assignee selects, date picker, cancel/create buttons)
- User actions: title input (required 2-200 chars), field selection, "Create" -> POST API -> close + refresh, "Cancel"/ESC/backdrop click -> close, auto issue key
- API: POST /api/projects/{projectNo}/issues
- State: react-query (useCreateIssue), form state (useState or react-hook-form), modal useState

## Components Used
(design-page)

## Design Decisions
(design-page)
