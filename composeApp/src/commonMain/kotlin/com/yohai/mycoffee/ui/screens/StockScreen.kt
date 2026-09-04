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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.yohai.mycoffee.database.BrewRecord
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.ProcessMethod
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.database.getDatabase
import com.yohai.mycoffee.freshnessFor
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import mycoffee.composeapp.generated.resources.*
import mycoffee.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Instant

private val ORIGIN_OPTIONS = listOf(
    "Ethiopia", "Colombia", "Kenya", "Brazil", "Costa Rica", "Guatemala",
    "Sumatra", "Tanzania", "Rwanda", "Honduras", "Mexico", "Panama",
    "Peru", "Java", "Yemen", "India", "Uganda", "Burundi", "Papua New Guinea", "Other",
)

private val SPECIES_OPTIONS = listOf("Arabica", "Robusta", "Liberica", "Excelsa")
private fun weightUnit(useGrams: Boolean): String = if (useGrams) "grams" else "oz"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(settings: Settings = Settings.DEFAULT) {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val stockList: List<CoffeeStock> by database.coffeeDao().getAllStock()
        .collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStock by remember { mutableStateOf<CoffeeStock?>(null) }
    var finishingStock by remember { mutableStateOf<CoffeeStock?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<CoffeeStock?>(null) }
    var finishedBagsExpanded by remember { mutableStateOf(false) }

    val activeStockList = remember(stockList) {
        stockList.filter { it.state != CoffeeState.FINISHED }.sortedBy {
            when (it.state) {
                CoffeeState.OPEN -> 0
                CoffeeState.NEW -> 1
                CoffeeState.FINISHED -> 2
            }
        }
    }

    val finishedStockList = remember(stockList) {
        stockList.filter { it.state == CoffeeState.FINISHED }.sortedByDescending { it.rating ?: 0 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            val contentPadding = PaddingValues(bottom = 96.dp)
            if (stockList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(Res.string.stock_empty_title), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(stringResource(Res.string.stock_empty_message), style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { showAddDialog = true }) { Text(stringResource(Res.string.add_coffee)) }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item { StatisticsBanner(stockList, settings = settings) }
                    if (activeStockList.any { it.state == CoffeeState.OPEN }) {
                        item {
                            Text(
                                stringResource(Res.string.currently_open),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                    items(activeStockList, key = { it.id }) { stock ->
                        StockItem(
                            stock = stock,
                            settings = settings,
                            onOpenClick = {
                                scope.launch {
                                    database.coffeeDao().updateStock(
                                        stock.copy(
                                            state = CoffeeState.OPEN,
                                            openDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                                            remainingWeight = stock.size,
                                        ),
                                    )
                                }
                            },
                            onFinishClick = { finishingStock = stock },
                            onEditClick = { editingStock = stock },
                            onDeleteClick = { showDeleteConfirm = stock },
                        )
                    }
                    if (finishedStockList.isNotEmpty()) {
                        item { FinishedBagsHeader(finishedStockList.size, finishedBagsExpanded) { finishedBagsExpanded = !finishedBagsExpanded } }
                        if (finishedBagsExpanded) {
                            items(finishedStockList, key = { it.id }) { stock ->
                                StockItem(stock, settings, onDeleteClick = { showDeleteConfirm = stock })
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_stock))
        }

        if (showAddDialog) {
            AddStockDialog(
                defaultBagSize = settings.defaultBagSize,
                useGrams = settings.useGrams,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, roaster, size, roastDate, origin, process, notes, height, species ->
                    scope.launch {
                        database.coffeeDao().insertStock(
                            CoffeeStock(
                                name = name,
                                roaster = roaster,
                                state = CoffeeState.NEW,
                                size = size,
                                roastDate = roastDate,
                                openDate = null,
                                finishDate = null,
                                origin = origin,
                                process = process,
                                tastingNotes = notes,
                                height = height,
                                species = species,
                            ),
                        )
                        showAddDialog = false
                    }
                },
            )
        }

        editingStock?.let { stock ->
            AddStockDialog(
                initialStock = stock,
                useGrams = settings.useGrams,
                onDismiss = { editingStock = null },
                onConfirm = { name, roaster, size, roastDate, origin, process, notes, height, species ->
                    scope.launch {
                        database.coffeeDao().updateStock(
                            stock.copy(
                                name = name,
                                roaster = roaster,
                                size = size,
                                roastDate = roastDate,
                                origin = origin,
                                process = process,
                                tastingNotes = notes,
                                height = height,
                                species = species,
                            ),
                        )
                        editingStock = null
                    }
                },
            )
        }

        finishingStock?.let { stock ->
            FinishStockDialog(
                stock = stock,
                onDismiss = { finishingStock = null },
                onConfirm = { rating ->
                    scope.launch {
                        database.coffeeDao().updateStock(
                            stock.copy(
                                state = CoffeeState.FINISHED,
                                finishDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                                rating = rating,
                            ),
                        )
                        finishingStock = null
                    }
                },
            )
        }

        showDeleteConfirm?.let { stock ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text(stringResource(Res.string.delete_bag)) },
                text = { Text(stringResource(Res.string.delete_bag_confirmation)) },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            database.brewDao().deleteBrewsForCoffee(stock.id)
                            database.coffeeDao().deleteStock(stock)
                            showDeleteConfirm = null
                        }
                    }) {
                        Text(stringResource(Res.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) {
                        Text(stringResource(Res.string.cancel))
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, roaster: String, size: Double, roastDate: LocalDate, origin: String?, process: ProcessMethod?, tastingNotes: String?, height: Int?, species: String?) -> Unit,
    initialStock: CoffeeStock? = null,
    defaultBagSize: Double = Settings.DEFAULT.defaultBagSize,
    useGrams: Boolean = Settings.DEFAULT.useGrams,
) {
    val isEditing = initialStock != null
    var name by remember { mutableStateOf(initialStock?.name ?: "") }
    var roaster by remember { mutableStateOf(initialStock?.roaster ?: "") }
    var sizeText by remember { mutableStateOf(initialStock?.size?.let { formatMeasurement(it, useGrams) } ?: formatMeasurement(defaultBagSize, useGrams)) }
    var selectedDate by remember { mutableStateOf(initialStock?.roastDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var origin by remember { mutableStateOf(initialStock?.origin ?: "") }
    var originExpanded by remember { mutableStateOf(false) }
    var process by remember { mutableStateOf(initialStock?.process) }
    var tastingNotes by remember { mutableStateOf(initialStock?.tastingNotes ?: "") }
    var heightText by remember { mutableStateOf(initialStock?.height?.toString() ?: "") }
    var species by remember { mutableStateOf(initialStock?.species ?: "") }
    var speciesExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDayIn(TimeZone.UTC)
                ?.toEpochMilliseconds()
                ?: Clock.System.now().toEpochMilliseconds(),
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
                    Text(stringResource(Res.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(if (isEditing) Res.string.edit_stock else Res.string.add_new_stock),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.coffee_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = roaster,
                    onValueChange = { roaster = it },
                    label = { Text(stringResource(Res.string.roaster)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sizeText,
                    onValueChange = { sizeText = it },
                    label = { Text(stringResource(if (useGrams) Res.string.size_grams else Res.string.size_ounces)) },
                    isError = measurementError(sizeText, "Size", 0.1, if (useGrams) 10000.0 else gramsToOunces(10000.0)) != null,
                    supportingText = measurementError(sizeText, "Size", 0.1, if (useGrams) 10000.0 else gramsToOunces(10000.0))?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDate?.toString() ?: "",
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.roast_date)) },
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(Res.string.select_date))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val filteredOrigins =
                    ORIGIN_OPTIONS.filter { it.contains(origin, ignoreCase = true) }
                ExposedDropdownMenuBox(
                    expanded = originExpanded && filteredOrigins.isNotEmpty(),
                    onExpandedChange = { originExpanded = it },
                ) {
                    OutlinedTextField(
                        value = origin,
                        onValueChange = {
                            origin = it
                            originExpanded = true
                        },
                        label = { Text(stringResource(Res.string.origin)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = originExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = originExpanded && filteredOrigins.isNotEmpty(),
                        onDismissRequest = { originExpanded = false },
                    ) {
                        filteredOrigins.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    origin = option
                                    originExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                var processExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = process?.name?.replace("_", " ") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.process)) },
                        trailingIcon = {
                            Icon(
                                if (processExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = stringResource(Res.string.select_process),
                                modifier = Modifier.clickable { processExpanded = !processExpanded },
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenu(
                        expanded = processExpanded,
                        onDismissRequest = { processExpanded = false },
                    ) {
                        ProcessMethod.entries.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.name.replace("_", " ")) },
                                onClick = {
                                    process = method
                                    processExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text(stringResource(Res.string.height_masl)) },
                    isError = integerError(heightText, "Height", 0, 10000, required = false) != null,
                    supportingText = integerError(heightText, "Height", 0, 10000, required = false)?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                val filteredSpecies =
                    SPECIES_OPTIONS.filter { it.contains(species, ignoreCase = true) }
                ExposedDropdownMenuBox(
                    expanded = speciesExpanded && filteredSpecies.isNotEmpty(),
                    onExpandedChange = { speciesExpanded = it },
                ) {
                    OutlinedTextField(
                        value = species,
                        onValueChange = {
                            species = it
                            speciesExpanded = true
                        },
                        label = { Text(stringResource(Res.string.species)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = speciesExpanded && filteredSpecies.isNotEmpty(),
                        onDismissRequest = { speciesExpanded = false },
                    ) {
                        filteredSpecies.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    species = option
                                    speciesExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tastingNotes,
                    onValueChange = { tastingNotes = it },
                    label = { Text(stringResource(Res.string.tasting_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val size = sizeText.toDoubleOrNull() ?: 0.0
                    val sizeError = measurementError(sizeText, "Size", 0.1, if (useGrams) 10000.0 else gramsToOunces(10000.0))
                    val heightError = integerError(heightText, "Height", 0, 10000, required = false)
                    val isValid = name.isNotBlank() &&
                        roaster.isNotBlank() &&
                        sizeError == null &&
                        heightError == null &&
                        selectedDate != null
                    TextButton(
                        onClick = {
                            onConfirm(
                                name, roaster, if (useGrams) size else ouncesToGrams(size), selectedDate!!,
                                origin.ifBlank { null },
                                process,
                                tastingNotes.ifBlank { null },
                                heightText.toIntOrNull(),
                                species.ifBlank { null },
                            )
                        },
                        enabled = isValid,
                    ) {
                        Text(stringResource(if (isEditing) Res.string.save else Res.string.add))
                    }
                }
            }
        }
    }
}

suspend fun insertDummyStock(database: CoffeeDatabase) {
    database.coffeeDao().insertStock(
        CoffeeStock(
            name = "tmp",
            roaster = "tmp",
            state = CoffeeState.NEW,
            size = 250.0,
            roastDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            openDate = null,
            finishDate = null,
        ),
    )
}

fun calculateAverageOpenTime(stockList: List<CoffeeStock>): Double? {
    val finishedBags = stockList.filter { stock ->
        stock.openDate != null && stock.finishDate != null
    }

    if (finishedBags.isEmpty()) {
        return null
    }

    val totalDays = finishedBags.sumOf { stock ->
        val openDate = stock.openDate!!
        val finishDate = stock.finishDate!!
        (finishDate.toEpochDays() - openDate.toEpochDays()).toDouble()
    }

    return totalDays / finishedBags.size
}

fun calculateAverageRating(stockList: List<CoffeeStock>): Double? {
    val ratedBags = stockList.filter { it.state == CoffeeState.FINISHED && it.rating != null }

    if (ratedBags.isEmpty()) {
        return null
    }

    return ratedBags.sumOf { it.rating!! }.toDouble() / ratedBags.size
}

@Composable
fun RatingSelector(
    rating: Int?,
    onRatingChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    imageVector = if (i <= (
                            rating
                                ?: 0
                            )
                    ) {
                        Icons.Filled.Star
                    } else {
                        Icons.Filled.StarBorder
                    },
                    contentDescription = stringResource(Res.string.star_number, i),
                    tint = if (i <= (
                            rating
                                ?: 0
                            )
                    ) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
fun StarRating(rating: Int?) {
    if (rating != null) {
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = stringResource(Res.string.star_number, i),
                    tint = if (i <= rating) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f,
                        )
                    },
                    modifier = Modifier.height(16.dp),
                )
            }
        }
    }
}

@Composable
fun StatisticsBanner(
    stockList: List<CoffeeStock>,
    brewCount: Int = 0,
    avgDose: Int = 0,
    settings: Settings = Settings.DEFAULT,
) {
    val averageOpenTime = calculateAverageOpenTime(stockList)
    val averageRating = calculateAverageRating(stockList)

    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(Res.string.statistics),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(stringResource(Res.string.shelf_summary), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ShelfStat("${stockList.count { it.state != CoffeeState.FINISHED }}", "active")
                ShelfStat("${stockList.count { it.state == CoffeeState.OPEN }}", stringResource(Res.string.open))
                ShelfStat("${stockList.count { it.state == CoffeeState.NEW }}", stringResource(Res.string.unopened))
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (averageOpenTime != null) {
                val roundedDays = averageOpenTime.roundToInt()
                val daysText = stringResource(if (roundedDays == 1) Res.string.day else Res.string.days)
                Text(
                    text = stringResource(Res.string.average_open_time, roundedDays, daysText),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(
                    text = stringResource(Res.string.no_finished_bags),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (averageRating != null) {
                val roundedRating = averageRating.roundToInt()
                Text(
                    text = stringResource(Res.string.average_rating, roundedRating),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(
                    text = stringResource(Res.string.no_rated_bags),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ShelfStat(value: String, label: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishedBagsHeader(
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onToggle,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.finished_bags, count),
                style = MaterialTheme.typography.titleMedium,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = stringResource(if (expanded) Res.string.collapse else Res.string.expand),
            )
        }
    }
}

fun calculateBrewStats(brewList: List<BrewRecord>): Pair<Int, Int>? {
    if (brewList.isEmpty()) return null
    val totalBrews = brewList.size
    val avgDose = brewList.map { it.dose }.average().roundToInt()
    return Pair(totalBrews, avgDose)
}

@Composable
fun StockItem(
    stock: CoffeeStock,
    settings: Settings = Settings.DEFAULT,
    onOpenClick: () -> Unit = {},
    onFinishClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (stock.state == CoffeeState.OPEN) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (stock.state == CoffeeState.OPEN) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            },
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    StatusDot(stock.state)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(text = stock.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = stringResource(Res.string.roaster_value, stock.roaster), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row {
                    if (stock.state != CoffeeState.FINISHED) {
                        IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.edit)) }
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.delete))
                    }
                }
            }
            Text(stringResource(Res.string.freshness_value, freshnessFor(stock.roastDate, Clock.System.todayIn(TimeZone.currentSystemDefault())).name.replace('_', ' ')), style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = stringResource(Res.string.state_value, stock.state), style = MaterialTheme.typography.bodySmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val remaining = stock.remainingWeight ?: stock.size
                    if (remaining < 50) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = stringResource(Res.string.low_stock),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    }
                    Text(
                        text = if (settings.useGrams) {
                            "${remaining.toInt()}g"
                        } else {
                            "${formatDisplayMeasurement(remaining, useGrams = false)}oz"
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (stock.origin != null || stock.process != null || stock.tastingNotes != null || stock.height != null || stock.species != null) {
                Spacer(modifier = Modifier.height(8.dp))
                stock.origin?.let {
                    Text(
                        text = stringResource(Res.string.origin_value, it),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                stock.species?.let {
                    Text(
                        text = stringResource(Res.string.species_value, it),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                stock.height?.let {
                    Text(
                        text = stringResource(Res.string.height_value, it),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                stock.process?.let {
                    Text(
                        text = stringResource(Res.string.process_value, it.name.replace("_", " ")),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                stock.tastingNotes?.let {
                    Text(
                        text = stringResource(Res.string.notes_value, it),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (stock.state == CoffeeState.FINISHED && stock.rating != null) {
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(stock.rating)
            }

            if (stock.state != CoffeeState.FINISHED) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (stock.state == CoffeeState.NEW) {
                        TextButton(onClick = onOpenClick) {
                            Text(stringResource(Res.string.open))
                        }
                    } else if (stock.state == CoffeeState.OPEN) {
                        TextButton(onClick = onFinishClick) {
                            Text(stringResource(Res.string.finish))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusDot(state: CoffeeState) {
    val color = when (state) {
        CoffeeState.OPEN -> MaterialTheme.colorScheme.primary
        CoffeeState.NEW -> MaterialTheme.colorScheme.tertiary
        CoffeeState.FINISHED -> MaterialTheme.colorScheme.outline
    }
    Surface(
        shape = androidx.compose.foundation.shape.CircleShape,
        color = color,
        modifier = Modifier.width(8.dp).height(8.dp),
    ) {}
}

@Composable
fun FinishStockDialog(
    stock: CoffeeStock,
    onDismiss: () -> Unit,
    onConfirm: (rating: Int?) -> Unit,
) {
    var rating by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(Res.string.finish_stock_title, stock.name),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Text(
                    text = stringResource(Res.string.rate_coffee),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                RatingSelector(
                    rating = rating,
                    onRatingChanged = { rating = it },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onConfirm(rating) },
                    ) {
                        Text(stringResource(Res.string.finish))
                    }
                }
            }
        }
    }
}
