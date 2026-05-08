---
name: add-new-screen
description: Step-by-step guide for creating new screens with navigation, database integration, and tests.
---
# Add New Screen

This skill guides you through creating a new screen in the MyCoffee app.

## Steps

### 1. Create the Screen File

Create a new file in `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/ui/screens/`:

```kotlin
package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("New Screen")
        }
    }
}
```

### 2. Add Navigation Route

Update `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/App.kt`:

```kotlin
import com.yohai.mycoffee.ui.screens.NewScreen

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Stock : Screen("stock", "Stock", Icons.AutoMirrored.Filled.List)
    data object Brew : Screen("brew", "Brew", Icons.Default.Refresh)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object New : Screen("new", "New", Icons.Default.Add)
}

// Add to items list:
val items = listOf(
    Screen.Stock,
    Screen.Brew,
    Screen.Settings,
    Screen.New,
)

// In NavHost block:
composable(Screen.New.route) {
    NewScreen()
}
```

### 3. Register Import in App.kt

Add the import at the top of `App.kt`:

```kotlin
import com.yohai.mycoffee.ui.screens.NewScreen
```

## Screen Pattern Template

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

**Note**: Screens use `Scaffold` for local FAB and dialog support. The `TopAppBar` is managed centrally in `App.kt` via `CenterAlignedTopAppBar` — do not add your own in screen files.

## Key Imports

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
```

## Testing

Create test file at `composeApp/src/commonTest/kotlin/com/yohai/mycoffee/ui/screens/ScreenNameTest.kt`:

```kotlin
package com.yohai.mycoffee.ui.screens

import androidx.compose.ui.test.*
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test

class ScreenNameTest : com.yohai.mycoffee.BaseTest() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun screenName_displaysTitle() = runComposeUiTest {
        setContent { ScreenName() }
        onNodeWithText("Screen Title").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun screenName_showsFab() = runComposeUiTest {
        setContent { ScreenName() }
        onNodeWithContentDescription("Add").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun screenName_fabClick_opensDialog() = runComposeUiTest {
        setContent { ScreenName() }
        onNodeWithContentDescription("Add").performClick()
        // Assert dialog appeared
    }
}
```

For more detailed testing patterns (callback verification, state-dependent UI, utility functions), see the [write-tests](../write-tests/SKILL.md) skill.

## Related Skills

- [Database/Entity](../database-entity/SKILL.md) — for adding new entities and DAOs needed by your screen
- [Write Tests](../write-tests/SKILL.md) — for comprehensive testing patterns
