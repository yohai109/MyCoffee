package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.defaultTimerSeconds
import com.yohai.mycoffee.notifyTimerComplete
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.TimerPreset
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Clock

@Composable
fun TimerScreen(database: CoffeeDatabase = remember { getDatabase() }) {
    val storedPresets by database.timerPresetDao().getAll().collectAsState(initial = emptyList())
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var method by remember { mutableStateOf(BrewMethod.ESPRESSO) }
    var duration by remember(storedPresets, method) { mutableStateOf((storedPresets.find { it.method == method }?.seconds ?: defaultTimerSeconds(method)).toString()) }
    var remaining by remember { mutableLongStateOf(duration.toLongOrNull()?.times(1000) ?: 0L) }
    var startedAt by remember { mutableStateOf<Long?>(null) }
    var completed by remember { mutableStateOf(false) }
    val running = startedAt != null

    LaunchedEffect(startedAt) {
        while (startedAt != null) {
            val start = startedAt ?: break
            remaining = ((duration.toLongOrNull()?.times(1000) ?: 0L) - (Clock.System.now().toEpochMilliseconds() - start)).coerceAtLeast(0)
            if (remaining == 0L) { startedAt = null; completed = true; notifyTimerComplete() } else delay(100)
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)) {
        Text("Brew timer", style = MaterialTheme.typography.headlineMedium)
        Text(formatBrewTime((remaining / 1000).toInt()), style = MaterialTheme.typography.displayLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(BrewMethod.ESPRESSO, BrewMethod.POUR_OVER, BrewMethod.FRENCH_PRESS, BrewMethod.AEROPRESS).forEach { presetMethod ->
                Button(onClick = { method = presetMethod; duration = (storedPresets.find { it.method == presetMethod }?.seconds ?: defaultTimerSeconds(presetMethod)).toString(); remaining = duration.toLong() * 1000L; startedAt = null; completed = false }, enabled = !running) {
                    Text(formatBrewMethod(presetMethod))
                }
            }
        }
        OutlinedTextField(value = duration, onValueChange = { value -> duration = value; value.toIntOrNull()?.let { seconds -> scope.launch { database.timerPresetDao().save(TimerPreset(method, seconds)) } } }, label = { Text("Seconds") }, enabled = !running)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { completed = false; startedAt = if (running) null else Clock.System.now().toEpochMilliseconds() }, enabled = remaining > 0 || !running) {
                Text(if (running) "Stop" else "Start")
            }
            Button(onClick = { startedAt = null; completed = false; remaining = duration.toLongOrNull()?.times(1000) ?: 0L }) { Text("Reset") }
        }
    }
    if (completed) AlertDialog(onDismissRequest = { completed = false }, title = { Text("Brew complete") }, text = { Text("Your timer has finished.") }, confirmButton = { Button(onClick = { completed = false }) { Text("Done") } })
}
