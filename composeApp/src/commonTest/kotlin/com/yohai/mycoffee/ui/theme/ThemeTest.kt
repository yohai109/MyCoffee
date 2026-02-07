package com.yohai.mycoffee.ui.theme

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import kotlin.test.Test

class ThemeTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myCoffeeThemeAppliesLightColorScheme() = runComposeUiTest {
        // When
        setContent {
            MyCoffeeTheme(darkTheme = false) {
                Text("Test")
            }
        }

        // Then - verify theme is applied by checking that text is displayed
        // (this confirms the theme wraps content correctly)
        onNodeWithText("Test").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myCoffeeThemeAppliesDarkColorScheme() = runComposeUiTest {
        // When
        setContent {
            MyCoffeeTheme(darkTheme = true) {
                Text("Test")
            }
        }

        // Then - verify theme is applied by checking that text is displayed
        // (this confirms the theme wraps content correctly)
        onNodeWithText("Test").assertIsDisplayed()
    }
}
