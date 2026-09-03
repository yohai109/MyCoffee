---
name: spec-reading
description: Prescriptive workflow for selecting, implementing, and submitting work from GitHub spec issues.
---

# Spec Reading

You must follow this workflow when you implement a GitHub issue that acts as a project spec.

## Rules You Must Follow

- You must fully read the issue before changing code.
- You must not start a spec that is already completed or actively owned unless the user explicitly directs you to do so.
- You must keep the implementation focused on the issue scope.
- You must add verification before you call the work finished.
- You must link the resulting PR back to the issue.

## 1. Identify the Correct Spec

List candidate specs and choose the right one.

```bash
gh issue list --label "spec,pending"
gh issue list --label "spec,priority:high"
gh issue list --label "spec"
```

If the user already named the issue, you must prioritize that issue over backlog exploration.

### Verification

- Verify the chosen issue is still open.
- Verify the chosen issue matches the user request.

## 2. Mark the Spec In Progress

If your workflow uses labels and assignees, update them before implementation.

```bash
gh issue update <number> --add-assignee @me
gh issue update <number> --remove-label "pending" --add-label "in_progress"
```

### Verification

- Verify the issue now shows the expected assignee or `in_progress` label.
- Verify you did not remove unrelated labels.

## 3. Sync Your Branching Context

You must start from an up-to-date base branch and then create a focused work branch.

```bash
git checkout master
git pull origin master
git checkout -b feature/<issue-number>-<short-description>
```

### Verification

- Verify the branch name includes the issue number when your workflow expects it.
- Verify you are not working on `master` or `main`.

## 4. Implement Only What the Spec Requires

Break the issue into explicit requirements and implement them one by one. You must not expand scope without a strong reason.

### Verification

- Verify each checkbox or requirement in the issue is mapped to a concrete code or documentation change.
- Verify no unrelated refactor was mixed into the branch.

## 5. Add Tests and Run Verification

You must add focused tests when behavior changes, then run verification.

```bash
./gradlew test
```

Use narrower module commands first when appropriate, then finish with `./gradlew test`.

### Verification

- Verify the changed behavior is tested.
- Verify `./gradlew test` passed or is documented as pre-existing failure.

## 6. Move the Spec to Review and Open a PR

After verification, update the issue state and create the PR.

```bash
gh issue update <number> --remove-label "in_progress" --add-label "in_review"
```

Then follow `.agent/skills/creating-pr/SKILL.md`.

### Verification

- Verify the issue label changed to `in_review` if the repository uses that workflow.
- Verify the PR body references the issue so merging will close it.

## Related Skills

- `.agent/skills/creating-pr/SKILL.md` — required when you are ready to submit the implementation.
- `.agent/skills/spec-writing/SKILL.md` — use when the needed spec does not exist yet.
- `.agent/skills/build-and-test/SKILL.md` — use for the verification sequence.


## User Request

$ARGUMENTS
