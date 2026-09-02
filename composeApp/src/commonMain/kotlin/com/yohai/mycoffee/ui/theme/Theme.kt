package com.yohai.mycoffee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

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
    surface = LightSurface,
    onSurface = DarkRoast,
    surfaceVariant = LatteFoam,
    outline = LightOutline,
    tertiary = Sage,
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
    surface = DarkSurface,
    onSurface = WhiteFoam,
    surfaceVariant = DarkEspresso,
    outline = DarkOutline,
    tertiary = Sage,
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
        typography = CoffeeTypography,
        content = content
    )
}

private val CoffeeTypography = androidx.compose.material3.Typography().run {
    copy(
        titleLarge = titleLarge.copy(fontFamily = FontFamily.Serif),
        headlineSmall = headlineSmall.copy(fontFamily = FontFamily.Serif),
        displaySmall = displaySmall.copy(fontFamily = FontFamily.Serif)
    )
}
