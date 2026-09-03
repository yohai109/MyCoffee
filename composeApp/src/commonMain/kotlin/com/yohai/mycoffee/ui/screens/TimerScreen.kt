package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewMethod
import kotlinx.coroutines.delay
import kotlin.time.Clock

private fun timerPreset(method: BrewMethod) = when (method) {
    BrewMethod.ESPRESSO -> 30
    BrewMethod.POUR_OVER -> 210
    BrewMethod.FRENCH_PRESS -> 240
    BrewMethod.AEROPRESS -> 120
    BrewMethod.MOKA_POT -> 300
    BrewMethod.COLD_BREW -> 43200
    BrewMethod.DRIP -> 300
    BrewMethod.OTHER -> 180
}

fun timerDurationMillis(text: String): Long = text.toLongOrNull()?.takeIf { it > 0 }?.times(1000) ?: 0

@Composable
fun TimerScreen() {
    var method by remember { mutableStateOf(BrewMethod.ESPRESSO) }
    var duration by remember { mutableStateOf(timerPreset(method).toString()) }
    var remaining by remember { mutableLongStateOf(timerDurationMillis(duration)) }
    var startedAt by remember { mutableStateOf<Long?>(null) }
    var completed by remember { mutableStateOf(false) }
    val running = startedAt != null

    LaunchedEffect(startedAt) {
        while (startedAt != null) {
            val start = startedAt ?: break
            remaining = (timerDurationMillis(duration) - (Clock.System.now().toEpochMilliseconds() - start)).coerceAtLeast(0)
            if (remaining == 0L) { startedAt = null; completed = true } else delay(100)
        }
    }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
        Text("Brew timer", style = MaterialTheme.typography.headlineMedium)
        Text(formatBrewTime((remaining / 1000).toInt()), style = MaterialTheme.typography.displayLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(BrewMethod.ESPRESSO, BrewMethod.POUR_OVER, BrewMethod.FRENCH_PRESS, BrewMethod.AEROPRESS).forEach { preset ->
                Button(onClick = { method = preset; duration = timerPreset(preset).toString(); remaining = timerPreset(preset) * 1000L; completed = false }, enabled = !running) { Text(formatBrewMethod(preset)) }
            }
        }
        OutlinedTextField(duration, {
            duration = it
            if (!running) remaining = timerDurationMillis(it)
        }, label = { Text("Seconds") }, enabled = !running, isError = duration.isNotBlank() && timerDurationMillis(duration) == 0L,
            supportingText = if (duration.isNotBlank() && timerDurationMillis(duration) == 0L) {{ Text("Enter a positive number") }} else null)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { completed = false; startedAt = if (running) null else Clock.System.now().toEpochMilliseconds() }, enabled = if (running) true else timerDurationMillis(duration) > 0) { Text(if (running) "Stop" else "Start") }
            Button(onClick = { startedAt = null; completed = false; remaining = timerDurationMillis(duration) }) { Text("Reset") }
        }
    }
    if (completed) AlertDialog(onDismissRequest = { completed = false }, title = { Text("Brew complete") }, text = { Text("Your timer has finished.") }, confirmButton = { Button(onClick = { completed = false }) { Text("Done") } })
}
