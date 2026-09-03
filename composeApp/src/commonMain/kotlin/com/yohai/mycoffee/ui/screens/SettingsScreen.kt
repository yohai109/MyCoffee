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
import androidx.compose.material3.AlertDialog
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
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.ExportFormat
import com.yohai.mycoffee.ExportScope
import com.yohai.mycoffee.exportCsv
import com.yohai.mycoffee.exportJson
import com.yohai.mycoffee.saveExportFile
import com.yohai.mycoffee.shareExportFile
import kotlinx.datetime.todayIn
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
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
    var defaultBagSize by remember(currentSettings) { mutableStateOf(formatMeasurement(currentSettings.defaultBagSize, currentSettings.useGrams)) }
    var useDarkTheme by remember(currentSettings) { mutableStateOf(currentSettings.darkMode) }
    var selectedBrewMethod by remember(currentSettings) { mutableStateOf(currentSettings.defaultBrewMethod) }
    var defaultBrewDose by remember(currentSettings) { mutableStateOf(formatMeasurement(currentSettings.defaultBrewDose, currentSettings.useGrams)) }
    var defaultBrewYield by remember(currentSettings) { mutableStateOf(formatMeasurement(currentSettings.defaultBrewYield, currentSettings.useGrams)) }
    var brewMethodExpanded by remember { mutableStateOf(false) }
    val stock by database?.coffeeDao()?.getAllStock()?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<CoffeeStock>()) }
    val brews by database?.brewDao()?.getAllBrews()?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<BrewRecord>()) }
    var exportFormat by remember { mutableStateOf(ExportFormat.JSON) }
    var exportScope by remember { mutableStateOf(ExportScope.ALL) }
    var exportMessage by remember { mutableStateOf<String?>(null) }

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
                TextButton(onClick = {
                    save {
                        it.copy(
                            defaultBagSize = defaultBagSize.toDoubleOrNull()?.let { value -> if (useGrams) value else ouncesToGrams(value) } ?: it.defaultBagSize,
                            defaultBrewDose = defaultBrewDose.toDoubleOrNull()?.let { value -> if (useGrams) value else ouncesToGrams(value) } ?: it.defaultBrewDose,
                            defaultBrewYield = defaultBrewYield.toDoubleOrNull()?.let { value -> if (useGrams) value else ouncesToGrams(value) } ?: it.defaultBrewYield,
                            useGrams = useGrams
                        )
                    }
                }) { Text("Save") }
            }
            Text("Units", style = MaterialTheme.typography.titleMedium)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                tonalElevation = 1.dp
            ) { Column(Modifier.padding(4.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = useGrams, onClick = {
                        if (!useGrams) {
                            defaultBagSize = defaultBagSize.toDoubleOrNull()?.let(::ouncesToGrams)?.toString() ?: defaultBagSize
                            defaultBrewDose = defaultBrewDose.toDoubleOrNull()?.let(::ouncesToGrams)?.toString() ?: defaultBrewDose
                            defaultBrewYield = defaultBrewYield.toDoubleOrNull()?.let(::ouncesToGrams)?.toString() ?: defaultBrewYield
                        }
                        useGrams = true
                        save { it.copy(useGrams = true) }
                    })
                    Text("Grams", modifier = Modifier.padding(start = 8.dp))
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !useGrams, onClick = {
                        if (useGrams) {
                            defaultBagSize = defaultBagSize.toDoubleOrNull()?.let(::gramsToOunces)?.toString() ?: defaultBagSize
                            defaultBrewDose = defaultBrewDose.toDoubleOrNull()?.let(::gramsToOunces)?.toString() ?: defaultBrewDose
                            defaultBrewYield = defaultBrewYield.toDoubleOrNull()?.let(::gramsToOunces)?.toString() ?: defaultBrewYield
                        }
                        useGrams = false
                        save { it.copy(useGrams = false) }
                    })
                    Text("Ounces", modifier = Modifier.padding(start = 8.dp))
                }
            } }

            HorizontalDivider()

            Text("Default Bag Size", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = defaultBagSize,
                onValueChange = { defaultBagSize = it },
                label = { Text("Size (${if (useGrams) "grams" else "oz"})") },
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
                    onValueChange = { defaultBrewDose = it },
                    label = { Text("Dose (${if (useGrams) "grams" else "oz"})") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = defaultBrewYield,
                    onValueChange = { defaultBrewYield = it },
                    label = { Text("Yield (${if (useGrams) "grams" else "oz"})") },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()
            Text("Data export", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { exportFormat = ExportFormat.JSON }) { Text("JSON") }
                TextButton(onClick = { exportFormat = ExportFormat.CSV }) { Text("CSV") }
                TextButton(onClick = { exportScope = ExportScope.ALL }) { Text("All") }
                TextButton(onClick = { exportScope = ExportScope.STOCK }) { Text("Stock") }
                TextButton(onClick = { exportScope = ExportScope.BREWS }) { Text("Brews") }
            }
            TextButton(onClick = {
                val selectedStock = if (exportScope == ExportScope.BREWS) emptyList() else stock
                val selectedBrews = if (exportScope == ExportScope.STOCK) emptyList() else brews
                val extension = exportFormat.name.lowercase()
                val filename = "mycoffee_export_${Clock.System.todayIn(TimeZone.currentSystemDefault())}.$extension"
                val content = if (exportFormat == ExportFormat.JSON) exportJson(selectedStock, selectedBrews, filename) else exportCsv(selectedStock, selectedBrews)
                exportMessage = if (saveExportFile(filename, content)) {
                    shareExportFile(filename, content)
                    "Export saved to Downloads: $filename"
                } else "Could not save export"
            }, enabled = database != null) { Text("Export data") }

    }
    exportMessage?.let { message ->
        AlertDialog(onDismissRequest = { exportMessage = null }, text = { Text(message) }, confirmButton = { TextButton(onClick = { exportMessage = null }) { Text("OK") } })
    }
}
