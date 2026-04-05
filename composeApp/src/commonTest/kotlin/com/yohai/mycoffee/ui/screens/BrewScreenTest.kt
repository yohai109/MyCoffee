package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals

class BrewScreenTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDisplaysCorrectInformation() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val brew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            coffeeName = "Ethiopian Yirgacheffe",
            date = today,
            method = BrewMethod.POUR_OVER,
            dose = 20.0,
            yield = 300.0,
            brewTime = 180,
            notes = "Delicious coffee"
        )

        setContent {
            BrewItem(brew = brew)
        }

        onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
        onNodeWithText("Method: Pour Over").assertIsDisplayed()
        onNodeWithText("Dose: 20.0g").assertIsDisplayed()
        onNodeWithText("Yield: 300.0g").assertIsDisplayed()
        onNodeWithText("Time: 180s").assertIsDisplayed()
        onNodeWithText("Notes: Delicious coffee").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDisplaysEspressoMethod() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val brew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            coffeeName = "Test Coffee",
            date = today,
            method = BrewMethod.ESPRESSO,
            dose = 18.0,
            yield = 36.0,
            brewTime = 30,
            notes = ""
        )

        setContent {
            BrewItem(brew = brew)
        }

        onNodeWithText("Method: Espresso").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemDisplaysFrenchPressMethod() = runComposeUiTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val brew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            coffeeName = "Test Coffee",
            date = today,
            method = BrewMethod.FRENCH_PRESS,
            dose = 30.0,
            yield = 500.0,
            brewTime = 240,
            notes = ""
        )

        setContent {
            BrewItem(brew = brew)
        }

        onNodeWithText("Method: French Press").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun brewItemEditButtonCallsOnEditClick() = runComposeUiTest {
        var editClicked = false
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val brew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            coffeeName = "Test Coffee",
            date = today,
            method = BrewMethod.POUR_OVER,
            dose = 20.0,
            yield = 300.0,
            brewTime = 180,
            notes = ""
        )

        setContent {
            BrewItem(
                brew = brew,
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
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val brew = BrewRecord(
            id = 1,
            coffeeStockId = 1,
            coffeeName = "Test Coffee",
            date = today,
            method = BrewMethod.POUR_OVER,
            dose = 20.0,
            yield = 300.0,
            brewTime = 180,
            notes = ""
        )

        setContent {
            BrewItem(
                brew = brew,
                onEditClick = {},
                onDeleteClick = { deleteClicked = true }
            )
        }

        onNodeWithContentDescription("Delete").performClick()

        assert(deleteClicked)
    }

    @Test
    fun brewMethodDisplayName_pourOver_returnsPourOver() {
        assertEquals("Pour Over", BrewMethod.POUR_OVER.displayName())
    }

    @Test
    fun brewMethodDisplayName_espresso_returnsEspresso() {
        assertEquals("Espresso", BrewMethod.ESPRESSO.displayName())
    }

    @Test
    fun brewMethodDisplayName_frenchPress_returnsFrenchPress() {
        assertEquals("French Press", BrewMethod.FRENCH_PRESS.displayName())
    }

    @Test
    fun brewMethodDisplayName_aeropress_returnsAeroPress() {
        assertEquals("AeroPress", BrewMethod.AEROPRESS.displayName())
    }

    @Test
    fun brewMethodDisplayName_drip_returnsDrip() {
        assertEquals("Drip", BrewMethod.DRIP.displayName())
    }

    @Test
    fun brewMethodDisplayName_moka_returnsMoka() {
        assertEquals("Moka", BrewMethod.MOKA.displayName())
    }

    @Test
    fun brewMethodDisplayName_coldBrew_returnsColdBrew() {
        assertEquals("Cold Brew", BrewMethod.COLD_BREW.displayName())
    }

    @Test
    fun brewMethodDisplayName_other_returnsOther() {
        assertEquals("Other", BrewMethod.OTHER.displayName())
    }
}
