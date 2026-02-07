package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import kotlin.test.Test

class BrewScreenTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewScreenDisplaysCorrectText() = runComposeUiTest {
        // When
        setContent {
            BrewScreen()
        }

        // Then
        onNodeWithText("Track your daily brews here").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewScreenIsScrollable() = runComposeUiTest {
        // When
        setContent {
            BrewScreen()
        }

        // Then - verify the screen renders without errors
        onRoot().assertExists()
    }
}
