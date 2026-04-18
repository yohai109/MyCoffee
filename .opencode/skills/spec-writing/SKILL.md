---
name: spec-writing
description: skill that explains how to create new specs using GitHub Issues
---

# Overview

This skill explains how to create new feature specifications using GitHub Issues.

# Steps

## Creating a New Specification

1. Create a new GitHub Issue using `gh issue create`
2. The issue title should be the feature name
3. Use labels to categorize: `spec`, `priority:high`, `priority:medium`, `priority:low`
4. In the issue body, include:
   - **Description**: What the feature does and why
   - **Requirements**: List of specific requirements
   - **Implementation Notes**: Any technical details or considerations
   - **Testing**: How to verify the feature works

## Issue Labels

Use these labels to track spec status:
- `spec` - Marks the issue as a feature specification
- `priority:high`, `priority:medium`, `priority:low` - Priority level
- `pending` - Not yet started
- `in_progress` - Currently being implemented
- `in_review` - Ready for review
- `completed` - Merged and done

## Creating an Issue

```bash
gh issue create --title "Feature: Brew Timer" \
  --body "Description: ..." \
  --label "spec,priority:high,pending"
```

## Updating the Catalog

To list all specs, use `gh issue list` with label filtering:

```bash
# List all pending specs
gh issue list --label "spec,pending"

# List specs by priority
gh issue list --label "spec,priority:high"
```

# Guidelines

- Keep specs focused and actionable
- Each spec should be implementable in one PR
- Update labels at each workflow stage
- Reference the issue number in commit messages and PRs