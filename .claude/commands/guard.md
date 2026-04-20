---
name: guard
description: "완전 안전 모드 활성화 (careful + freeze)."
argument-hint: "<directory-path>"
allowed-tools:
  - Read
  - Write
  - Bash
  - AskUserQuestion
---

<objective>
Enable destructive-command warnings and directory-scoped edit lock together.
</objective>

<context>
Target freeze path: $ARGUMENTS
</context>

<process>
1. Enable careful mode by creating `.claude/state/careful.enabled`.
2. Resolve freeze boundary from argument or ask user.
3. Validate directory exists and write `.claude/state/freeze-dir.txt`.
4. Confirm both protections are active and show current boundary.
</process>

