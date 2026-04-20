---
name: dependency-audit
description: "의존성 취약점 검사. 심각도 기준 차단."
argument-hint: "[--fail-on low|moderate|high|critical] [optional repo root]"
allowed-tools:
  - Read
  - Write
  - Bash
  - Glob
  - Grep
---

<objective>
Detect vulnerable dependencies early and produce machine-readable evidence for risk gating.
</objective>

<context>
Input: $ARGUMENTS

References:
- @.claude/scripts/security_scan.py
- @.claude/templates/AUDIT-STEP-TEMPLATE.md
</context>

<process>
1. Resolve repo root and severity threshold (`--fail-on`, default `high`).
2. Run dependency-only scan:
   - `python3 .claude/scripts/security_scan.py --dependency-only --repo-root <root> --fail-on <threshold> --out .planning/pm/current/DEPENDENCY-AUDIT.json --md-out .planning/pm/current/DEPENDENCY-AUDIT.md --pretty`
3. If status is `failed`, block build/deploy progression.
4. If status is `needs_action`, mark `done_with_concerns` and request scanner prerequisites.
5. Write audit log entry with evidence file links and remediation list.
</process>
