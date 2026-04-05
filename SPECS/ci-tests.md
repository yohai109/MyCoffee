# Run Tests in CI

**Issue:** #3 | **Priority:** High | **Status:** Completed

**Description:** CI should run for every PR using GitHub Actions.

**Implementation:**
- Created `.github/workflows/ci.yml`
- Runs `./gradlew test` on every PR and push to master
- Reports test results

**PR:** #26
