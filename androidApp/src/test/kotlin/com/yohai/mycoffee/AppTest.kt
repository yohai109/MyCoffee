package com.yohai.mycoffee

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.DatabaseFactory
import com.yohai.mycoffee.database.initDatabase
import org.junit.Before
import org.junit.Test

private class TestDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            CoffeeDatabase::class.java
        )
    }
}

class AppTest {

    @Before
    fun setup() {
        initDatabase(TestDatabaseFactory())
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

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun secondaryDestinationsDisplayTheirShellTitlesWithoutStockSubtitle() = runComposeUiTest {
        setContent {
            App()
        }

        onNodeWithContentDescription("More destinations").performClick()
        onNodeWithText("Recipes").performClick()
        waitForIdle()
        onNodeWithText("Recipes").assertIsDisplayed()
        onNodeWithText("Your coffee, at a glance").assertDoesNotExist()

        onNodeWithContentDescription("More destinations").performClick()
        onNodeWithText("Timer").performClick()
        waitForIdle()
        onNodeWithText("Timer").assertIsDisplayed()
        onNodeWithText("Your coffee, at a glance").assertDoesNotExist()
    }
}
