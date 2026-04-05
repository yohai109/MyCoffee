package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import kotlin.test.Test

class SettingsScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysCorrectText() = runComposeUiTest {
        // When
        setContent {
            SettingsScreen()
        }

        // Then
        onNodeWithText("App settings and preferences").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenRendersSuccessfully() = runComposeUiTest {
        // When
        setContent {
            SettingsScreen()
        }

        // Then
        onRoot().assertExists()
    }
}
