---
name: creating-pr
description: Workflow for creating pull requests including branch management, commit format, and PR checklist.
---

# Creating a PR

This skill guides you through creating a pull request for the MyCoffee project.

## IMPORTANT: Never Commit Directly to Master

**Do NOT commit to the `master` branch.** Always work on feature branches and submit PRs.

A pre-commit hook prevents direct commits to `master` or `main`. If a commit is attempted on these branches, it will be rejected.

## Prerequisites

1. Fork the repository on GitHub
2. Clone your fork locally
3. Create a feature branch from master

## Workflow

### 1. Create a Feature Branch
```bash
git checkout master
git pull origin master
git checkout -b feature/your-feature-name
```

### 2. Ensure Code Quality
```bash
./gradlew test                    # Run all tests
./gradlew :composeApp:assembleDebug  # Verify build
```

### 3. Commit Your Changes
```bash
git add .
git commit -m "feat: add new feature description"
```

Reference the issue number in the commit message body:

```bash
git commit -m "feat: add brew timer feature" -m "Closes #123"
```

## Commit Message Format

```
<type>: <short description>

<optional body>

<optional footer>
```

### Types
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

### Examples
```
feat: add brew timer feature

- Implement timer functionality in BrewScreen
- Add timer state management
- Add tests for timer logic

Fixes #123
```

```
fix: prevent crash when stock list is empty

Returns early if list is empty before calculating statistics.
```

## 4. Push to Your Fork
```bash
git push origin feature/your-feature-name
```

## 5. Create Pull Request on GitHub

### PR Title Format
- Start with type: `feat:`, `fix:`, etc.
- Be concise (under 72 characters)
- Use imperative mood: "Add feature" not "Added feature"

### PR Description Template
```markdown
## Summary
Brief description of changes.

## Changes
- Change 1
- Change 2

## Testing
- [ ] Tests pass locally
- [ ] Tested on [platforms]

## Screenshots (if UI changes)
[Add screenshots here]
```

## Code Review Checklist

- [ ] Tests pass
- [ ] Code follows naming conventions
- [ ] Imports organized correctly
- [ ] No force unwrapping (`!!`)
- [ ] Error handling in place
- [ ] Documentation updated if needed
- [ ] Issue referenced in commit or PR body (e.g., `Closes #123`)
- [ ] `./gradlew test` passes
- [ ] `./gradlew :composeApp:assembleDebug` builds successfully

## After Approval

Once your PR is approved:
1. Merge via GitHub UI or `git merge --no-ff`
2. Delete the feature branch
3. Pull latest changes to local master:
   ```bash
    git checkout master
    git pull origin master
    ```

## Related Skills

- [Spec Reading](../spec-reading/SKILL.md) — full workflow from issue to PR, including label management
- [Build & Test](../build-and-test/SKILL.md) — build and test commands to verify before pushing
