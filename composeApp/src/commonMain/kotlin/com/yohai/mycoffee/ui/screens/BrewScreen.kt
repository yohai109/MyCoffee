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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yohai.mycoffee.database.BrewDao
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeDao
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val brewList: List<BrewRecord> by database.brewDao().getAllBrews().collectAsState(initial = emptyList())
    val stockList: List<CoffeeStock> by database.coffeeDao().getAllStock().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBrew by remember { mutableStateOf<BrewRecord?>(null) }

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
                items(brewList) { brew ->
                    BrewItem(
                        brew = brew,
                        onEditClick = { editingBrew = brew },
                        onDeleteClick = {
                            scope.launch {
                                database.brewDao().deleteBrew(brew)
                            }
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddBrewDialog(
                coffeeStockList = stockList,
                onDismiss = { showAddDialog = false },
                onConfirm = { coffeeStockId, coffeeName, date, method, dose, yield, brewTime, notes ->
                    scope.launch {
                        database.brewDao().insertBrew(
                            BrewRecord(
                                coffeeStockId = coffeeStockId,
                                coffeeName = coffeeName,
                                date = date,
                                method = method,
                                dose = dose,
                                yield = yield,
                                brewTime = brewTime,
                                notes = notes
                            )
                        )
                        showAddDialog = false
                    }
                }
            )
        }

        editingBrew?.let { brew ->
            AddBrewDialog(
                coffeeStockList = stockList,
                initialBrew = brew,
                onDismiss = { editingBrew = null },
                onConfirm = { coffeeStockId, coffeeName, date, method, dose, yield, brewTime, notes ->
                    scope.launch {
                        database.brewDao().updateBrew(
                            brew.copy(
                                coffeeStockId = coffeeStockId,
                                coffeeName = coffeeName,
                                date = date,
                                method = method,
                                dose = dose,
                                yield = yield,
                                brewTime = brewTime,
                                notes = notes
                            )
                        )
                        editingBrew = null
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBrewDialog(
    coffeeStockList: List<CoffeeStock>,
    onDismiss: () -> Unit,
    onConfirm: (coffeeStockId: Long, coffeeName: String, date: LocalDate, method: BrewMethod, dose: Double, yield: Double, brewTime: Int, notes: String) -> Unit,
    initialBrew: BrewRecord? = null
) {
    val isEditing = initialBrew != null
    var selectedCoffee by remember {
        mutableStateOf(initialBrew?.let { coffeeStockList.find { c -> c.id == it.coffeeStockId } })
    }
    var date by remember { mutableStateOf(initialBrew?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var method by remember { mutableStateOf(initialBrew?.method ?: BrewMethod.POUR_OVER) }
    var doseText by remember { mutableStateOf(initialBrew?.dose?.toString() ?: "") }
    var yieldText by remember { mutableStateOf(initialBrew?.yield?.toString() ?: "") }
    var brewTimeText by remember { mutableStateOf(initialBrew?.brewTime?.toString() ?: "") }
    var notes by remember { mutableStateOf(initialBrew?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var methodExpanded by remember { mutableStateOf(false) }
    var coffeeExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        date = Instant.fromEpochMilliseconds(millis)
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
                    text = if (isEditing) "Edit Brew" else "Add New Brew",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = coffeeExpanded,
                    onExpandedChange = { coffeeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCoffee?.name ?: "",
                        onValueChange = {},
                        label = { Text("Coffee") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = coffeeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = coffeeExpanded,
                        onDismissRequest = { coffeeExpanded = false }
                    ) {
                        coffeeStockList.forEach { coffee ->
                            DropdownMenuItem(
                                text = { Text(coffee.name) },
                                onClick = {
                                    selectedCoffee = coffee
                                    coffeeExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = date.toString(),
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
                        value = method.displayName(),
                        onValueChange = {},
                        label = { Text("Brew Method") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = methodExpanded,
                        onDismissRequest = { methodExpanded = false }
                    ) {
                        BrewMethod.entries.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.displayName()) },
                                onClick = {
                                    method = m
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = yieldText,
                        onValueChange = { yieldText = it },
                        label = { Text("Yield (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = brewTimeText,
                    onValueChange = { brewTimeText = it },
                    label = { Text("Brew Time (seconds)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
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
                    val brewTime = brewTimeText.toIntOrNull() ?: 0
                    val isValid = selectedCoffee != null && dose > 0 && brewTime > 0
                    TextButton(
                        onClick = {
                            onConfirm(
                                selectedCoffee!!.id,
                                selectedCoffee!!.name,
                                date,
                                method,
                                dose,
                                yieldText.toDoubleOrNull() ?: 0.0,
                                brewTime,
                                notes
                            )
                        },
                        enabled = isValid
                    ) {
                        Text(if (isEditing) "Save" else "Add")
                    }
                }
            }
        }
    }
}

@Composable
fun BrewItem(
    brew: BrewRecord,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = brew.coffeeName, style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Text(text = "Method: ${brew.method.displayName()}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Dose: ${brew.dose}g", style = MaterialTheme.typography.bodySmall)
                Text(text = "Yield: ${brew.yield}g", style = MaterialTheme.typography.bodySmall)
                Text(text = "Time: ${brew.brewTime}s", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Date: ${brew.date}", style = MaterialTheme.typography.bodySmall)
            if (brew.notes.isNotBlank()) {
                Text(text = "Notes: ${brew.notes}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun BrewMethod.displayName(): String {
    return when (this) {
        BrewMethod.POUR_OVER -> "Pour Over"
        BrewMethod.ESPRESSO -> "Espresso"
        BrewMethod.FRENCH_PRESS -> "French Press"
        BrewMethod.AEROPRESS -> "AeroPress"
        BrewMethod.DRIP -> "Drip"
        BrewMethod.MOKA -> "Moka"
        BrewMethod.COLD_BREW -> "Cold Brew"
        BrewMethod.OTHER -> "Other"
    }
}
