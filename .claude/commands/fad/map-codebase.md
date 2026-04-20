---
name: fad:map-codebase
description: "브라운필드 코드베이스 분석 — 아키텍처, 컨벤션, 테스트 패턴 파악."
argument-hint: "[optional focus area]"
allowed-tools:
  - Read
  - Write
  - Edit
  - Bash
  - Glob
  - Grep
  - Task
---

<objective>
Produce the codebase baseline needed before coding in an existing project.
</objective>

<context>
Focus: $ARGUMENTS

Outputs:
- `.planning/codebase/ARCHITECTURE.md`
- `.planning/codebase/CONVENTIONS.md`
- `.planning/codebase/CONCERNS.md`
- `.planning/codebase/TESTING.md`
</context>

<process>
1. Inspect project structure, entrypoints, modules, and test layout.
2. Summarize:
   - architecture boundaries
   - naming and style conventions actually worth following
   - risky legacy areas and hot spots
   - existing testing strategy and gaps
3. Prefer current good patterns, not the most common bad patterns.
4. Write/update the output files in `.planning/codebase/`.
5. End with:
   - readiness for brownfield coding
   - ambiguous areas requiring human guidance
   - recommended next command (`brownfield-map-style` or `pm-to-build`)
</process>
