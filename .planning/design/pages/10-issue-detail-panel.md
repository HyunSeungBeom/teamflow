# 10. Issue Detail Panel
> Sprint 2 | REQ-ISSUE-004, REQ-ISSUE-005, REQ-ISSUE-006

## Route
Right side panel (slide-in) on Kanban Board

## Status
- [x] Skeleton created (design-sprint)
- [ ] UI designed (design-page)
- [ ] Code generated (design-to-code)
- [ ] QC verified (design-review)

## Figma
- Frame node: 94:3407
- Description node: 94:3402

## Description
- Page name: 10. Issue Detail Panel
- Route: Right side panel on Kanban Board
- Data: issue: { no, issueKey, title, description, status, priority, assigneeNo, dueDate, position }, members
- Components: Panel Header (issue key + title + close), Status/Assignee/Priority Selects, Due Date Picker, Description Textarea, Action Bar (delete + save), Delete Confirm Dialog
- User actions: inline edit title/description, dropdown changes, date picker, "Save" -> PATCH API, "Delete" -> confirm -> DELETE API, ESC/backdrop/close -> panel close
- API: GET /api/issues/{issueNo}, PATCH /api/issues/{issueNo}, DELETE /api/issues/{issueNo}
- State: react-query (useIssue, useUpdateIssue, useDeleteIssue), panel open/close + selectedIssueNo (zustand or useState), delete confirm modal

## Components Used
(design-page)

## Design Decisions
(design-page)
