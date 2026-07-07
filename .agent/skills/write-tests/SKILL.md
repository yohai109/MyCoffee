---
name: write-tests
description: Prescriptive workflow for adding focused Compose UI, shared, and server tests in MyCoffee.
---

# Write Tests

You must use this workflow whenever you add or change behavior in MyCoffee. Tests must prove the changed behavior, not just exercise code paths.

## Rules You Must Follow

- You must place tests in the correct source set for the code you changed.
- You must prefer focused tests over broad, redundant coverage.
- You must assert observable behavior.
- You must cover at least one happy path and the important edge cases introduced by the change.
- You must run the smallest relevant test command before broader verification.

## 1. Choose the Correct Test Location

Use the existing source sets:

| Test Type | Location |
|---|---|
| Common Compose tests | `composeApp/src/commonTest/kotlin/` |
| Desktop tests | `composeApp/src/jvmTest/kotlin/` |
| iOS tests | `composeApp/src/iosTest/kotlin/` |
| Android unit tests | `composeApp/src/androidUnitTest/kotlin/` |
| Server tests | `server/src/test/kotlin/` |
| Shared tests | `shared/src/commonTest/kotlin/` |

### Verification

- Verify the new test file matches the module and platform of the changed code.
- Verify the package path mirrors the production package when applicable.

## 2. Reuse Existing Test Setup

For screen tests, you should reuse the existing base setup in `composeApp/src/commonTest/kotlin/com/yohai/mycoffee/BaseTest.kt`.

Typical structure:

```kotlin
class MyScreenTest : com.yohai.mycoffee.BaseTest() {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myScreen_scenario_expectedBehavior() = runComposeUiTest {
        setContent { MyScreen() }
        onNodeWithText("Expected Text").assertIsDisplayed()
    }
}
```

### Verification

- Verify screen tests extend `BaseTest` when they need the initialized database.
- Verify the test name follows `methodName_scenario_expectedBehavior`.

## 3. Assert Real Behavior

You must assert the visible or externally observable result of the change.

Use Compose test APIs such as:

```kotlin
onNodeWithText("Text").assertIsDisplayed()
onNodeWithText("Text").assertDoesNotExist()
onNodeWithContentDescription("Edit").performClick()
```

For pure logic, use `kotlin.test` assertions such as `assertEquals`, `assertNull`, and `assertTrue`.

### Verification

- Verify each test would fail if the changed behavior regressed.
- Verify the assertions check output, state, or callbacks instead of only executing code.

## 4. Cover Edge Cases Introduced by the Change

You must add coverage for the most important branch conditions introduced by the code change, such as:

- empty lists
- null values
- state-dependent button visibility
- invalid input handling
- migration or persistence edge cases

### Verification

- Verify at least one edge case is covered when the change introduces branching logic.
- Verify the edge-case test is independent and deterministic.

## 5. Run Targeted Verification

Run the smallest relevant command first.

```bash
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.StockScreenTest"
./gradlew :server:test --tests "com.yohai.mycoffee.ApplicationTest"
./gradlew :shared:allTests
```

Then finish with:

```bash
./gradlew test
```

### Verification

- Verify the targeted command actually exercised the new tests.
- Verify `./gradlew test` passed or is documented as a pre-existing failure.

## Related Skills

- `.agent/skills/build-and-test/SKILL.md` — use for the required command sequence and escalation from targeted to broad verification.
- `.agent/skills/database-entity/SKILL.md` — use when test coverage must include Room entities, DAOs, or migrations.
