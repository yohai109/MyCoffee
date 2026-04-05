# Low Stock Alerts

**Priority:** Low | **Status:** Pending

**Description:** Notify users when a coffee bag is running low.

**Requirements:**
- Track remaining weight (initial size - estimated daily usage)
- Show visual indicator on bags below a threshold (e.g., < 50g)
- Configurable threshold in Settings

**Implementation Notes:**
- Add remainingWeight field to CoffeeStock or calculate dynamically
- Add visual indicator (icon/color) on StockItem
- Store threshold in settings
