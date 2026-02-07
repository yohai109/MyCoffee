package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockScreenTest {

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
}
