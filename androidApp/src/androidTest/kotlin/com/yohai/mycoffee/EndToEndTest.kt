package com.yohai.mycoffee

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasRole
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.waitForIdle
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest {
    private companion object {
        const val E2E_DATABASE_NAME = "coffee-e2e.db"
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetAppDatabase() {
        // Close the activity before clearing its file, then launch the clean app state.
        composeRule.activityRule.scenario.close()
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase("coffee.db")
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase(E2E_DATABASE_NAME)
        composeRule.activityRule.launchActivity(
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_DATABASE_NAME, E2E_DATABASE_NAME)
        )
        composeRule.waitForIdle()
    }

    @After
    fun removeE2eDatabase() {
        composeRule.activityRule.scenario.close()
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase(E2E_DATABASE_NAME)
    }

    @Test
    fun appStartsOnStockAndPrimaryNavigationWorks() {
        composeRule.onAllNodesWithText("Stock").onFirst().assertIsDisplayed()
        composeRule.onAllNodesWithText("Brew").onFirst().performClick()
        composeRule.waitForText("Your brew journal is quiet")
        composeRule.onAllNodesWithText("Settings").onFirst().performClick()
        composeRule.waitForText("App settings and preferences")
        composeRule.onAllNodesWithText("Stock").onFirst().performClick()
        composeRule.waitForText("Your shelf is waiting")
    }

    @Test
    fun overflowDestinationsReturnToTheirOriginatingPrimaryScreen() {
        composeRule.openOverflow("Recipes")
        composeRule.waitForText("Search recipes")
        composeRule.pressBackFromActivity()
        composeRule.waitForText("Your shelf is waiting")

        composeRule.onAllNodesWithText("Brew").onFirst().performClick()
        composeRule.waitForText("Your brew journal is quiet")
        composeRule.openOverflow("Timer")
        composeRule.waitForText("Brew timer")
        composeRule.pressBackFromActivity()
        composeRule.waitForText("Your brew journal is quiet")
    }

    @Test
    fun stockCanBeAddedEditedOpenedAndFinished() {
        composeRule.onNodeWithText("Add coffee").performClick()
        composeRule.onNodeWithText("Coffee Name").performTextInput("Morning Roast")
        composeRule.onNodeWithText("Roaster").performTextInput("Local Roaster")
        composeRule.onNodeWithText("Roast Date").performClick()
        composeRule.onNodeWithText("OK").performClick()
        composeRule.onNodeWithText("Add").performClick()
        composeRule.waitForText("Morning Roast")

        composeRule.onAllNodesWithContentDescription("Edit").onFirst().performClick()
        composeRule.onNodeWithText("Coffee Name").performTextClearance()
        composeRule.onNodeWithText("Coffee Name").performTextInput("Evening Roast")
        composeRule.onNodeWithText("Save").performClick()
        composeRule.waitForText("Evening Roast")
        composeRule.onNodeWithText("Open").performClick()
        composeRule.waitForText("State: OPEN")
        composeRule.onNodeWithText("Finish").performClick()
        composeRule.onNodeWithText("Finish").performClick()
        composeRule.waitForText("Finished Bags (1)")
    }

    @Test
    fun invalidStockInputCannotBeSaved() {
        composeRule.onNodeWithText("Add coffee").performClick()
        composeRule.onNodeWithText("Coffee Name").performTextInput("Invalid Roast")
        composeRule.onNodeWithText("Roaster").performTextInput("Local Roaster")
        composeRule.onNodeWithText("Size (grams)").performTextClearance()
        composeRule.onNodeWithText("Size (grams)").performTextInput("0")
        composeRule.onNodeWithText("Enter 0.1 to 10000.0").assertIsDisplayed()
        composeRule.onNodeWithText("Add").assertIsNotEnabled()
    }

    @Test
    fun settingsPreferencePersistsAfterNavigationAndRelaunch() {
        composeRule.onAllNodesWithText("Settings").onFirst().performClick()
        composeRule.waitForText("App settings and preferences")
        composeRule.onAllNodes(hasRole(androidx.compose.ui.semantics.Role.RadioButton))[1].performClick()
        composeRule.onAllNodesWithText("Stock").onFirst().performClick()
        composeRule.waitForText("Your shelf is waiting")
        composeRule.activityRule.scenario.recreate()
        composeRule.waitForText("Your shelf is waiting")
        composeRule.onAllNodesWithText("Settings").onFirst().performClick()
        composeRule.waitForText("App settings and preferences")
        composeRule.onAllNodes(hasRole(androidx.compose.ui.semantics.Role.RadioButton))[1].assertIsSelected()
        composeRule.onNodeWithText("Size (oz)").assertIsDisplayed()
    }

    @Test
    fun brewRecipesAndTimerShowTheirCurrentStates() {
        composeRule.onAllNodesWithText("Brew").onFirst().performClick()
        composeRule.waitForText("Your brew journal is quiet")
        composeRule.openOverflow("Recipes")
        composeRule.waitForText("Search recipes")
        composeRule.onNodeWithText("New recipe").assertIsDisplayed()
        composeRule.openOverflow("Timer")
        composeRule.waitForText("Brew timer")
        composeRule.onNodeWithText("Seconds").performTextClearance()
        composeRule.onNodeWithText("Seconds").performTextInput("60")
        composeRule.onNodeWithText("Start").performClick()
        composeRule.waitForText("Stop")
        composeRule.onNodeWithText("Stop").performClick()
        composeRule.onNodeWithText("Reset").performClick()
        composeRule.waitForText("Start")
    }

    private fun AndroidComposeTestRule<*, MainActivity>.waitForText(text: String) {
        waitUntil(5_000) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText(text).assertIsDisplayed()
    }

    private fun AndroidComposeTestRule<*, MainActivity>.openOverflow(destination: String) {
        onNodeWithContentDescription("More destinations").performClick()
        onNodeWithText(destination).performClick()
        waitForIdle()
    }

    private fun AndroidComposeTestRule<*, MainActivity>.pressBackFromActivity() {
        activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        waitForIdle()
    }
}
