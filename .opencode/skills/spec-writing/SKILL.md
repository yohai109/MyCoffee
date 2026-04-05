---
name: spec-writing
description: skill that explains how to create new specs and manage the SPECS catalog
---

# Overview

This skill explains how to create new feature specifications and properly maintain the SPECS catalog.

# Steps

## Creating a New Specification

1. Create a new file in the `SPECS/` directory
2. The filename should be descriptive and match the feature (e.g., `brew-timer.md`)
3. Include the following sections in your spec:
   - **Title**: Clear feature name
   - **Description**: What the feature does and why
   - **Requirements**: List of specific requirements
   - **Implementation Notes**: Any technical details or considerations
   - **Testing**: How to verify the feature works

## Updating the Catalog

The catalog (`SPECS/catalog.csv`) tracks all specs with these columns:
- `number`: Sequential ID
- `title`: Feature title
- `filename`: Reference to spec file
- `priority`: `high`, `medium`, or `low`
- `status`: `pending`, `in_progress`, `in_review`, `completed`
- `branch_name`: Feature branch name
- `pr_number`: PR number when created

When adding a new spec:
1. Add a new row to the catalog with `pending` status
2. Assign priority based on importance
3. Generate the next sequential number

# Catalog Format

```csv
number,title,filename,priority,status,branch_name,pr_number
1,Example Feature,example-feature.md,high,pending,,
```

# Guidelines

- Keep specs focused and actionable
- Each spec should be implementable in one PR
- Update status at each workflow stage
- Include the PR number when known
