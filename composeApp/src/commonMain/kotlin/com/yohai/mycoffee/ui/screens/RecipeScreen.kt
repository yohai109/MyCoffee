package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecipe
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch

@Composable
fun RecipeScreen(database: CoffeeDatabase = remember { getDatabase() }) {
    val recipes by database.recipeDao().getAllRecipes().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<BrewRecipe?>(null) }
    val visible = recipes.filter { it.name.contains(query, ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(query, { query = it }, label = { Text("Search recipes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { editing = BrewRecipe(name = "", method = BrewMethod.ESPRESSO, dose = 18.0, yield = 36.0, brewTime = 30, waterTemperature = null, notes = null) }) {
            Text("New recipe")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { recipe ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(recipe.name)
                        Text("${formatBrewMethod(recipe.method)} · ${recipe.dose}g · ${formatBrewTime(recipe.brewTime)}")
                    }
                    TextButton(onClick = { scope.launch { database.recipeDao().insertRecipe(recipe.copy(id = 0, name = "${recipe.name} copy")) } }) { Text("Copy") }
                    TextButton(onClick = { editing = recipe }) { Text("Edit") }
                    TextButton(onClick = { scope.launch { database.recipeDao().deleteRecipe(recipe) } }) { Text("Delete") }
                }
            }
        }
    }
    editing?.let { recipe ->
        RecipeDialog(recipe, onDismiss = { editing = null }) { updated ->
            scope.launch {
                if (updated.id == 0L) database.recipeDao().insertRecipe(updated) else database.recipeDao().updateRecipe(updated)
                editing = null
            }
        }
    }
}

@Composable
private fun RecipeDialog(recipe: BrewRecipe, onDismiss: () -> Unit, onSave: (BrewRecipe) -> Unit) {
    var name by remember(recipe) { mutableStateOf(recipe.name) }
    var dose by remember(recipe) { mutableStateOf(recipe.dose.toString()) }
    var yield by remember(recipe) { mutableStateOf(recipe.yield?.toString() ?: "") }
    var time by remember(recipe) { mutableStateOf(recipe.brewTime.toString()) }
    var temperature by remember(recipe) { mutableStateOf(recipe.waterTemperature?.toString() ?: "") }
    var notes by remember(recipe) { mutableStateOf(recipe.notes ?: "") }
    var method by remember(recipe) { mutableStateOf(recipe.method) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (recipe.id == 0L) "New recipe" else "Edit recipe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(dose, { dose = it }, label = { Text("Dose (g)") })
                OutlinedTextField(yield, { yield = it }, label = { Text("Yield (g)") })
                OutlinedTextField(time, { time = it }, label = { Text("Brew time (seconds)") })
                Text("Brew method")
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    BrewMethod.entries.take(4).forEach { option ->
                        TextButton(onClick = { method = option }) { Text(formatBrewMethod(option)) }
                    }
                }
                OutlinedTextField(temperature, { temperature = it }, label = { Text("Water temperature") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = { TextButton(onClick = { onSave(recipe.copy(name = name, method = method, dose = dose.toDoubleOrNull() ?: recipe.dose, yield = yield.toDoubleOrNull(), brewTime = time.toIntOrNull() ?: recipe.brewTime, waterTemperature = temperature.toDoubleOrNull(), notes = notes.ifBlank { null })) }, enabled = name.isNotBlank()) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
