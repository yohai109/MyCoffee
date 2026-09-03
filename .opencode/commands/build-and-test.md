---
name: build-and-test
description: Prescriptive build and test workflow for the MyCoffee Kotlin Multiplatform project.
---

# Build & Test

You must use this skill whenever you need to validate a change in MyCoffee. Always run the smallest relevant command first, then expand to broader verification before you finish.

## Rules You Must Follow

- You must run a baseline verification command before changing code when practical.
- You must run module-specific verification immediately after changing that module.
- You must run `./gradlew test` before closing work unless a pre-existing failure blocks it.
- You must never claim a command passed unless you actually ran it.
- You must stop and investigate any unexpected failure instead of skipping it.

## 1. Choose the Smallest Correct Command

Pick the narrowest command that covers the files you changed.

### Compose UI

```bash
./gradlew :composeApp:testDebugUnitTest
./gradlew :composeApp:jvmTest
./gradlew :composeApp:assembleDebug
```

### Shared Database Code

```bash
./gradlew :shared:allTests
```

### Server

```bash
./gradlew :server:test
./gradlew :server:build
```

### Full JVM Verification

```bash
./gradlew test
```

### Verification

- Verify the selected command actually covers the module you changed.
- Verify you did not skip a more relevant module-specific command.

## 2. Run Targeted Tests First

When you change behavior, you must run the most focused test command available before broader verification.

```bash
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.StockScreenTest"
./gradlew :server:test --tests "com.yohai.mycoffee.ApplicationTest"
./gradlew :shared:allTests --tests "com.yohai.mycoffee.SharedCommonTest"
```

### Verification

- Verify the targeted test name matches the class or method you intended to exercise.
- Verify the targeted command passed, or capture the exact failure.

## 3. Run Build Validation After Tests

After targeted tests pass, you must run the relevant build command for the changed area.

```bash
./gradlew :composeApp:assembleDebug
./gradlew :server:build
```

### Verification

- Verify the build completed without compilation errors.
- Verify no newly introduced warnings indicate a broken change.

## 4. Finish With Repository-Wide Validation

Before you finish, you must run:

```bash
./gradlew test
```

If `./gradlew test` already failed before your changes, you must still run it again and clearly report whether the same pre-existing failure remains unchanged.

### Verification

- Verify `./gradlew test` passed, or record the exact pre-existing failure.
- Verify your change did not introduce any additional failing task.

## Platform Reference

| Target | Command | Notes |
|---|---|---|
| Android assemble | `./gradlew :composeApp:assembleDebug` | Must compile the Android app. |
| Android unit tests | `./gradlew :composeApp:testDebugUnitTest` | JVM/Robolectric; no emulator required. |
| Desktop tests | `./gradlew :composeApp:jvmTest` | Use for Compose desktop logic. |
| Shared module | `./gradlew :shared:allTests` | Covers shared Room/database code. |
| Server tests | `./gradlew :server:test` | Use for Ktor behavior changes. |
| Full verification | `./gradlew test` | Required before finishing. |

## Related Skills

- `.agent/skills/write-tests/SKILL.md` — use to decide what tests to add before running commands.
- `.agent/skills/creating-pr/SKILL.md` — use after verification is complete and you are ready to submit work.


## User Request

$ARGUMENTS
