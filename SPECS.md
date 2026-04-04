# MyCoffee - Feature Specs

Features derived from open GitHub issues and future enhancement ideas.

---

## From Open GitHub Issues

### 1. Edit Button for Coffee Bags
**Issue:** #21 | **Priority:** High

**Description:** Bags should have an edit button that opens the creation dialog with the existing data pre-filled.

**Requirements:**
- Add an edit icon/button to each `StockItem` card
- Opening edit reuses the `AddStockDialog` but populated with the bag's current data
- On save, call `updateStock()` instead of `insertStock()`
- Validation rules remain the same as the add flow

**Files to modify:**
- `composeApp/src/commonMain/.../ui/screens/StockScreen.kt`

---

### 2. Group Finished Bags Under Collapsible Header
**Issue:** #20 | **Priority:** Medium

**Description:** Finished bags should be grouped at the bottom of the list under a collapsible header.

**Requirements:**
- Display OPEN and NEW bags in the main list as today
- Add a collapsible section at the bottom labeled "Finished Bags"
- Collapsed state should be remembered (but does not need to survive process death)
- Show count of finished bags in the header (e.g., "Finished Bags (5)")

**Files to modify:**
- `composeApp/src/commonMain/.../ui/screens/StockScreen.kt`

---

### 3. Calendar Date Picker for Roast Date
**Issue:** #18 | **Priority:** Medium

**Description:** Selecting the roast date should be a calendar dialog instead of free text input.

**Current Status:** Cannot implement with current Compose Multiplatform version (1.7.3). Material3 DatePicker requires version 1.4.0+ which is not yet available in Compose Multiplatform stable releases.

**Potential Solutions:**
- Wait for Compose Multiplatform to add DatePicker support
- Use a third-party library like `compose-multiplatform-material3-datePicker`
- Implement a custom calendar UI
- Update to a newer (possibly unstable) version of compose-multiplatform

**Files to modify:**
- `composeApp/src/commonMain/.../ui/screens/StockScreen.kt`

---

### 4. Externalize All Texts to Resource Files
**Issue:** #11 | **Priority:** Low

**Description:** Make sure all user-facing texts are in resource files for i18n support.

**Requirements:**
- Move all hardcoded strings to `strings.xml` (Android) and equivalent for other platforms
- Use `stringResource()` in Composables
- Cover all screens: Stock, Brew, Settings, dialogs, and error messages

**Files to modify:**
- All screen files under `composeApp/src/commonMain/.../ui/screens/`
- Resource files under `composeApp/src/androidMain/res/values/strings.xml`

---

### 5. Run Tests in CI
**Issue:** #3 | **Priority:** High

**Description:** CI should run for every PR using GitHub Actions.

**Requirements:**
- Create a GitHub Actions workflow (`.github/workflows/ci.yml`)
- Run on every PR and push to main
- Steps:
  - Checkout code
  - Set up JDK
  - Run `./gradlew test` (all unit tests)
  - Optionally run lint/check tasks
- Report pass/fail status on PR

**Files to create:**
- `.github/workflows/ci.yml`

---

## Additional Feature Ideas

### 6. Brew Tracking Screen
**Priority:** Medium

**Description:** Implement the Brew screen (currently a placeholder) to track daily coffee brews.

**Requirements:**
- Database entity: `BrewRecord` (id, coffeeStockId, date, method, dose, yield, brewTime, notes)
- UI: List of brew records with ability to add/edit
- Link brew records to specific coffee bags
- Brewing method selector (Pour Over, Espresso, French Press, AeroPress, etc.)

---

### 7. Settings Screen
**Priority:** Low

**Description:** Implement the Settings screen (currently a placeholder).

**Requirements:**
- Default brew method preference
- Default bag size
- Unit preferences (grams/ounces)
- Dark/light theme toggle
- Data export/import

---

### 8. Coffee Bag Rating System
**Priority:** Low

**Description:** Allow users to rate coffee bags when finishing them.

**Requirements:**
- Add a `rating: Int?` field to `CoffeeStock` (1-5 stars)
- Show rating prompt when transitioning to FINISHED state
- Display average rating in `StatisticsBanner`
- Sort finished bags by rating when expanded

---

### 9. Coffee Origin & Tasting Notes
**Priority:** Low

**Description:** Add more metadata fields to coffee bags.

**Requirements:**
- Add fields: origin country, process (washed/natural/honey), tasting notes (free text)
- Update add/edit dialog to include these fields
- Display additional info on `StockItem` card (expandable or in a detail view)

---

### 10. Low Stock Alerts
**Priority:** Low

**Description:** Notify users when a coffee bag is running low.

**Requirements:**
- Track remaining weight (initial size - estimated daily usage)
- Show visual indicator on bags below a threshold (e.g., < 50g)
- Configurable threshold in Settings

---

### 11. Brew Analytics Dashboard
**Priority:** Low

**Description:** Visual analytics for brewing habits.

**Requirements:**
- Charts showing brew frequency over time
- Most used brew methods
- Coffee consumption rate (bags per week/month)
- Favorite coffees by rating

---

### 12. Data Backup & Sync
**Priority:** Low

**Description:** Allow users to back up and restore their data.

**Requirements:**
- Export data as JSON file
- Import from JSON backup
- (Future) Cloud sync via the Ktor server
