package com.yohai.mycoffee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = LightCream,
    primaryContainer = Espresso,
    onPrimaryContainer = LightCream,
    secondary = Cream,
    onSecondary = DarkRoast,
    secondaryContainer = LatteFoam,
    onSecondaryContainer = DarkRoast,
    background = LightCream,
    onBackground = DarkRoast,
    surface = LightCream,
    onSurface = DarkRoast,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkCoffeeBrown,
    onPrimary = WhiteFoam,
    primaryContainer = DarkEspresso,
    onPrimaryContainer = WhiteFoam,
    secondary = DarkCream,
    onSecondary = WhiteFoam,
    secondaryContainer = DarkLatteFoam,
    onSecondaryContainer = WhiteFoam,
    background = DarkBackground,
    onBackground = WhiteFoam,
    surface = DarkBackground,
    onSurface = WhiteFoam,
)

@Composable
fun MyCoffeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
