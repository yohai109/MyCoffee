---
name: creating-pr
description: Prescriptive PR workflow for MyCoffee, including branch safety, verification, and PR preparation.
---

# Creating a PR

You must follow this workflow before you submit changes to MyCoffee.

## Rules You Must Follow

- You must never commit directly to `master` or `main`.
- You must always work on a focused feature or fix branch.
- You must always run verification before you commit.
- You must reference the issue number in the commit body or PR body when the work closes or fixes an issue.
- You must never open a PR with unverified code.

## 1. Confirm You Are on a Safe Branch

Check your current branch before committing.

```bash
git symbolic-ref --short HEAD
```

If the branch is `master` or `main`, you must stop and create a feature branch instead.

```bash
git checkout master
git pull origin master
git checkout -b feature/<short-description>
```

### Verification

- Verify `git symbolic-ref --short HEAD` does not return `master` or `main`.
- Verify the branch name describes the change.

## 2. Run Required Verification

You must complete local verification before you stage or commit.

```bash
./gradlew test
./gradlew :composeApp:assembleDebug
```

If a failure is pre-existing, you must capture that fact clearly and confirm your change did not create a new failure.

### Verification

- Verify `./gradlew test` is pass or documented as a pre-existing failure.
- Verify `./gradlew :composeApp:assembleDebug` succeeds for app-facing changes.

## 3. Stage and Commit Only the Intended Files

Stage only the files that belong to the task.

```bash
git status
git add <paths>
git commit -m "feat: short summary" -m "Closes #123"
```

### Verification

- Verify `git status` does not include unrelated generated files.
- Verify the commit message is imperative and concise.
- Verify the issue reference is present when applicable.

## 4. Push and Open the PR

Push the feature branch and open a PR.

```bash
git push origin feature/<short-description>
```

The PR must include:

- A concise title such as `feat: add brew timer`
- A summary of what changed
- A testing section with the exact commands you ran
- Screenshots when UI changed

### Verification

- Verify the PR title matches the change type.
- Verify the PR description lists the real verification commands you ran.
- Verify the PR is linked to the issue when it should close that issue.

## Review Checklist

You must confirm all of the following before requesting review:

- [ ] Tests were run
- [ ] Build validation was run
- [ ] Naming and import conventions were followed
- [ ] No force unwrap (`!!`) was introduced
- [ ] Documentation was updated when behavior changed
- [ ] The issue reference is included when needed

## Related Skills

- `.agent/skills/build-and-test/SKILL.md` — use for the required verification sequence.
- `.agent/skills/spec-reading/SKILL.md` — use when the PR implements a spec issue from the backlog.


## User Request

$ARGUMENTS
