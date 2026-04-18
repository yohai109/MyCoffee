package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.yohai.mycoffee.database.BrewMethod
import com.yohai.mycoffee.database.Settings
import com.yohai.mycoffee.database.getDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val database = remember { getDatabase() }
    val scope = rememberCoroutineScope()
    val settings: Settings? by database.settingsDao().getSettings().collectAsState(initial = null)

    var useGrams by remember { mutableStateOf(true) }
    var defaultBagSize by remember { mutableStateOf("340") }
    var useDarkTheme by remember { mutableStateOf<Boolean?>(null) }
    var selectedBrewMethod by remember { mutableStateOf<BrewMethod?>(null) }

    var initialized by remember { mutableStateOf(false) }

    settings?.let { s ->
        if (!initialized) {
            useGrams = s.useGrams
            defaultBagSize = s.defaultBagSize.toString()
            useDarkTheme = s.darkTheme
            selectedBrewMethod = s.defaultBrewMethod
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Units", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = useGrams,
                    onClick = { useGrams = true }
                )
                Text("Grams", modifier = Modifier.padding(start = 8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !useGrams,
                    onClick = { useGrams = false }
                )
                Text("Ounces", modifier = Modifier.padding(start = 8.dp))
            }

            HorizontalDivider()

            Text("Default Bag Size", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = defaultBagSize,
                onValueChange = { defaultBagSize = it },
                label = { Text("Size") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text("Theme", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = useDarkTheme == true,
                    onCheckedChange = { useDarkTheme = if (it) true else null }
                )
                Text(
                    if (useDarkTheme == true) "Dark theme" else "System theme",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider()

            Text("Default Brew Method", style = MaterialTheme.typography.titleMedium)

            Column {
                BrewMethod.entries.forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedBrewMethod == method,
                            onClick = { selectedBrewMethod = method }
                        )
                        Text(
                            method.name.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        scope.launch {
                            val bagSize = defaultBagSize.toDoubleOrNull() ?: 340.0
                            val newSettings = Settings(
                                id = 1,
                                defaultBrewMethod = selectedBrewMethod,
                                defaultBagSize = bagSize,
                                useGrams = useGrams,
                                darkTheme = useDarkTheme
                            )
                            database.settingsDao().insertSettings(newSettings)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}