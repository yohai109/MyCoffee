---
name: add-new-screen
description: Prescriptive workflow for adding a new screen with navigation, state, and tests.
---

# Add New Screen

You must follow this workflow when adding a new screen to MyCoffee. Always keep the change small, wire the screen into navigation immediately, and verify each step before continuing.

## Rules You Must Follow

- You must create new screens under `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/`.
- You must keep the shared app navigation in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/App.kt` as the single source of truth for routes.
- You must use `remember { getDatabase() }`, `rememberCoroutineScope()`, and `collectAsState(...)` when the screen reads or writes database state.
- You must not add a second top app bar inside the screen. `App.kt` already owns the main app bar.
- You must add or update tests before you finish the task.
- You must never leave a screen unreachable from navigation.

## 1. Inspect an Existing Screen Pattern

Read an existing screen in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/` and inspect `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/App.kt` before you create anything new.

### Verification

- Verify you know which route, label, and icon pattern the app already uses.
- Verify the new screen belongs in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/`.

## 2. Create the Screen File

Create a composable in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/<ScreenName>.kt`.

You should start from this structure and adapt it to the feature:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenName() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* action */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Content
        }
    }
}
```

### Verification

- Verify the file is in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/`.
- Verify the composable name matches the file name and follows PascalCase.
- Verify the screen does not add its own top app bar.

## 3. Register the Screen in Navigation

Update `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/App.kt`.

You must:

1. Add the new screen import.
2. Add a new `Screen` entry with route, label, and icon.
3. Add the new entry to the navigation item list when the screen should be user-visible.
4. Add a `composable(...)` destination that calls the new screen.

### Verification

- Verify `App.kt` imports the new screen exactly once.
- Verify the route string is unique.
- Verify the new destination is reachable from the intended navigation flow.

## 4. Wire Data and Actions

If the screen reads or writes data, you must use the existing database patterns:

- Read with `collectAsState(initial = ...)`.
- Write inside `scope.launch { ... }`.
- Keep local UI state with `remember` or `rememberSaveable`.
- Use existing domain types instead of duplicating models.

### Verification

- Verify every database write happens inside a coroutine.
- Verify the UI has a stable initial state such as `emptyList()` or `null` handling.
- Verify there is no force unwrap (`!!`) in the new screen.

## 5. Add Focused Tests

Create or update tests in `composeApp/src/commonTest/kotlin/com/yohai/mycoffee/ui/screens/`.

You must cover:

- A basic render path.
- The primary user action.
- Any state-dependent visibility that the new screen introduces.

### Verification

- Verify the new test names follow `methodName_scenario_expectedBehavior`.
- Verify the test file follows the existing screen test package structure.
- Verify every new behavior added to the screen is asserted at least once.

## 6. Run Verification Commands

Run the smallest relevant commands first, then run broader verification before finishing.

```bash
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.<ScreenName>Test"
./gradlew :composeApp:assembleDebug
./gradlew test
```

If a command fails, you must fix the failure or explicitly document that the failure was pre-existing and unrelated.

### Verification

- Verify the targeted screen test covers the new path.
- Verify the Android app still assembles.
- Verify `./gradlew test` is recorded as pass or documented as a pre-existing failure.

## Related Skills

- `.agent/skills/database-entity/SKILL.md` — use when the screen requires new Room entities, DAOs, or migrations.
- `.agent/skills/write-tests/SKILL.md` — use for deeper test patterns and edge-case coverage.
- `.agent/skills/build-and-test/SKILL.md` — use for the required verification command sequence.


## User Request

$ARGUMENTS
