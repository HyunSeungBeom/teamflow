# 07. Workspace Layout
> Sprint 2 | REQ-WS-LAYOUT-001~004

## Route
/workspace/:wsNo/project/:projectNo (PrivateRoute)

## Status
- [x] Skeleton created (design-sprint)
- [ ] UI designed (design-page)
- [ ] Code generated (design-to-code)
- [ ] QC verified (design-review)

## Figma
- Frame node: 94:3404
- Description node: 94:3399

## Description
- Page name: 07. Workspace Layout
- Route: /workspace/:wsNo/project/:projectNo (PrivateRoute)
- Data: project: { no, name, key }, workspace: { no, name }, members: [{ no, name, picture }]
- Components: Sidebar (240px, logo, project name, tabs, member list), Main Content (flex-1)
- User actions: logo/back -> hub, Board tab -> kanban, Docs/Chat -> "Coming Soon", avatar hover -> tooltip
- API: GET /api/projects/:projectNo, GET /api/workspaces/:wsNo/members
- State: react-query (useProject, useMembers), activeTab useState, react-router nested routes

## Components Used
(design-page)

## Design Decisions
(design-page)
