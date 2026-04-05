# Brew Tracking Screen

**Priority:** Medium | **Status:** Pending

**Description:** Implement the Brew screen (currently a placeholder) to track daily coffee brews.

**Requirements:**
- Database entity: `BrewRecord` (id, coffeeStockId, date, method, dose, yield, brewTime, notes)
- UI: List of brew records with ability to add/edit
- Link brew records to specific coffee bags
- Brewing method selector (Pour Over, Espresso, French Press, AeroPress, etc.)

**Implementation Notes:**
- Create new entity in Room database
- Create DAO for brew records
- Add navigation entry in Screen sealed class
- Create BrewScreen composable with list and add dialog
- Consider reusable components from StockScreen
