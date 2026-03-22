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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Screen") })
        }
    ) { padding ->
        // Content here
    }
}
```

### 2. Add Navigation Route

Update `composeApp/src/commonMain/kotlin/com/yohai/mycoffee/App.kt`:

```kotlin
sealed class Screen(val route: String) {
    // ... existing screens
    object NewScreen : Screen("new_screen")
}

// In NavHost:
composable(Screen.NewScreen.route) {
    NewScreen()
}

// In NavigationBar:
NavigationBarItem(
    icon = { Icon(Icons.Default.New, contentDescription = "New") },
    label = { Text("New") },
    selected = currentRoute == Screen.NewScreen.route,
    onClick = { navController.navigate(Screen.NewScreen.route) }
)
```

### 3. Add Navigation Import

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
                .padding(16.dp)
        ) {
            // Content
        }
    }
}
```

## Key Imports

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
```

## Testing

Create test file at `composeApp/src/commonTest/kotlin/com/yohai/mycoffee/ui/screens/ScreenNameTest.kt`:

```kotlin
@OptIn(ExperimentalTestApi::class)
@Test
fun screenName_displaysTitle() = runComposeUiTest {
    setContent { ScreenName() }
    onNodeWithText("Screen Title").assertIsDisplayed()
}
```
