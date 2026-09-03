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

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysSystemThemeOptions() = runComposeUiTest {
        setContent {
            SettingsScreen()
        }

        onNodeWithText("Same as system").assertIsDisplayed()
        onNodeWithText("Dark").assertIsDisplayed()
        onNodeWithText("Light").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsScreenDisplaysConvertedOunceDefaults() = runComposeUiTest {
        setContent {
            SettingsScreen(settings = com.yohai.mycoffee.database.Settings(useGrams = false))
        }

        onNodeWithText("Dose (oz)").performScrollTo().assertIsDisplayed()
        onNodeWithText("Yield (oz)").assertIsDisplayed()
        onNodeWithText("0.63").assertIsDisplayed()
        onNodeWithText("1.27").assertIsDisplayed()
    }
}
