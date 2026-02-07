package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.initTestDatabase
import kotlin.test.BeforeTest
import kotlin.test.Test

class SettingsScreenTest {

    @BeforeTest
    fun setup() {
        initTestDatabase()
    }

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
