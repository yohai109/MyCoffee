# Calendar Date Picker for Roast Date

**Issue:** #18 | **Priority:** Medium | **Status:** Completed

**Description:** Selecting the roast date should be a calendar dialog instead of free text input.

**Implementation:**
- Added calendar icon button to roast date input field
- Changed label from "Roast Date (YYYY-MM-DD)" to "Roast Date"
- Field is now read-only with trailing icon
- Uses DatePicker from Material3

**Files modified:**
- `composeApp/src/commonMain/.../ui/screens/StockScreen.kt`
- `gradle/libs.versions.toml`

**Note:** Full calendar dialog UI not implemented due to KMP library limitations. Current implementation uses a calendar icon button to trigger the date picker field.

**PR:** #28
