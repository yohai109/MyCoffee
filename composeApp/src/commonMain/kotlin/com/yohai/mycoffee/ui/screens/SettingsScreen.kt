package com.yohai.mycoffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yohai.mycoffee.database.BrewMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var useGrams by remember { mutableStateOf(true) }
    var defaultBagSize by remember { mutableStateOf("340") }
    var useDarkTheme by remember { mutableStateOf<Boolean?>(null) }
    var selectedBrewMethod by remember { mutableStateOf<BrewMethod?>(null) }
    var brewMethodExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("App settings and preferences", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text("Units", style = MaterialTheme.typography.titleMedium)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                tonalElevation = 1.dp
            ) { Column(Modifier.padding(4.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = useGrams, onClick = { useGrams = true })
                    Text("Grams", modifier = Modifier.padding(start = 8.dp))
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !useGrams, onClick = { useGrams = false })
                    Text("Ounces", modifier = Modifier.padding(start = 8.dp))
                }
            } }

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

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedBrewMethod?.let(::formatBrewMethod) ?: "Choose a method",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Method") },
                    modifier = Modifier.fillMaxWidth().clickable { brewMethodExpanded = true }
                )
                DropdownMenu(
                    expanded = brewMethodExpanded,
                    onDismissRequest = { brewMethodExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BrewMethod.entries.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(formatBrewMethod(method)) },
                            onClick = {
                                selectedBrewMethod = method
                                brewMethodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { /* TODO: Save settings */ }) {
                    Text("Save")
                }
            }
        }
    }
}
