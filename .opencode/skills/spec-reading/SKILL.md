---
name: spec-reading
description: skill that explains how to properly go over specs and implement them using correct development workflow
---

# Overview

This skill explains how to properly implement new features by working with GitHub Issues and following correct development workflow.

# Steps

## Understanding the Available Tasks

All features and issues are tracked as GitHub Issues with the `spec` label. Use `gh issue list` to see available specs:

```bash
# List all pending specs
gh issue list --label "spec,pending"

# List by priority
gh issue list --label "spec,priority:high"

# List all specs regardless of status
gh issue list --label "spec"
```

## Deciding on What Task to Work On

After viewing available tasks, you have 3 options:

1. Go over them one by one, by priority, and implement all of them
2. Implement a specific one the user requested
3. Implement something else

Make sure not to work on completed tasks (filter by labels).

## Work Flow of Working on One Task

When working on a task, follow this workflow exactly:

1. Assign the issue to yourself (optional): `gh issue update <number> --add-assignee @me`
2. Change labels to mark as in progress: `gh issue update <number> --remove-label "pending" --add-label "in_progress"`
3. Read the issue to fully understand the requirements
4. Switch to master branch
5. Create a new branch for the task (use issue number in branch name)
6. Implement the requirements
7. Write tests that verify everything working as expected
8. Change labels to mark as in review: `gh issue update <number> --remove-label "in_progress" --add-label "in_review"`
9. Commit the changes, referencing the issue number
10. Create a PR for this branch
11. Update issue labels to mark as merged/completed after PR is merged

# Guidelines

- Always make sure everything works as expected
- Each PR should be small yet complete - for example if adding infrastructure, also add a basic usage to verify it works
- Do not forget tests
- Reference the issue number in commit messages (e.g., "Closes #123")