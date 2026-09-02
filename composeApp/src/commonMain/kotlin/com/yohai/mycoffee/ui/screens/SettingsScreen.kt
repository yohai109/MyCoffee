package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.Settings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    database: CoffeeDatabase? = null,
    settings: Settings = Settings.DEFAULT
) {
    val currentSettings = settings
    val scope = rememberCoroutineScope()
    var useGrams by remember(currentSettings) { mutableStateOf(currentSettings.useGrams) }
    var defaultBagSize by remember(currentSettings) { mutableStateOf(currentSettings.defaultBagSize.toString()) }
    var useDarkTheme by remember(currentSettings) { mutableStateOf(currentSettings.darkMode) }
    var selectedBrewMethod by remember(currentSettings) { mutableStateOf(currentSettings.defaultBrewMethod) }
    var defaultBrewDose by remember(currentSettings) { mutableStateOf(currentSettings.defaultBrewDose.toString()) }
    var defaultBrewYield by remember(currentSettings) { mutableStateOf(currentSettings.defaultBrewYield.toString()) }
    var brewMethodExpanded by remember { mutableStateOf(false) }

    fun save(update: (Settings) -> Settings) {
        database?.let { db ->
            scope.launch { db.settingsDao().updateSettings(update(currentSettings)) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("App settings and preferences", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                TextButton(onClick = { save { it.copy(defaultBagSize = defaultBagSize.toDoubleOrNull() ?: it.defaultBagSize, defaultBrewDose = defaultBrewDose.toDoubleOrNull() ?: it.defaultBrewDose, defaultBrewYield = defaultBrewYield.toDoubleOrNull() ?: it.defaultBrewYield) } }) { Text("Save") }
            }
            Text("Units", style = MaterialTheme.typography.titleMedium)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                tonalElevation = 1.dp
            ) { Column(Modifier.padding(4.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = useGrams, onClick = { useGrams = true; save { it.copy(useGrams = true) } })
                    Text("Grams", modifier = Modifier.padding(start = 8.dp))
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !useGrams, onClick = { useGrams = false; save { it.copy(useGrams = false) } })
                    Text("Ounces", modifier = Modifier.padding(start = 8.dp))
                }
            } }

            HorizontalDivider()

            Text("Default Bag Size", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = defaultBagSize,
                onValueChange = { defaultBagSize = it; it.toDoubleOrNull()?.takeIf { value -> value > 0 }?.let { value -> save { settings -> settings.copy(defaultBagSize = value) } } },
                label = { Text("Size") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text("Theme", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = useDarkTheme,
                    onCheckedChange = { useDarkTheme = it; save { settings -> settings.copy(darkMode = it) } }
                )
                Text(
                    if (useDarkTheme) "Dark theme" else "Light theme",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider()

            Text("Default Brew Method", style = MaterialTheme.typography.titleMedium)

            ExposedDropdownMenuBox(
                expanded = brewMethodExpanded,
                onExpandedChange = { brewMethodExpanded = it }
            ) {
                OutlinedTextField(
                    value = formatBrewMethod(selectedBrewMethod),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brewMethodExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = brewMethodExpanded,
                    onDismissRequest = { brewMethodExpanded = false },
                ) {
                    BrewMethod.entries.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(formatBrewMethod(method)) },
                            onClick = {
                                selectedBrewMethod = method
                                save { it.copy(defaultBrewMethod = method) }
                                brewMethodExpanded = false
                            }
                        )
                    }
                }
             }

            Text("Default Brew Recipe", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = defaultBrewDose,
                    onValueChange = { defaultBrewDose = it; it.toDoubleOrNull()?.takeIf { value -> value > 0 }?.let { value -> save { settings -> settings.copy(defaultBrewDose = value) } } },
                    label = { Text("Dose (grams)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = defaultBrewYield,
                    onValueChange = { defaultBrewYield = it; it.toDoubleOrNull()?.takeIf { value -> value > 0 }?.let { value -> save { settings -> settings.copy(defaultBrewYield = value) } } },
                    label = { Text("Yield (grams)") },
                    modifier = Modifier.weight(1f)
                )
            }

    }
}
