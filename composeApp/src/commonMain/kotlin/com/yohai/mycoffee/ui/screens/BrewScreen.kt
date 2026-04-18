package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.yohai.mycoffee.database.BrewDao
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.math.roundToInt
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@Composable
fun BrewAnalyticsCard(brewList: List<BrewRecord>) {
    if (brewList.isEmpty()) return

    val totalBrews = brewList.size
    val avgDose = brewList.map { it.dose }.average().roundToInt()
    val methodCounts = brewList.groupBy { it.method }.mapValues { it.value.size }
    val topMethod = methodCounts.maxByOrNull { it.value }?.key

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Brew Analytics", style = MaterialTheme.typography.titleMedium)
            Text("Total brews: $totalBrews", style = MaterialTheme.typography.bodyMedium)
            Text("Average dose: ${avgDose}g", style = MaterialTheme.typography.bodyMedium)
            topMethod?.let {
                Text("Favorite method: ${it.name.replace("_", " ")}", style = MaterialTheme.typography.bodyMedium)
            }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val brewList: List<BrewRecord> by database.brewDao().getAllBrews().collectAsState(initial = emptyList())
    val coffeeStock: List<CoffeeStock> by database.coffeeDao().getAllStock().collectAsState(initial = emptyList())
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBrew by remember { mutableStateOf<BrewRecord?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<BrewRecord?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Brew")
            }
        }
    ) { padding ->
        if (brewList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No brews recorded yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { BrewAnalyticsCard(brewList) }
                items(brewList) { brew ->
                    val coffee = coffeeStock.find { it.id == brew.coffeeStockId }
                    BrewItem(
                        brew = brew,
                        coffeeName = coffee?.name ?: "Unknown Coffee",
                        onEditClick = { editingBrew = brew },
                        onDeleteClick = { showDeleteConfirm = brew }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddBrewDialog(
                coffeeStock = coffeeStock,
                onDismiss = { showAddDialog = false },
                onConfirm = { coffeeStockId, date, method, dose, brewTime, yield, notes ->
                    scope.launch {
                        database.brewDao().insertBrew(
                            BrewRecord(
                                coffeeStockId = coffeeStockId,
                                date = date,
                                method = method,
                                dose = dose,
                                brewTime = brewTime,
                                yield = yield,
                                notes = notes
                            )
                        )
                        val stock = coffeeStock.find { it.id == coffeeStockId }
                        stock?.let {
                            val currentRemaining = it.remainingWeight ?: it.size
                            val newRemaining = (currentRemaining - dose).coerceAtLeast(0.0)
                            database.coffeeDao().updateStock(
                                it.copy(remainingWeight = newRemaining)
                            )
                        }
                        showAddDialog = false
                    }
                }
            )
        }

        editingBrew?.let { brew ->
            val coffee = coffeeStock.find { it.id == brew.coffeeStockId }
            AddBrewDialog(
                initialBrew = brew,
                coffeeStock = coffeeStock,
                selectedCoffeeName = coffee?.name,
                onDismiss = { editingBrew = null },
                onConfirm = { coffeeStockId, date, method, dose, brewTime, yield, notes ->
                    scope.launch {
                        database.brewDao().updateBrew(
                            brew.copy(
                                coffeeStockId = coffeeStockId,
                                date = date,
                                method = method,
                                dose = dose,
                                brewTime = brewTime,
                                yield = yield,
                                notes = notes
                            )
                        )
                        val stock = coffeeStock.find { it.id == coffeeStockId }
                        stock?.let {
                            val currentRemaining = it.remainingWeight ?: it.size
                            val newRemaining = (currentRemaining - dose).coerceAtLeast(0.0)
                            database.coffeeDao().updateStock(
                                it.copy(remainingWeight = newRemaining)
                            )
                        }
                        editingBrew = null
                    }
                }
            )
        }

        showDeleteConfirm?.let { brew ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("Delete Brew") },
                text = { Text("Are you sure you want to delete this brew record?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            database.brewDao().deleteBrew(brew)
                            showDeleteConfirm = null
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun BrewItem(
    brew: BrewRecord,
    coffeeName: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Coffee, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = coffeeName, style = MaterialTheme.typography.titleMedium)
                }
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Method", style = MaterialTheme.typography.labelSmall)
                    Text(formatBrewMethod(brew.method), style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Dose", style = MaterialTheme.typography.labelSmall)
                    Text("${brew.dose.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Time", style = MaterialTheme.typography.labelSmall)
                    Text(formatBrewTime(brew.brewTime), style = MaterialTheme.typography.bodyMedium)
                }
                if (brew.yield != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Yield", style = MaterialTheme.typography.labelSmall)
                        Text("${brew.yield?.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Date: ${brew.date}", style = MaterialTheme.typography.bodySmall)
            if (!brew.notes.isNullOrBlank()) {
                Text("Notes: ${brew.notes}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBrewDialog(
    coffeeStock: List<CoffeeStock>,
    onDismiss: () -> Unit,
    onConfirm: (coffeeStockId: Long, date: LocalDate, method: BrewMethod, dose: Double, brewTime: Int, yield: Double?, notes: String?) -> Unit,
    initialBrew: BrewRecord? = null,
    selectedCoffeeName: String? = null
) {
    val isEditing = initialBrew != null
    var selectedCoffee by remember { mutableStateOf(initialBrew?.coffeeStockId) }
    var selectedDate by remember { mutableStateOf(initialBrew?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var selectedMethod by remember { mutableStateOf(initialBrew?.method ?: BrewMethod.ESPRESSO) }
    var doseText by remember { mutableStateOf(initialBrew?.dose?.toString() ?: "") }
    var brewTimeMinutes by remember { mutableStateOf(initialBrew?.brewTime?.div(60) ?: 0) }
    var brewTimeSeconds by remember { mutableStateOf(initialBrew?.brewTime?.rem(60) ?: 0) }
    var yieldText by remember { mutableStateOf(initialBrew?.yield?.toString() ?: "") }
    var notes by remember { mutableStateOf(initialBrew?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var methodExpanded by remember { mutableStateOf(false) }
    var coffeeExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isEditing) "Edit Brew" else "Add Brew",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (coffeeStock.isEmpty()) {
                    Text("No coffee in stock. Add coffee first.", color = MaterialTheme.colorScheme.error)
                } else {
                    ExposedDropdownMenuBox(
                        expanded = coffeeExpanded,
                        onExpandedChange = { coffeeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = coffeeStock.find { it.id == selectedCoffee }?.name ?: selectedCoffeeName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Coffee") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = coffeeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = coffeeExpanded,
                            onDismissRequest = { coffeeExpanded = false }
                        ) {
                            coffeeStock.forEach { coffee ->
                                DropdownMenuItem(
                                    text = { Text(coffee.name) },
                                    onClick = {
                                        selectedCoffee = coffee.id
                                        coffeeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDate.toString(),
                        onValueChange = {},
                        label = { Text("Date") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Select date")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = methodExpanded,
                    onExpandedChange = { methodExpanded = it }
                ) {
                    OutlinedTextField(
                        value = formatBrewMethod(selectedMethod),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Brew Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = methodExpanded,
                        onDismissRequest = { methodExpanded = false }
                    ) {
                        BrewMethod.entries.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(formatBrewMethod(method)) },
                                onClick = {
                                    selectedMethod = method
                                    methodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = doseText,
                        onValueChange = { doseText = it },
                        label = { Text("Dose (g)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = yieldText,
                        onValueChange = { yieldText = it },
                        label = { Text("Yield (g)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (brewTimeMinutes > 0) brewTimeMinutes.toString() else "",
                        onValueChange = { brewTimeMinutes = it.toIntOrNull() ?: 0 },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = if (brewTimeSeconds > 0) brewTimeSeconds.toString() else "",
                        onValueChange = { brewTimeSeconds = it.toIntOrNull() ?: 0 },
                        label = { Text("Seconds") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
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
                    val dose = doseText.toDoubleOrNull() ?: 0.0
                    val totalBrewTime = brewTimeMinutes * 60 + brewTimeSeconds
                    val isValid = selectedCoffee != null && dose > 0 && totalBrewTime > 0
                    Button(
                        onClick = {
                            val yield = yieldText.toDoubleOrNull()
                            onConfirm(selectedCoffee!!, selectedDate, selectedMethod, dose, totalBrewTime, yield, notes.ifBlank { null })
                        },
                        enabled = isValid && selectedCoffee != null
                    ) {
                        Text(if (isEditing) "Save" else "Add")
                    }
                }
            }
        }
    }
}

fun formatBrewMethod(method: BrewMethod): String {
    return method.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

fun formatBrewTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) "${minutes}m ${secs}s" else "${secs}s"
}