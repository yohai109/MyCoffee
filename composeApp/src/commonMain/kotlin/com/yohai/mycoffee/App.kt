package com.yohai.mycoffee

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yohai.mycoffee.ui.screens.BrewScreen
import com.yohai.mycoffee.ui.screens.SettingsScreen
import com.yohai.mycoffee.ui.screens.StockScreen
import com.yohai.mycoffee.ui.screens.RecipeScreen
import com.yohai.mycoffee.ui.theme.MyCoffeeTheme
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.database.getDatabase

sealed class Screen(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Stock : Screen("stock", "Stock", Icons.AutoMirrored.Filled.List)
    data object Brew : Screen("brew", "Brew", Icons.Default.Refresh)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object Recipes : Screen("recipes", "Recipes", Icons.Default.Refresh)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val database = remember { getDatabase() }
    val storedSettings by database.settingsDao().getSettings().collectAsState(initial = null)
    val settings = storedSettings ?: Settings.DEFAULT

    LaunchedEffect(storedSettings) {
        if (storedSettings == null) {
            database.settingsDao().insertSettings(Settings.DEFAULT)
        }
    }

    MyCoffeeTheme(darkTheme = settings.darkMode) {
        val navController = rememberNavController()
        val items = listOf(
            Screen.Stock,
            Screen.Brew,
            Screen.Settings,
            Screen.Recipes,
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentScreen = items.find { it.route == currentDestination?.route } ?: Screen.Stock

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(currentScreen.label, style = MaterialTheme.typography.titleLarge)
                            if (currentScreen == Screen.Stock) {
                                Text("Your coffee, at a glance", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars),
                    tonalElevation = 0.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    val startRoute =
                                        navController.graph.findStartDestination().route
                                    if (startRoute != null) {
                                        popUpTo(startRoute) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Stock.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Stock.route) { StockScreen(settings = settings) }
                composable(Screen.Brew.route) { BrewScreen(settings = settings) }
                composable(Screen.Settings.route) { SettingsScreen(database = database, settings = settings) }
                composable(Screen.Recipes.route) { RecipeScreen(database) }
            }
        }
    }
}
