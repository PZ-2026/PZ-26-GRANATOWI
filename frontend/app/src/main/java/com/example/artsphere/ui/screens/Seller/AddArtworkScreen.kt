package com.example.artsphere.ui.screens.Seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArtworkScreen(onNavigateBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isPriceless by remember { mutableStateOf(false) }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj nowe dzieło") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Podstawowe informacje", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("Artysta") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { if (!isPriceless) price = it },
                    label = { Text("Cena (zł)") },
                    enabled = !isPriceless,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(checked = isPriceless, onCheckedChange = { isPriceless = it })
                Text("Bezcenne", fontSize = 14.sp)
            }

            Text("Wymiary (cm)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = { Text("Szer.") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Wys.") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = depth,
                    onValueChange = { depth = it },
                    label = { Text("Głęb.") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategoria") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            Button(
                onClick = { /* Wybierz plik */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wybierz zdjęcie dzieła")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* Zapisz */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zapisz")
                }

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("Anuluj")
                }
            }
        }
    }
}