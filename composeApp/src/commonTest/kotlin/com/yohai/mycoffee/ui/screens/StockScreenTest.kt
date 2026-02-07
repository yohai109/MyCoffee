package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test

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
}
