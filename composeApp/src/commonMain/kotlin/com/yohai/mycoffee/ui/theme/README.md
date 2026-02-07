# MyCoffee Theme

This package contains the theme configuration for the MyCoffee app.

## Structure

- **Color.kt** - Defines the coffee-themed color palette for light and dark modes
- **Theme.kt** - Contains the `MyCoffeeTheme` composable that applies the custom color scheme

## Usage

The theme is already applied in `App.kt`:

```kotlin
@Composable
fun App() {
    MyCoffeeTheme {
        // Your app content
    }
}
```

The theme automatically adapts to the system's dark mode setting. You can also force a specific theme:

```kotlin
MyCoffeeTheme(darkTheme = true) {
    // Content will use dark theme
}
```

## Changing Colors

To change the app's colors across all screens, simply update the color values in `Color.kt`:

```kotlin
// Light Theme Colors
val CoffeeBrown = Color(0xFF6F4E37)  // Change this to modify the primary color
val Cream = Color(0xFFF5E6D3)        // Change this to modify the secondary color
// ... etc
```

The color scheme follows Material3 conventions:
- **primary** - Main brand color used for prominent UI elements
- **secondary** - Accent color for less prominent UI elements
- **background** - Color of the app background
- **surface** - Color of surfaces like cards and sheets
- **onPrimary/onSecondary/etc** - Colors used for content on top of their respective colors
