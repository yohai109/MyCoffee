package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import kotlin.test.Test

class BrewScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewScreenDisplaysEmptyMessage() = runComposeUiTest {
        setContent {
            BrewScreen()
        }
        onNodeWithText("No brews recorded yet").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewScreenRendersSuccessfully() = runComposeUiTest {
        setContent {
            BrewScreen()
        }
        onRoot().assertExists()
    }
}
