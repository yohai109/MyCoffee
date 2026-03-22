# AGENTS.md - MyCoffee Development Guide

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM), and Server.

## Project Structure

```
/composeApp    - Compose Multiplatform UI code
/iosApp        - iOS entry point and SwiftUI code
/server        - Ktor server application
/shared        - Common code shared across all targets (Room database)
```

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

## Code Style Guidelines

### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Packages | `com.yohai.mycoffee.<module>` | `com.yohai.mycoffee.ui.screens` |
| Classes | PascalCase | `CoffeeStock`, `StockScreen` |
| Data Classes | PascalCase | `data class CoffeeStock` |
| Enums | PascalCase (members UPPER_SNAKE) | `CoffeeState.NEW` |
| Functions | camelCase | `getDatabase()`, `calculateAverage()` |
| Composables | PascalCase | `StockScreen()`, `StatisticsBanner()` |
| Constants | PascalCase | `SERVER_PORT`, `LightColorScheme` |
| Private vals/vars | camelCase | `darkTheme`, `stockList` |
| Colors | PascalCase | `CoffeeBrown`, `DarkRoast` |

### Import Organization

Organize imports with blank lines between groups:

1. AndroidX/Compose imports (`androidx.compose.*`)
2. Material icons (`androidx.compose.material.icons.*`)
3. Material3 components (`androidx.compose.material3.*`)
4. Navigation (`androidx.navigation.*`)
5. Room/Database imports (`com.yohai.mycoffee.database.*`)
6. Project other imports (`com.yohai.mycoffee.*`)
7. Kotlin standard library (`kotlin.*`)
8. kotlinx libraries (`kotlinx.*`)

### Composable Guidelines

- Use `@Composable` annotation for all UI functions
- Mark experimental APIs with `@OptIn(ExperimentalMaterial3Api::class)` etc.
- Prefer `remember` for stable values and `rememberSaveable` for state that survives process death
- Use `collectAsState()` for Flow collection in composables
- Use `rememberCoroutineScope()` for launching coroutines in composables

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val stockList: List<CoffeeStock> by database.coffeeDao().getAllStock().collectAsState(initial = emptyList())
}
```

### Database (Room) Guidelines

- Entity classes use `@Entity` annotation
- Use `suspend` functions for DAO operations
- Return `Flow<T>` for reactive data streams
- Use `@TypeConverters` for custom types (e.g., `LocalDate`)

```kotlin
@Entity
data class CoffeeStock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val state: CoffeeState
)

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM CoffeeStock")
    fun getAllStock(): Flow<List<CoffeeStock>>

    @Insert
    suspend fun insertStock(stock: CoffeeStock)
}
```

### Error Handling

- Use try-catch for parsing operations
- Track error state with boolean flags
- Display errors via `OutlinedTextField`'s `isError` and `supportingText`

```kotlin
val roastDate = try {
    if (roastDateText.isBlank()) null else LocalDate.parse(roastDateText)
} catch (e: Exception) {
    null
}
val isDateError = roastDateText.isNotBlank() && roastDate == null
```

### Null Safety

- Use Elvis operator `?:` for defaults
- Use safe call operators `?.`
- Avoid force unwrapping with `!!`

## Testing Guidelines

### Compose UI Tests

Use `runComposeUiTest` from `androidx.compose.ui.test`:

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun stockItemDisplaysCorrectInformation() = runComposeUiTest {
    // Given
    val testStock = CoffeeStock(...)
    
    // When
    setContent { StockItem(testStock) }
    
    // Then
    onNodeWithText("Ethiopian Yirgacheffe").assertIsDisplayed()
}
```

### Unit Tests

Use `kotlin.test` framework:

```kotlin
@Test
fun calculateAverageOpenTime_withNoFinishedBags_returnsNull() {
    // Given
    val stockList = listOf(...)
    
    // When
    val result = calculateAverageOpenTime(stockList)
    
    // Then
    assertNull(result)
}
```

### Ktor Server Tests

Use `testApplication`:

```kotlin
@Test
fun testRoot() = testApplication {
    application { module() }
    val response = client.get("/")
    assertEquals(HttpStatusCode.OK, response.status)
}
```

### Test Naming

- Test class names: `<Component>Test` (e.g., `StockScreenTest`)
- Test method naming: `methodName_scenario_expectedBehavior()`

## Architecture Notes

- **No ViewModels**: Screens manage state directly with `remember { mutableStateOf() }`
- **Database via expect/actual**: `getDatabase()` is defined with expect/actual pattern
- **Platform-specific code**: Use `iosMain`, `jvmMain`, `androidMain` directories
- **Navigation**: Uses `NavigationBar` with `NavHost` and sealed class `Screen` for routes

## Key Dependencies

- Kotlin 2.1.10
- Compose Multiplatform 1.7.3
- Room 2.7.0
- Ktor 3.0.3
- kotlinx-datetime 0.6.1
- Android SDK 36 (compile), minSdk 28
