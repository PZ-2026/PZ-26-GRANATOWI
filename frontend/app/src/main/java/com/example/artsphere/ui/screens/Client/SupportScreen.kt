package com.example.artsphere.ui.screens.Client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(onNavigateBack: () -> Unit) {
    var donationAmount by remember { mutableStateOf("") }
    var selectedSeller by remember { mutableStateOf("jakub_art") } // Na podstawie screena[cite: 6]
    var expanded by remember { mutableStateOf(false) }

    // Lista sprzedawców (do rozwijanego menu)
    val sellers = listOf("jakub_art", "maria_gallery", "art_studio_1")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wesprzyj sprzedawcę") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Przekaż darowiznę ulubionemu artyście",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Wybór sprzedawcy[cite: 6]
            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedSeller,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Wybierz sprzedawcę") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sellers.forEach { seller ->
                            DropdownMenuItem(
                                text = { Text(seller) },
                                onClick = {
                                    selectedSeller = seller
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Kwota darowizny[cite: 6]
            item {
                OutlinedTextField(
                    value = donationAmount,
                    onValueChange = { donationAmount = it },
                    label = { Text("Kwota darowizny (zł)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00") }
                )
            }

            // Przycisk "Wyślij darowiznę"[cite: 6]
            item {
                Button(
                    onClick = { /* Logika wysyłania */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wyślij darowiznę")
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ostatnie darowizny:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Historia darowizn ze screena[cite: 6]
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    PaddingValues(16.dp)
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Darowizna przekazana do: jakub_art – 77,00 zł (10.06.2025 22:14)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}