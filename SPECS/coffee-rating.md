# Coffee Bag Rating System

**Priority:** Low | **Status:** Pending

**Description:** Allow users to rate coffee bags when finishing them.

**Requirements:**
- Add a `rating: Int?` field to `CoffeeStock` (1-5 stars)
- Show rating prompt when transitioning to FINISHED state
- Display average rating in `StatisticsBanner`
- Sort finished bags by rating when expanded

**Implementation Notes:**
- Add rating field to CoffeeStock entity
- Update AddStockDialog to include star rating
- Add rating selector in FINISHED transition dialog
- Update StatisticsBanner to show average
