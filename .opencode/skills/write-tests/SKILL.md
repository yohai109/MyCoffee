---
name: write-tests
description: Guide for writing Compose UI tests, unit tests, and Ktor server tests with patterns and examples.
---
# Write Tests

This skill guides you through writing tests for the MyCoffee project.

## Test Locations

| Test Type | Location |
|-----------|----------|
| Common Compose Tests | `composeApp/src/commonTest/kotlin/` |
| Desktop Tests | `composeApp/src/jvmTest/kotlin/` |
| iOS Tests | `composeApp/src/iosTest/kotlin/` |
| Android Tests | `composeApp/src/androidUnitTest/kotlin/` |
| Server Tests | `server/src/test/kotlin/` |
| Shared Tests | `shared/src/commonTest/kotlin/` |

## Base Test Setup

All screen tests use a `BaseTest` class that initializes an in-memory Room database:

```kotlin
// composeApp/src/commonTest/kotlin/com/yohai/mycoffee/BaseTest.kt
package com.yohai.mycoffee

import androidx.room.Room
import androidx.room.RoomDatabase
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.DatabaseFactory
import com.yohai.mycoffee.database.initDatabase
import kotlin.test.BeforeTest

class TestDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.inMemoryDatabaseBuilder<CoffeeDatabase>()
    }
}

open class BaseTest {
    @BeforeTest
    fun setup() {
        initDatabase(TestDatabaseFactory())
    }
}
```

Screen test classes extend `BaseTest`:

```kotlin
class MyScreenTest : com.yohai.mycoffee.BaseTest() {
    // tests here
}
```

## Compose UI Tests

### Setup
```kotlin
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
```

### Test Template
```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun componentName_behavior_expectedResult() = runComposeUiTest {
    // Given
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val testData = TestData(...)

    // When
    setContent {
        ComponentUnderTest(testData)
    }

    // Then
    onNodeWithText("Expected Text").assertIsDisplayed()
}
```

### Common Assertions
```kotlin
onNodeWithText("Text").assertIsDisplayed()
onNodeWithText("Text").assertDoesNotExist()
onNodeWithText("Text", substring = true).assertIsDisplayed()
onNodeWithContentDescription("Edit").assertIsDisplayed()
onNodeWithContentDescription("Delete").assertDoesNotExist()
```

### Interacting with UI
```kotlin
// Click buttons
onNodeWithText("Save").performClick()
onNodeWithContentDescription("Edit").performClick()

// Assert callbacks fired
var callbackCalled = false
onNodeWithText("Save").performClick()
assert(callbackCalled)
```

### Full Compose UI Test Example (from StockScreenTest)

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun stockItemDisplaysCorrectInformation() = runComposeUiTest {
    // Given
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val testStock = CoffeeStock(
        id = 1,
        name = "Ethiopian Yirgacheffe",
        roaster = "Blue Bottle",
        state = CoffeeState.NEW,
        size = 250.0,
        roastDate = today,
        openDate = null,
        finishDate = null,
        remainingWeight = 250.0,
    )

    // When
    setContent {
        StockItem(testStock)
    }

    // Then
    onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
    onNodeWithText("Roaster: Blue Bottle").assertIsDisplayed()
    onNodeWithText("State: NEW").assertIsDisplayed()
    onNodeWithText("250g").assertIsDisplayed()
}
```

### Testing State-Dependent Visibility

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun stockItemShowsOpenButtonForNewState() = runComposeUiTest {
    val testStock = CoffeeStock(
        id = 1, name = "Test Coffee", roaster = "Test Roaster",
        state = CoffeeState.NEW, size = 250.0,
        roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        openDate = null, finishDate = null,
    )
    setContent { StockItem(testStock) }
    onNodeWithText("Open").assertIsDisplayed()
    onNodeWithText("Finish").assertDoesNotExist()
}

@OptIn(ExperimentalTestApi::class)
@Test
fun stockItemHidesButtonsForFinishedState() = runComposeUiTest {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val testStock = CoffeeStock(
        id = 3, name = "Test Coffee", roaster = "Test Roaster",
        state = CoffeeState.FINISHED, size = 250.0,
        roastDate = today, openDate = today, finishDate = today,
    )
    setContent { StockItem(testStock) }
    onNodeWithText("Open").assertDoesNotExist()
    onNodeWithText("Finish").assertDoesNotExist()
}
```

### Testing Callback Invocation

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun stockItemEditButtonCallsOnEditClick() = runComposeUiTest {
    // Given
    var editClicked = false
    val testStock = CoffeeStock(
        id = 1, name = "Test Coffee", roaster = "Test Roaster",
        state = CoffeeState.NEW, size = 250.0,
        roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        openDate = null, finishDate = null,
    )

    // When
    setContent {
        StockItem(stock = testStock, onEditClick = { editClicked = true })
    }
    onNodeWithContentDescription("Edit").performClick()

    // Then
    assert(editClicked)
}
```

### Testing Dialogs

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun addDialogDisplaysCorrectly() = runComposeUiTest {
    setContent {
        AddDialog(onDismiss = {}, onConfirm = { _, _, _, _, _, _, _ -> })
    }
    onNodeWithText("Add New Item").assertIsDisplayed()
    onNodeWithText("Name").assertIsDisplayed()
    onNodeWithText("Cancel").assertIsDisplayed()
    onNodeWithText("Add").assertIsDisplayed()
}

@OptIn(ExperimentalTestApi::class)
@Test
fun addDialogWithInitialData_prefillsFields() = runComposeUiTest {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val initialItem = CoffeeStock(
        id = 1, name = "Ethiopian Yirgacheffe", roaster = "Blue Bottle",
        state = CoffeeState.NEW, size = 250.0, roastDate = today,
        openDate = null, finishDate = null,
    )
    setContent {
        AddDialog(onDismiss = {}, onConfirm = { _, _, _, _, _, _, _ -> }, initialStock = initialItem)
    }
    onNodeWithText("Edit Stock").assertIsDisplayed()
    onNodeWithText("Save").assertIsDisplayed()
    onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
}
```

### Testing callbacks with assertions

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun addDialog_callsOnConfirmWithPreFilledValues() = runComposeUiTest {
    // Given
    var confirmCalled = false
    var confirmedName = ""

    setContent {
        AddDialog(
            onDismiss = {},
            onConfirm = { name, _, _, _, _, _, _ ->
                confirmCalled = true
                confirmedName = name
            },
            initialStock = initialStock
        )
    }

    // When
    onNodeWithText("Save").performClick()

    // Then
    assert(confirmCalled)
    assertEquals("Test Coffee", confirmedName)
}
```

## Unit Tests

Unit tests for utility functions live in the same file as the corresponding screen test.

### Date API

Always use `Clock.System.todayIn(TimeZone.currentSystemDefault())` for current date:

```kotlin
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
```

### Test Template
```kotlin
@Test
fun functionName_withCondition_expectedResult() {
    // Given
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val input = listOf(...)

    // When
    val result = functionUnderTest(input)

    // Then
    assertEquals(expected, result)
}
```

### Full Unit Test Example

```kotlin
@Test
fun calculateAverageOpenTime_withNoFinishedBags_returnsNull() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val stockList = listOf(
        CoffeeStock(id = 1, state = CoffeeState.NEW, size = 250.0, roastDate = today,
            openDate = null, finishDate = null, roaster = "R", name = "C"),
        CoffeeStock(id = 2, state = CoffeeState.OPEN, size = 250.0, roastDate = today,
            openDate = today, finishDate = null, roaster = "R", name = "C"),
    )
    val result = calculateAverageOpenTime(stockList)
    assertNull(result)
}

@Test
fun calculateAverageOpenTime_withFinishedBags_returnsAverage() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tenDaysAgo = today.minus(10, DateTimeUnit.DAY)
    val stockList = listOf(
        CoffeeStock(id = 1, state = CoffeeState.FINISHED, size = 250.0,
            roastDate = tenDaysAgo, openDate = tenDaysAgo, finishDate = today,
            roaster = "R", name = "C"),
        CoffeeStock(id = 2, state = CoffeeState.FINISHED, size = 250.0,
            roastDate = tenDaysAgo, openDate = tenDaysAgo, finishDate = today,
            roaster = "R", name = "C"),
    )
    val result = calculateAverageOpenTime(stockList)
    assertEquals(10.0, result)
}
```

## Testing with In-Memory Database

Use `TestDatabaseFactory` to test DAO operations without a real database:

```kotlin
@Test
fun insertAndQueryStock() = runTest {
    val db = getDatabase()
    val dao = db.coffeeDao()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    dao.insertStock(CoffeeStock(name = "Test", roaster = "R", state = CoffeeState.NEW,
        size = 250.0, roastDate = today, openDate = null, finishDate = null))

    val all = dao.getAllStock().first()
    assertEquals(1, all.size)
}
```

## Ktor Server Tests

```kotlin
import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*

@Test
fun endpoint_returnsExpectedResponse() = testApplication {
    application { module() }
    val response = client.get("/endpoint")
    assertEquals(HttpStatusCode.OK, response.status)
    assertEquals("expected", response.bodyAsText())
}
```

## Running Tests

```bash
# Single test class
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.StockScreenTest"

# Single test method
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.StockScreenTest.stockItemDisplaysCorrectInformation"

# All tests
./gradlew test
```

See [build-and-test](../build-and-test/SKILL.md) for the full list of test commands per module.

## Test Naming Convention

- Class: `<Component>Test` (e.g., `StockScreenTest`)
- Method: `methodName_scenario_expectedBehavior` (e.g., `calculateAverageOpenTime_withNoFinishedBags_returnsNull`)

## Related Skills

- [Build & Test](../build-and-test/SKILL.md) — for running tests and interpreting results
