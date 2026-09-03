package com.yohai.mycoffee

import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeaturesTest {
    @Test
    fun freshnessUsesSpecifiedBoundaries() {
        val today = LocalDate(2026, 9, 3)
        assertEquals(Freshness.FRESH, freshnessFor(LocalDate(2026, 8, 28), today))
        assertEquals(Freshness.PEAK, freshnessFor(LocalDate(2026, 8, 27), today))
        assertEquals(Freshness.AGING, freshnessFor(LocalDate(2026, 8, 13), today))
        assertEquals(Freshness.PAST_OPTIMAL, freshnessFor(LocalDate(2026, 8, 3), today))
    }

    @Test
    fun brewFiltersAndCalculatesRatio() {
        val brew = BrewRecord(1, 4, LocalDate(2026, 9, 3), BrewMethod.ESPRESSO, 18.0, 30, 36.0, "good")
        assertEquals(2.0, extractionRatio(brew))
        assertEquals(listOf(brew), filterBrews(listOf(brew), method = BrewMethod.ESPRESSO, coffeeId = 4))
    }

    @Test
    fun csvEscapesQuotes() {
        assertTrue(csvEscape("bright \"citrus\"").contains("\"\"citrus\"\""))
    }
}
