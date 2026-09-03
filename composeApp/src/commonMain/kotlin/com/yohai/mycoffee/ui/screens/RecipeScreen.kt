package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecipe
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch

@Composable
fun RecipeScreen(
    database: CoffeeDatabase = remember { getDatabase() },
    settings: Settings = Settings.DEFAULT
) {
    val recipes by database.recipeDao().getAllRecipes().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<BrewRecipe?>(null) }
    var showNew by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(query, { query = it }, label = { Text("Search recipes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { showNew = true }) { Text("New recipe") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recipes.filter { it.name.contains(query, true) }, key = { it.id }) { recipe ->
                Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${recipe.name} · ${formatBrewMethod(recipe.method)}", style = MaterialTheme.typography.titleMedium)
                        Text("Dose ${formatDisplayMeasurement(recipe.dose, settings.useGrams)}${if (settings.useGrams) "g" else "oz"} · Yield ${recipe.yield?.let { formatDisplayMeasurement(it, settings.useGrams) + if (settings.useGrams) "g" else "oz" } ?: "-"} · ${recipe.brewTime}s")
                        Text("Water ${recipe.waterTemperature?.let { formatDisplayMeasurement(it, settings.useGrams) + if (settings.useGrams) "g" else "oz" } ?: "-"}")
                        Row {
                            TextButton(onClick = { editing = recipe }) { Text("Edit") }
                            TextButton(onClick = { scope.launch { database.recipeDao().insertRecipe(recipe.copy(id = 0, name = "${recipe.name} copy")) } }) { Text("Copy") }
                            TextButton(onClick = { scope.launch { database.recipeDao().deleteRecipe(recipe) } }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
    if (showNew || editing != null) RecipeFormDialog(
        initial = editing,
        useGrams = settings.useGrams,
        onDismiss = { editing = null; showNew = false },
        onSave = { recipe ->
            scope.launch { if (recipe.id == 0L) database.recipeDao().insertRecipe(recipe) else database.recipeDao().updateRecipe(recipe) }
            editing = null
            showNew = false
        }
    )
}

@Composable
fun RecipePreviewCard(recipe: BrewRecipe, settings: Settings = Settings.DEFAULT) {
    Column(Modifier.padding(12.dp)) {
        Text("${recipe.name} · ${formatBrewMethod(recipe.method)}", style = MaterialTheme.typography.titleMedium)
        Text("Dose ${formatDisplayMeasurement(recipe.dose, settings.useGrams)}${if (settings.useGrams) "g" else "oz"} · Yield ${recipe.yield?.let { formatDisplayMeasurement(it, settings.useGrams) } ?: "-"}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeFormDialog(
    initial: BrewRecipe?,
    useGrams: Boolean,
    onDismiss: () -> Unit,
    onSave: (BrewRecipe) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var method by remember(initial) { mutableStateOf(initial?.method ?: BrewMethod.ESPRESSO) }
    var dose by remember(initial) { mutableStateOf(initial?.dose?.let { formatMeasurement(it, useGrams) } ?: "18") }
    var yield by remember(initial) { mutableStateOf(initial?.yield?.let { formatMeasurement(it, useGrams) } ?: "36") }
    var time by remember(initial) { mutableStateOf(initial?.brewTime?.toString() ?: "30") }
    var temperature by remember(initial) { mutableStateOf(initial?.waterTemperature?.let { formatMeasurement(it, useGrams) } ?: "") }
    var notes by remember(initial) { mutableStateOf(initial?.notes ?: "") }
    var expanded by remember { mutableStateOf(false) }
    val doseError = measurementError(dose, "Dose", 0.1, 1000.0)
    val yieldError = optionalMeasurementError(yield, "Yield", 0.1, 5000.0)
    val timeError = integerError(time, "Brew time", 1, 86400, true)
    val tempError = optionalMeasurementError(temperature, "Water temperature", 0.1, 1000.0)
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (initial == null) "New recipe" else "Edit recipe", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), isError = name.isBlank())
                ExposedDropdownMenuBox(expanded, { expanded = it }) {
                    OutlinedTextField(formatBrewMethod(method), {}, readOnly = true, label = { Text("Method") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable))
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        BrewMethod.entries.forEach { option -> DropdownMenuItem({ Text(formatBrewMethod(option)) }, { method = option; expanded = false }) }
                    }
                }
                MeasurementField("Dose", dose, { dose = it }, doseError)
                MeasurementField("Yield (optional)", yield, { yield = it }, yieldError)
                MeasurementField("Water temperature (optional)", temperature, { temperature = it }, tempError)
                OutlinedTextField(time, { time = it }, label = { Text("Brew time (seconds)") }, isError = timeError != null, supportingText = timeError?.let { { Text(it) } }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = {
                        onSave(BrewRecipe(initial?.id ?: 0, name.trim(), method,
                            dose.toDouble() * if (useGrams) 1.0 else 28.3495,
                            yield.toDoubleOrNull()?.times(if (useGrams) 1.0 else 28.3495), time.toInt(),
                            temperature.toDoubleOrNull()?.times(if (useGrams) 1.0 else 28.3495), notes.ifBlank { null }))
                    }, enabled = name.isNotBlank() && doseError == null && yieldError == null && tempError == null && timeError == null) { Text("Save") }
                }
            }
        }
    }
}

@Composable
private fun MeasurementField(label: String, value: String, onValueChange: (String) -> Unit, error: String?) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, isError = error != null, supportingText = error?.let { { Text(it) } }, modifier = Modifier.fillMaxWidth())
}
