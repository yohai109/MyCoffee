# Data Backup & Sync

**Priority:** Low | **Status:** Pending

**Description:** Allow users to back up and restore their data.

**Requirements:**
- Export data as JSON file
- Import from JSON backup
- (Future) Cloud sync via the Ktor server

**Implementation Notes:**
- Create export/import functions in database layer
- Use platform-specific file pickers for native experience
- Consider Ktor client for cloud sync future feature
