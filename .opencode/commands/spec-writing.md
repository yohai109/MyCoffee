---
name: spec-writing
description: Prescriptive workflow for creating actionable GitHub spec issues for MyCoffee.
---

# Spec Writing

You must use this workflow when you create a new feature specification in GitHub Issues.

## Rules You Must Follow

- You must keep each spec focused enough to fit in one reasonably sized PR.
- You must describe the user-visible goal, concrete requirements, implementation notes, and verification expectations.
- You must apply the correct labels when you create the issue.
- You must never create a vague spec that cannot be verified.

## 1. Confirm the Spec Does Not Already Exist

Search the issue tracker before creating a new spec.

```bash
gh issue list --label "spec"
```

### Verification

- Verify there is no open spec that already covers the same work.
- Verify the new title will be specific and non-duplicative.

## 2. Prepare the Required Content

Every spec must include all of the following sections:

- **Description** — what the feature does and why it matters
- **Requirements** — a checklist of concrete deliverables
- **Implementation Notes** — technical constraints, affected files, or migration details
- **Testing** — exactly how the implementation must be verified

### Verification

- Verify every requirement is testable.
- Verify the testing section names concrete commands or manual checks.

## 3. Apply the Correct Labels

Use:

- `spec`
- exactly one priority label: `priority:high`, `priority:medium`, or `priority:low`
- `pending` when the work has not started yet

Before creating the issue, you should confirm the labels exist:

```bash
gh label list
```

### Verification

- Verify each intended label exists.
- Verify you selected only one priority label.

## 4. Create the Issue

Use `gh issue create` with a complete body.

```bash
gh issue create \
  --title "Feature: Brew Timer" \
  --body "## Description\n...\n\n## Requirements\n- [ ] ...\n\n## Implementation Notes\n...\n\n## Testing\n- ..." \
  --label "spec,priority:high,pending"
```

### Verification

- Verify the issue title starts with a clear feature or fix label such as `Feature:` when appropriate.
- Verify the created issue body includes all required sections.
- Verify the created issue has the expected labels.

## 5. Verify the Spec Is Discoverable

List specs again and confirm the new issue appears.

```bash
gh issue list --label "spec,pending"
```

### Verification

- Verify the new issue appears in the filtered list.
- Verify the issue content is actionable enough that another engineer could implement it without follow-up questions.

## Related Skills

- `.agent/skills/spec-reading/SKILL.md` — use when it is time to implement the spec.
- `.agent/skills/creating-pr/SKILL.md` — use after the implementation is complete and verified.


## User Request

$ARGUMENTS
