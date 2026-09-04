package com.yohai.mycoffee

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.yohai.mycoffee.ui.screens.BrewScreen
import com.yohai.mycoffee.ui.screens.SettingsScreen
import com.yohai.mycoffee.ui.screens.StockScreen
import com.yohai.mycoffee.ui.screens.RecipeScreen
import com.yohai.mycoffee.ui.screens.TimerScreen
import com.yohai.mycoffee.ui.theme.MyCoffeeTheme
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.database.getDatabase
import com.yohai.mycoffee.ui.WindowLayoutClass
import com.yohai.mycoffee.ui.windowLayoutClassForWidth
import org.jetbrains.compose.resources.stringResource
import mycoffee.composeapp.generated.resources.Res
import mycoffee.composeapp.generated.resources.app_stock_subtitle
import mycoffee.composeapp.generated.resources.more_destinations
import mycoffee.composeapp.generated.resources.stock
import mycoffee.composeapp.generated.resources.brew
import mycoffee.composeapp.generated.resources.settings
import mycoffee.composeapp.generated.resources.recipes
import mycoffee.composeapp.generated.resources.timer

internal fun secondaryNavigationOptions() = navOptions {
    launchSingleTop = true
}

internal fun originatingPrimaryRoute(currentRoute: String?, secondaryOrigin: String) =
    currentRoute?.takeIf {
        it == Screen.Stock.route || it == Screen.Brew.route || it == Screen.Settings.route
    } ?: secondaryOrigin

sealed class Screen(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelResource: org.jetbrains.compose.resources.StringResource
) {
    data object Stock : Screen("stock", "Stock", Icons.AutoMirrored.Filled.List, Res.string.stock)
    data object Brew : Screen("brew", "Brew", Icons.Default.Refresh, Res.string.brew)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings, Res.string.settings)
    data object Recipes : Screen("recipes", "Recipes", Icons.Default.Refresh, Res.string.recipes)
    data object Timer : Screen("timer", "Timer", Icons.Default.Refresh, Res.string.timer)
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
        )
        val destinations = items + listOf(Screen.Recipes, Screen.Timer)
        var secondaryExpanded by remember { mutableStateOf(false) }
        var secondaryOrigin by rememberSaveable { mutableStateOf(Screen.Stock.route) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentRoute = currentDestination?.route
        val originatingRoute = originatingPrimaryRoute(currentRoute, secondaryOrigin)
        val currentScreen = destinations.find { it.route == currentRoute } ?: Screen.Stock

        BoxWithConstraints {
        val layoutClass = windowLayoutClassForWidth(maxWidth.value.toInt())
        Scaffold(
            topBar = {
                 TopAppBar(
                    title = {
                        Column {
                             Text(stringResource(currentScreen.labelResource), style = MaterialTheme.typography.titleLarge)
                            if (currentScreen == Screen.Stock) {
                                 Text(stringResource(Res.string.app_stock_subtitle), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { secondaryExpanded = true }) {
                             Icon(Icons.Default.MoreVert, contentDescription = stringResource(Res.string.more_destinations))
                        }
                        DropdownMenu(expanded = secondaryExpanded, onDismissRequest = { secondaryExpanded = false }) {
                            listOf(Screen.Recipes, Screen.Timer).forEach { screen ->
                                 DropdownMenuItem(text = { Text(stringResource(screen.labelResource)) }, onClick = {
                                    secondaryExpanded = false
                                    secondaryOrigin = originatingRoute
                                    navController.navigate(
                                        screen.route,
                                        secondaryNavigationOptions()
                                    )
                                })
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
                             icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelResource)) },
                             label = { Text(stringResource(screen.labelResource)) },
                             selected = originatingRoute == screen.route,
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
             Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
             NavHost(
                 navController,
                 startDestination = Screen.Stock.route,
                 Modifier.padding(innerPadding).widthIn(max = if (layoutClass == WindowLayoutClass.EXPANDED) 1100.dp else 720.dp)
             ) {
                composable(Screen.Stock.route) { StockScreen(settings = settings) }
                composable(Screen.Brew.route) { BrewScreen(settings = settings) }
                composable(Screen.Settings.route) { SettingsScreen(database = database, settings = settings) }
                 composable("${Screen.Recipes.route}?origin={origin}") { RecipeScreen(database, settings) }
                 composable("${Screen.Timer.route}?origin={origin}") { TimerScreen() }
             }
             }
         }
     }
     }
}
