package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewRecipe
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch

@Composable
fun RecipeScreen(database: CoffeeDatabase = remember { getDatabase() }) {
    val recipes by database.recipeDao().getAllRecipes().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(query, { query = it }, label = { Text("Search recipes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { scope.launch { database.recipeDao().insertRecipe(BrewRecipe(name = "New recipe", method = com.yohai.mycoffee.database.BrewMethod.ESPRESSO, dose = 18.0, yield = 36.0, brewTime = 30, waterTemperature = null, notes = null)) } }) { Text("New recipe") }
        LazyColumn { items(recipes.filter { it.name.contains(query, true) }, key = { it.id }) { recipe ->
            Text("${recipe.name} · ${formatBrewMethod(recipe.method)} · ${recipe.dose}g")
            TextButton(onClick = { scope.launch { database.recipeDao().insertRecipe(recipe.copy(id = 0, name = "${recipe.name} copy")) } }) { Text("Copy") }
            TextButton(onClick = { scope.launch { database.recipeDao().deleteRecipe(recipe) } }) { Text("Delete") }
        } }
    }
}
