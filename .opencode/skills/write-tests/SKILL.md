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

## Compose UI Tests

### Setup
```kotlin
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
```

### Test Template
```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun componentName_behavior_expectedResult() = runComposeUiTest {
    // Given
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
onNodeWithText("Text").assertIsEnabled()
onNodeWithText("Text").assertIsNotEnabled()
onNodeWithText("Text", substring = true).assertIsDisplayed()
```

### Interacting with UI
```kotlin
onNodeWithText("Button").performClick()
onNodeWithText("TextField").performTextInput("user input")
onNodeWithText("Checkbox").performToggle()
```

## Unit Tests

### Setup
```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
```

### Test Template
```kotlin
@Test
fun functionName_withCondition_expectedResult() {
    // Given
    val input = prepareInput()
    
    // When
    val result = functionUnderTest(input)
    
    // Then
    assertEquals(expected, result)
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

## Testing Helper Functions

Functions like `calculateAverageOpenTime()` at the bottom of screen files should be tested with unit tests:

```kotlin
@Test
fun calculateAverageOpenTime_withEmptyList_returnsNull() {
    val result = calculateAverageOpenTime(emptyList())
    assertNull(result)
}

@Test
fun calculateAverageOpenTime_withFinishedBags_returnsAverage() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tenDaysAgo = today.minus(10, DateTimeUnit.DAY)
    
    val stockList = listOf(
        CoffeeStock(id = 1, state = CoffeeState.FINISHED, openDate = tenDaysAgo, finishDate = today, ...),
        CoffeeStock(id = 2, state = CoffeeState.FINISHED, openDate = tenDaysAgo, finishDate = today, ...)
    )
    
    val result = calculateAverageOpenTime(stockList)
    assertEquals(10.0, result)
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

## Test Naming Convention

- Class: `<Component>Test` (e.g., `StockScreenTest`)
- Method: `methodName_scenario_expectedBehavior` (e.g., `calculateAverageOpenTime_withNoFinishedBags_returnsNull`)
