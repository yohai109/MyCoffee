package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import kotlin.test.Test

class SettingsScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysUnitsSection() = runComposeUiTest {
        // When
        setContent {
            SettingsScreen()
        }

        // Then
        onNodeWithText("Units").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysSaveButton() = runComposeUiTest {
        // When
        setContent {
            SettingsScreen()
        }

        // Then
        onAllNodesWithText("Save").onFirst().assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysDefaultBrewMethod() = runComposeUiTest {
        // When
        setContent {
            SettingsScreen()
        }

        // Then
        onNodeWithText("Default Brew Method").assertIsDisplayed()
    }
}
