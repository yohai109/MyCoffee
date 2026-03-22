---
name: build-and-test
description: Commands for building and testing the MyCoffee Kotlin Multiplatform project across all targets (Android, iOS, Desktop, Server).
---

# Build & Test


This skill provides commands for building and testing the MyCoffee Kotlin Multiplatform project.

## Build Commands

### Android
```bash
./gradlew :composeApp:assembleDebug          # Build debug APK
./gradlew :composeApp:installDebug           # Build and install on device
```

### Desktop (JVM)
```bash
./gradlew :composeApp:run                    # Run desktop app
```

### Server
```bash
./gradlew :server:run                        # Run server
./gradlew :server:build                     # Build server JAR
```

### iOS
Open `/iosApp` directory in Xcode and run from there.

## Test Commands

### Run All Tests
```bash
./gradlew test
```

### Run Single Test Class
```bash
./gradlew :composeApp:testDebugUnitTest --tests "com.yohai.mycoffee.ui.screens.StockScreenTest"
./gradlew :server:test --tests "com.yohai.mycoffee.ApplicationTest"
./gradlew :shared:testDebugUnitTest --tests "com.yohai.mycoffee.SharedCommonTest"
```

### Run Specific Module Tests
```bash
./gradlew :composeApp:allTests               # All composeApp tests
./gradlew :composeApp:jvmTest                # Desktop tests only
./gradlew :composeApp:testDebugUnitTest      # Android unit tests
./gradlew :composeApp:iosSimulatorArm64Test   # iOS tests
./gradlew :server:test                        # Server tests
./gradlew :shared:allTests                    # Shared module tests
```

## Workflow

1. **Before writing code**: Run `./gradlew test` to ensure existing tests pass
2. **After making changes**: Run relevant tests for the module you modified
3. **Before PR**: Run `./gradlew test` for full test suite

## Module Structure

| Module | Location | Description |
|--------|----------|-------------|
| composeApp | /composeApp | Compose Multiplatform UI |
| server | /server | Ktor server application |
| shared | /shared | Common code with Room database |
