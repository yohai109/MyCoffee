package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.CoffeeState
import com.yohai.mycoffee.database.CoffeeStock
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val stockList: List<CoffeeStock> by database.coffeeDao().getAllStock().collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    insertDummyStock(database)
                }
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
                item {
                    StatisticsBanner(stockList)
                }
                items(stockList) { stock ->
                    StockItem(stock)
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

@Composable
fun StatisticsBanner(stockList: List<CoffeeStock>) {
    val averageOpenTime = calculateAverageOpenTime(stockList)
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium
            )
            if (averageOpenTime != null) {
                Text(
                    text = "Average open time: ${averageOpenTime.roundToInt()} days",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Average open time: No finished bags yet",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun StockItem(stock: CoffeeStock) {
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
        }
    }
}
