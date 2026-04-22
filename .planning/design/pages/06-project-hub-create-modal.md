# 06. Project Hub (Create Modal)
> Sprint 2 | REQ-CARRY-001, REQ-CARRY-002

## Route
/workspace/:wsNo (PrivateRoute)

## Status
- [x] Skeleton created (design-sprint)
- [ ] UI designed (design-page)
- [ ] Code generated (design-to-code)
- [ ] QC verified (design-review)

## Figma
- Frame node: 94:3403
- Description node: 94:3398

## Description
- Page name: 06. Project Hub - Create Modal
- Route: /workspace/:wsNo (PrivateRoute)
- Data: projects: [{ no, name, key, description }], workspace: { no, name }
- Components: Top Nav, Project Card Grid, "+ New Project" Button, Create Project Modal (Input + Button)
- User actions: "+ New Project" click -> modal, name input -> validation, "Create" -> POST API -> close + refresh, card click -> workspace
- API: GET /api/workspaces/:wsNo/projects, POST /api/workspaces/:wsNo/projects
- State: react-query (useProjects, useCreateProject), modal useState

## Components Used
(design-page)

## Design Decisions
(design-page)
