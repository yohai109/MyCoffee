package com.yohai.mycoffee.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecipe
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.ui.screens.BrewItem
import com.yohai.mycoffee.ui.screens.RecipePreviewCard
import com.yohai.mycoffee.ui.screens.SettingsScreen
import com.yohai.mycoffee.ui.screens.StockItem
import com.yohai.mycoffee.ui.screens.TimerScreen
import com.yohai.mycoffee.ui.theme.MyCoffeeTheme
import kotlinx.datetime.LocalDate

private val previewDate = LocalDate(2026, 1, 1)

@Preview
@Composable
fun StockPopulatedPreview() = MyCoffeeTheme { StockItem(CoffeeStock(name = "Ethiopian", roaster = "Local", roastDate = previewDate, openDate = previewDate, finishDate = null, state = CoffeeState.OPEN, size = 250.0), Settings.DEFAULT) }

@Preview
@Composable
fun BrewPopulatedPreview() = MyCoffeeTheme { BrewItem(BrewRecord(coffeeStockId = 1, date = previewDate, method = BrewMethod.POUR_OVER, dose = 18.0, brewTime = 180, yield = 300.0, notes = null), "Ethiopian", Settings.DEFAULT, {}, {}) }

@Preview
@Composable
fun SettingsPreview() = MyCoffeeTheme { SettingsScreen(database = null) }

@Preview
@Composable
fun RecipePopulatedPreview() = MyCoffeeTheme { RecipePreviewCard(BrewRecipe(name = "Morning pour over", method = BrewMethod.POUR_OVER, dose = 18.0, yield = 300.0, brewTime = 180, waterTemperature = 94.0, notes = "Bright and clean"), Settings.DEFAULT) }

@Preview
@Composable
fun TimerPreview() = MyCoffeeTheme { TimerScreen() }

@Preview
@Composable
fun DarkSurfacePreview() = MyCoffeeTheme(darkTheme = true) { Surface { androidx.compose.material3.Text("MyCoffee") } }
