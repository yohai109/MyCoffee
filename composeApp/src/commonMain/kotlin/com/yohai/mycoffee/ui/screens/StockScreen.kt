package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val stockList: List<CoffeeStock> by database.coffeeDao().getAllStock().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Stock")
            }
        }
    ) { padding ->
        if (stockList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No coffee stock tracked yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stockList) { stock ->
                    StockItem(
                        stock = stock,
                        onOpenClick = {
                            scope.launch {
                                database.coffeeDao().updateStock(
                                    stock.copy(
                                        state = CoffeeState.OPEN,
                                        openDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                                    )
                                )
                            }
                        },
                        onFinishClick = {
                            scope.launch {
                                database.coffeeDao().updateStock(
                                    stock.copy(
                                        state = CoffeeState.FINISHED,
                                        finishDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddStockDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, roaster, size ->
                    scope.launch {
                        database.coffeeDao().insertStock(
                            CoffeeStock(
                                name = name,
                                roaster = roaster,
                                state = CoffeeState.NEW,
                                size = size,
                                roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                                openDate = null,
                                finishDate = null,
                            )
                        )
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, roaster: String, size: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roaster by remember { mutableStateOf("") }
    var sizeText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Add New Stock",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Coffee Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = roaster,
                    onValueChange = { roaster = it },
                    label = { Text("Roaster") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sizeText,
                    onValueChange = { sizeText = it },
                    label = { Text("Size (grams)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val size = sizeText.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && roaster.isNotBlank() && size > 0) {
                                onConfirm(name, roaster, size)
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

suspend fun insertDummyStock(database: CoffeeDatabase) {
    database.coffeeDao().insertStock(CoffeeStock(
        name = "tmp",
        roaster = "tmp",
        state = CoffeeState.NEW,
        size = 250.0,
        roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        openDate = null,
        finishDate = null,
    ))
}


@Composable
fun StockItem(
    stock: CoffeeStock,
    onOpenClick: () -> Unit = {},
    onFinishClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stock.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Roaster: ${stock.roaster}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "State: ${stock.state}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Size: ${stock.size}g", style = MaterialTheme.typography.bodySmall)
            }
            
            if (stock.state != CoffeeState.FINISHED) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (stock.state == CoffeeState.NEW) {
                        Button(onClick = onOpenClick) {
                            Text("Open")
                        }
                    }
                    if (stock.state == CoffeeState.OPEN) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = onFinishClick) {
                            Text("Finish")
                        }
                    }
                }
            }
        }
    }
}
