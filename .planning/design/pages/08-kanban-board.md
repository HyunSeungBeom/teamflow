# 08. Kanban Board
> Sprint 2 | REQ-ISSUE-002, REQ-ISSUE-003

## Route
Workspace Main Content (Board tab)

## Status
- [x] Skeleton created (design-sprint)
- [ ] UI designed (design-page)
- [ ] Code generated (design-to-code)
- [ ] QC verified (design-review)

## Figma
- Frame node: 94:3405
- Description node: 94:3400

## Description
- Page name: 08. Kanban Board
- Route: Workspace Main Content (Board tab)
- Data: issues: [{ no, issueKey, title, status, priority, assigneeNo, dueDate, position }], members
- Components: Board Header (title + "+ Create Issue"), Kanban Column x4, Issue Card, Empty State
- User actions: card drag -> status change, same-column drag -> reorder, visual feedback, rollback on error, card click -> detail panel, "+ Create Issue" -> modal
- API: GET /api/projects/{projectNo}/issues, PATCH /api/issues/{issueNo}/status, PATCH /api/issues/{issueNo}/position
- State: react-query (useIssues), optimistic updates, DnD library (dnd-kit or @hello-pangea/dnd)

## Components Used
(design-page)

## Design Decisions
(design-page)
