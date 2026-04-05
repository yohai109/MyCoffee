package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals

class BrewScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDisplaysCorrectInformation() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = today,
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            brewTime = 30,
            yield = 36.0,
            notes = "Tasty shot"
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Ethiopian Yirgacheffe",
                onEditClick = {},
                onDeleteClick = {}
            )
        }

        onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
        onNodeWithText("Espresso").assertIsDisplayed()
        onNodeWithText("18g").assertIsDisplayed()
        onNodeWithText("30s").assertIsDisplayed()
        onNodeWithText("36g").assertIsDisplayed()
        onNodeWithText("Date: ${today}").assertIsDisplayed()
        onNodeWithText("Notes: Tasty shot").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDisplaysUnknownCoffeeWhenNotFound() = runComposeUiTest {
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 999,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            method = BrewMethod.POUR_OVER,
            dose = 20.0,
            brewTime = 180,
            yield = null,
            notes = null
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Unknown Coffee",
                onEditClick = {},
                onDeleteClick = {}
            )
        }

        onNodeWithText("Unknown Coffee").assertIsDisplayed()
        onNodeWithText("Pour over").assertIsDisplayed()
        onNodeWithText("20g").assertIsDisplayed()
        onNodeWithText("3m 0s").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemShowsEditButton() = runComposeUiTest {
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            brewTime = 30,
            yield = null,
            notes = null
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Test Coffee",
                onEditClick = {},
                onDeleteClick = {}
            )
        }

        onNodeWithContentDescription("Edit").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemShowsDeleteButton() = runComposeUiTest {
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            brewTime = 30,
            yield = null,
            notes = null
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Test Coffee",
                onEditClick = {},
                onDeleteClick = {}
            )
        }

        onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemEditButtonCallsOnEditClick() = runComposeUiTest {
        var editClicked = false
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            brewTime = 30,
            yield = null,
            notes = null
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Test Coffee",
                onEditClick = { editClicked = true },
                onDeleteClick = {}
            )
        }

        onNodeWithContentDescription("Edit").performClick()
        assert(editClicked)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDeleteButtonCallsOnDeleteClick() = runComposeUiTest {
        var deleteClicked = false
        val testBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            brewTime = 30,
            yield = null,
            notes = null
        )

        setContent {
            BrewItem(
                brew = testBrew,
                coffeeName = "Test Coffee",
                onEditClick = {},
                onDeleteClick = { deleteClicked = true }
            )
        }

        onNodeWithContentDescription("Delete").performClick()
        assert(deleteClicked)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addBrewDialogDisplaysCorrectly() = runComposeUiTest {
        val coffeeStock = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                openDate = null,
                finishDate = null
            )
        )

        setContent {
            AddBrewDialog(
                coffeeStock = coffeeStock,
                onDismiss = {},
                onConfirm = { _, _, _, _, _, _, _ -> }
            )
        }

        onNodeWithText("Add Brew").assertIsDisplayed()
        onNodeWithText("Coffee").assertIsDisplayed()
        onNodeWithText("Date").assertIsDisplayed()
        onNodeWithText("Brew Method").assertIsDisplayed()
        onNodeWithText("Dose (g)").assertIsDisplayed()
        onNodeWithText("Yield (g)").assertIsDisplayed()
        onNodeWithText("Minutes").assertIsDisplayed()
        onNodeWithText("Seconds").assertIsDisplayed()
        onNodeWithText("Notes").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addBrewDialogWithInitialBrew_prefillsFields() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val coffeeStock = listOf(
            CoffeeStock(
                id = 1,
                name = "Test Coffee",
                roaster = "Test Roaster",
                state = CoffeeState.NEW,
                size = 250.0,
                roastDate = today,
                openDate = null,
                finishDate = null
            )
        )
        val initialBrew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            date = today,
            method = BrewMethod.FRENCH_PRESS,
            dose = 30.0,
            brewTime = 240,
            yield = 450.0,
            notes = "Test notes"
        )

        setContent {
            AddBrewDialog(
                coffeeStock = coffeeStock,
                onDismiss = {},
                onConfirm = { _, _, _, _, _, _, _ -> },
                initialBrew = initialBrew,
                selectedCoffeeName = "Test Coffee"
            )
        }

        onNodeWithText("Edit Brew").assertIsDisplayed()
        onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun formatBrewMethod_withEspresso_returnsEspresso() {
        assertEquals("Espresso", formatBrewMethod(BrewMethod.ESPRESSO))
    }

    @Test
    fun formatBrewMethod_withPourOver_returnsPourOver() {
        assertEquals("Pour over", formatBrewMethod(BrewMethod.POUR_OVER))
    }

    @Test
    fun formatBrewMethod_withFrenchPress_returnsFrenchPress() {
        assertEquals("French press", formatBrewMethod(BrewMethod.FRENCH_PRESS))
    }

    @Test
    fun formatBrewMethod_withAeropress_returnsAeropress() {
        assertEquals("Aeropress", formatBrewMethod(BrewMethod.AEROPRESS))
    }

    @Test
    fun formatBrewMethod_withMokaPot_returnsMokaPot() {
        assertEquals("Moka pot", formatBrewMethod(BrewMethod.MOKA_POT))
    }

    @Test
    fun formatBrewMethod_withColdBrew_returnsColdBrew() {
        assertEquals("Cold brew", formatBrewMethod(BrewMethod.COLD_BREW))
    }

    @Test
    fun formatBrewTime_withSecondsOnly_returnsSeconds() {
        assertEquals("30s", formatBrewTime(30))
    }

    @Test
    fun formatBrewTime_withMinutesAndSeconds_returnsMinutesAndSeconds() {
        assertEquals("2m 30s", formatBrewTime(150))
    }

    @Test
    fun formatBrewTime_withZeroSeconds_returnsZeroSeconds() {
        assertEquals("0s", formatBrewTime(0))
    }

    @Test
    fun formatBrewTime_withOnlyMinutes_returnsMinutes() {
        assertEquals("3m 0s", formatBrewTime(180))
    }
}
