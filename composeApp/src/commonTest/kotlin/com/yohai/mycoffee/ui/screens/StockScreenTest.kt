package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemDisplaysCorrectInformation() = runComposeUiTest {
        // Given
        val testStock = CoffeeStock(
            id = 1,
            name = "Ethiopian Yirgacheffe",
            roaster = "Blue Bottle",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            openDate = null,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
        onNodeWithText("Roaster: Blue Bottle").assertIsDisplayed()
        onNodeWithText("State: NEW").assertIsDisplayed()
        onNodeWithText("Size: 250.0g").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemDisplaysOpenStateCorrectly() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 2,
            name = "Colombian Supremo",
            roaster = "Local Roasters",
            state = CoffeeState.OPEN,
            size = 500.0,
            roastDate = today,
            openDate = today,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Colombian Supremo").assertIsDisplayed()
        onNodeWithText("Roaster: Local Roasters").assertIsDisplayed()
        onNodeWithText("State: OPEN").assertIsDisplayed()
        onNodeWithText("Size: 500.0g").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemDisplaysFinishedStateCorrectly() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 3,
            name = "Kenyan AA",
            roaster = "Coffee Masters",
            state = CoffeeState.FINISHED,
            size = 1000.0,
            roastDate = today,
            openDate = today,
            finishDate = today,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Kenyan AA").assertIsDisplayed()
        onNodeWithText("Roaster: Coffee Masters").assertIsDisplayed()
        onNodeWithText("State: FINISHED").assertIsDisplayed()
        onNodeWithText("Size: 1000.0g").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemShowsOpenButtonForNewState() = runComposeUiTest {
        // Given
        val testStock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            openDate = null,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Open").assertIsDisplayed()
        onNodeWithText("Finish").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemShowsFinishButtonForOpenState() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 2,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.OPEN,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Finish").assertIsDisplayed()
        onNodeWithText("Open").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemHidesButtonsForFinishedState() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 3,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.FINISHED,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = today,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithText("Open").assertDoesNotExist()
        onNodeWithText("Finish").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addStockDialogDisplaysCorrectly() = runComposeUiTest {
        // When
        setContent {
            AddStockDialog(
                onDismiss = {},
                onConfirm = { _, _, _, _ -> }
            )
        }

        // Then
        onNodeWithText("Add New Stock").assertIsDisplayed()
        onNodeWithText("Coffee Name").assertIsDisplayed()
        onNodeWithText("Roaster").assertIsDisplayed()
        onNodeWithText("Size (grams)").assertIsDisplayed()
        onNodeWithText("Roast Date").assertIsDisplayed()
        onNodeWithText("Add").assertIsDisplayed()
        onNodeWithText("Cancel").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addStockDialogWithInitialStock_prefillsFields() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val initialStock = CoffeeStock(
            id = 1,
            name = "Ethiopian Yirgacheffe",
            roaster = "Blue Bottle",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = today,
            openDate = null,
            finishDate = null,
        )

        // When
        setContent {
            AddStockDialog(
                onDismiss = {},
                onConfirm = { _, _, _, _ -> },
                initialStock = initialStock
            )
        }

        // Then
        onNodeWithText("Edit Stock").assertIsDisplayed()
        onNodeWithText("Save").assertIsDisplayed()
        onNodeWithText("Add New Stock").assertDoesNotExist()
        onNodeWithText("Add").assertDoesNotExist()
        onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
        onNodeWithText("Blue Bottle").assertIsDisplayed()
        onNodeWithText("250.0").assertIsDisplayed()
        onNodeWithText(today.toString()).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addStockDialogWithInitialStock_callsOnConfirmWithPreFilledValues() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val initialStock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.NEW,
            size = 100.0,
            roastDate = today,
            openDate = null,
            finishDate = null,
        )
        var confirmCalled = false
        var confirmedName = ""
        var confirmedRoaster = ""
        var confirmedSize = 0.0
        var confirmedRoastDate: LocalDate? = null

        // When
        setContent {
            AddStockDialog(
                onDismiss = {},
                onConfirm = { name, roaster, size, roastDate ->
                    confirmCalled = true
                    confirmedName = name
                    confirmedRoaster = roaster
                    confirmedSize = size
                    confirmedRoastDate = roastDate
                },
                initialStock = initialStock
            )
        }

        // Click Save without modifying any fields
        onNodeWithText("Save").performClick()

        // Then
        assert(confirmCalled)
        assertEquals("Test Coffee", confirmedName)
        assertEquals("Test Roaster", confirmedRoaster)
        assertEquals(100.0, confirmedSize)
        assertEquals(today, confirmedRoastDate)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemShowsEditButtonForNewState() = runComposeUiTest {
        // Given
        val testStock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            openDate = null,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithContentDescription("Edit").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemShowsEditButtonForOpenState() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 2,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.OPEN,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = null,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithContentDescription("Edit").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemHidesEditButtonForFinishedState() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 3,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.FINISHED,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = today,
        )

        // When
        setContent {
            StockItem(testStock)
        }

        // Then
        onNodeWithContentDescription("Edit").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItemEditButtonCallsOnEditClick() = runComposeUiTest {
        // Given
        val testStock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            openDate = null,
            finishDate = null,
        )
        var editClicked = false

        // When
        setContent {
            StockItem(
                stock = testStock,
                onEditClick = { editClicked = true }
            )
        }

        // Click the edit button
        onNodeWithContentDescription("Edit").performClick()

        // Then
        assert(editClicked)
    }

    @Test
    fun calculateAverageOpenTime_withNoFinishedBags_returnsNull() {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = today,
                openDate = null,
                finishDate = null,
            ),
            CoffeeStock(
                id = 2,
                name = "Test Coffee 2",
                roaster = "Test Roaster",
                state = CoffeeState.OPEN,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = null,
            )
        )

        // When
        val result = calculateAverageOpenTime(stockList)

        // Then
        assertNull(result)
    }

    @Test
    fun calculateAverageOpenTime_withFinishedBags_returnsAverage() {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val tenDaysAgo = today.minus(10, DateTimeUnit.DAY)
        val twentyDaysAgo = today.minus(20, DateTimeUnit.DAY)
        
        val stockList = listOf(
            // Bag 1: opened 20 days ago, finished 10 days ago = 10 days open
            CoffeeStock(
                id = 1,
                name = "Test Coffee 1",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = twentyDaysAgo,
                openDate = twentyDaysAgo,
                finishDate = tenDaysAgo,
            ),
            // Bag 2: opened 10 days ago, finished today = 10 days open
            CoffeeStock(
                id = 2,
                name = "Test Coffee 2",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = tenDaysAgo,
                openDate = tenDaysAgo,
                finishDate = today,
            )
        )

        // When
        val result = calculateAverageOpenTime(stockList)

        // Then
        assertEquals(10.0, result)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun statisticsBanner_withNoFinishedBags_displaysNoDataMessage() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = today,
                openDate = null,
                finishDate = null,
            )
        )

        // When
        setContent {
            StatisticsBanner(stockList)
        }

        // Then
        onNodeWithText("Statistics").assertIsDisplayed()
        onNodeWithText("Average open time: No finished bags yet").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun statisticsBanner_withFinishedBags_displaysAverageOpenTime() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val tenDaysAgo = today.minus(10, DateTimeUnit.DAY)
        
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = tenDaysAgo,
                openDate = tenDaysAgo,
                finishDate = today,
            )
        )

        // When
        setContent {
            StatisticsBanner(stockList)
        }

        // Then
        onNodeWithText("Statistics").assertIsDisplayed()
        onNodeWithText("Average open time: 10 days").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun statisticsBanner_withOneDayAverage_displaysSingularDay() = runComposeUiTest {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val oneDayAgo = today.minus(1, DateTimeUnit.DAY)
        
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = oneDayAgo,
                openDate = oneDayAgo,
                finishDate = today,
            )
        )

        // When
        setContent {
            StatisticsBanner(stockList)
        }

        // Then
        onNodeWithText("Statistics").assertIsDisplayed()
        onNodeWithText("Average open time: 1 day").assertIsDisplayed()
    }

    @Test
    fun sortStockList_opensFirst_thenNew_thenFinished() {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Finished Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
            ),
            CoffeeStock(
                id = 2,
                name = "Open Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.OPEN,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = null,
            ),
            CoffeeStock(
                id = 3,
                name = "New Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = today,
                openDate = null,
                finishDate = null,
            )
        )

        // When - sort by state with OPEN first, then NEW, then FINISHED
        val sorted = stockList.sortedBy { 
            when (it.state) {
                CoffeeState.OPEN -> 0
                CoffeeState.NEW -> 1
                CoffeeState.FINISHED -> 2
            }
        }

        // Then
        assertEquals(CoffeeState.OPEN, sorted[0].state)
        assertEquals("Open Coffee", sorted[0].name)
        assertEquals(CoffeeState.NEW, sorted[1].state)
        assertEquals("New Coffee", sorted[1].name)
        assertEquals(CoffeeState.FINISHED, sorted[2].state)
        assertEquals("Finished Coffee", sorted[2].name)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishedBagsHeader_displaysCorrectCount() = runComposeUiTest {
        // When
        setContent {
            FinishedBagsHeader(
                count = 5,
                expanded = false,
                onToggle = {}
            )
        }

        // Then
        onNodeWithText("Finished Bags (5)").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishedBagsHeader_expandIconToggles() = runComposeUiTest {
        // When - expanded state
        setContent {
            FinishedBagsHeader(
                count = 3,
                expanded = true,
                onToggle = {}
            )
        }

        // Then - should show expand less icon
        onNodeWithContentDescription("Collapse").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishedBagsHeader_collapseIconToggles() = runComposeUiTest {
        // When - collapsed state
        setContent {
            FinishedBagsHeader(
                count = 3,
                expanded = false,
                onToggle = {}
            )
        }

        // Then - should show expand more icon
        onNodeWithContentDescription("Expand").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishedBagsHeader_callsOnToggle() = runComposeUiTest {
        // Given
        var toggleCalled = false

        // When
        setContent {
            FinishedBagsHeader(
                count = 1,
                expanded = false,
                onToggle = { toggleCalled = true }
            )
        }

        // Click the header
        onNodeWithText("Finished Bags (1)").performClick()

        // Then
        assert(toggleCalled)
    }

    @Test
    fun separateActiveAndFinishedStock_activeBagsOnlyContainsNonFinished() {
        // Given
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Finished Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
            ),
            CoffeeStock(
                id = 2,
                name = "Open Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.OPEN,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = null,
            ),
            CoffeeStock(
                id = 3,
                name = "New Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = today,
                openDate = null,
                finishDate = null,
            )
        )

        // When - separate into active and finished
        val activeStock = stockList.filter { it.state != CoffeeState.FINISHED }
        val finishedStock = stockList.filter { it.state == CoffeeState.FINISHED }

        // Then
        assertEquals(2, activeStock.size)
        assertEquals(1, finishedStock.size)
        assertEquals("Open Coffee", activeStock[0].name)
        assertEquals("New Coffee", activeStock[1].name)
        assertEquals("Finished Coffee", finishedStock[0].name)
    }

    @Test
    fun calculateAverageRating_withNoRatedBags_returnsNull() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = null
            )
        )

        val result = calculateAverageRating(stockList)

        assertNull(result)
    }

    @Test
    fun calculateAverageRating_withRatedBags_returnsAverage() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Coffee 1",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = 4
            ),
            CoffeeStock(
                id = 2,
                name = "Coffee 2",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = 5
            )
        )

        val result = calculateAverageRating(stockList)

        assertEquals(4.5, result)
    }

    @Test
    fun calculateAverageRating_ignoresNonFinishedBags() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Coffee 1",
                roaster = "Test Roaster",
                state = CoffeeState.OPEN,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = null,
                rating = 3
            ),
            CoffeeStock(
                id = 2,
                name = "Coffee 2",
                roaster = "Test Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = 5
            )
        )

        val result = calculateAverageRating(stockList)

        assertEquals(5.0, result)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun statisticsBanner_displaysAverageRating() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Coffee",
                roaster = "Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = 4
            )
        )

        setContent {
            StatisticsBanner(stockList)
        }

        onNodeWithText("Average rating: 4 stars").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun statisticsBanner_displaysNoRatedBagsMessage() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stockList = listOf(
            CoffeeStock(
                id = 1,
                name = "Coffee",
                roaster = "Roaster",
                state = CoffeeState.FINISHED,
                size = 250.0,
                roastDate = today,
                openDate = today,
                finishDate = today,
                rating = null
            )
        )

        setContent {
            StatisticsBanner(stockList)
        }

        onNodeWithText("Average rating: No rated bags yet").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun starRating_displaysFiveStars() = runComposeUiTest {
        setContent {
            StarRating(3)
        }

        onNodeWithContentDescription("Star 1").assertIsDisplayed()
        onNodeWithContentDescription("Star 2").assertIsDisplayed()
        onNodeWithContentDescription("Star 3").assertIsDisplayed()
        onNodeWithContentDescription("Star 4").assertIsDisplayed()
        onNodeWithContentDescription("Star 5").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stockItem_displaysRatingForFinishedState() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testStock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.FINISHED,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = today,
            rating = 4
        )

        setContent {
            StockItem(testStock)
        }

        onNodeWithContentDescription("Star 1").assertIsDisplayed()
        onNodeWithContentDescription("Star 4").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishStockDialog_displaysCorrectly() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.OPEN,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = null
        )

        setContent {
            FinishStockDialog(
                stock = stock,
                onDismiss = {},
                onConfirm = {}
            )
        }

        onNodeWithText("Finish Test Coffee?").assertIsDisplayed()
        onNodeWithText("Rate this coffee (optional)").assertIsDisplayed()
        onNodeWithText("Cancel").assertIsDisplayed()
        onNodeWithText("Finish").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun finishStockDialog_callsOnConfirmWithRating() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val stock = CoffeeStock(
            id = 1,
            name = "Test Coffee",
            roaster = "Test Roaster",
            state = CoffeeState.OPEN,
            size = 250.0,
            roastDate = today,
            openDate = today,
            finishDate = null
        )
        var confirmedRating: Int? = null

        setContent {
            FinishStockDialog(
                stock = stock,
                onDismiss = {},
                onConfirm = { rating -> confirmedRating = rating }
            )
        }

        onNodeWithContentDescription("Star 4").performClick()
        onNodeWithText("Finish").performClick()

        assertEquals(4, confirmedRating)
    }
}
