# Externalize All Texts to Resource Files

**Issue:** #11 | **Priority:** Low | **Status:** Blocked

**Description:** Make sure all user-facing texts are in resource files for i18n support.

**Status:** Requires significant architectural changes for a KMP project.

**Challenges:**
- Compose Multiplatform doesn't have a built-in cross-platform string resource system
- Options:
  1. Use `expect/actual` pattern to provide platform-specific strings
  2. Use a third-party library like `compose-resources`
  3. Android-only resources (not ideal for iOS/desktop)

**PR:** #29
