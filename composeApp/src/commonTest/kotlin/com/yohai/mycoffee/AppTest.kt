package com.yohai.mycoffee

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.initTestDatabase
import kotlin.test.BeforeTest
import kotlin.test.Test

class AppTest {

    @BeforeTest
    fun setup() {
        initTestDatabase()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun appDisplaysNavigationBar() = runComposeUiTest {
        // When
        setContent {
            App()
        }

        // Then - verify all navigation items are present
        onNodeWithText("Stock").assertIsDisplayed()
        onNodeWithText("Brew").assertIsDisplayed()
        onNodeWithText("Settings").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun appDisplaysStockScreenByDefault() = runComposeUiTest {
        // When
        setContent {
            App()
        }

        // Then - verify the top bar shows "Stock" as the current screen
        onAllNodesWithText("Stock").assertCountEquals(2) // One in nav bar, one in top bar
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigationToBrewScreenWorks() = runComposeUiTest {
        // Given
        setContent {
            App()
        }

        // When - click on the Brew navigation item
        onAllNodesWithText("Brew").onFirst().performClick()

        // Then - verify we navigated to Brew screen
        waitForIdle()
        onNodeWithText("Track your daily brews here").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigationToSettingsScreenWorks() = runComposeUiTest {
        // Given
        setContent {
            App()
        }

        // When - click on the Settings navigation item
        onAllNodesWithText("Settings").onFirst().performClick()

        // Then - verify we navigated to Settings screen
        waitForIdle()
        onNodeWithText("App settings and preferences").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun navigationBetweenAllScreensWorks() = runComposeUiTest {
        // Given
        setContent {
            App()
        }

        // Navigate to Brew
        onAllNodesWithText("Brew").onFirst().performClick()
        waitForIdle()
        onNodeWithText("Track your daily brews here").assertIsDisplayed()

        // Navigate to Settings
        onAllNodesWithText("Settings").onFirst().performClick()
        waitForIdle()
        onNodeWithText("App settings and preferences").assertIsDisplayed()

        // Navigate back to Stock
        onAllNodesWithText("Stock").onFirst().performClick()
        waitForIdle()
        // Stock screen should be visible (checking for navigation bar item presence)
        onNodeWithText("Stock").assertIsDisplayed()
    }
}
