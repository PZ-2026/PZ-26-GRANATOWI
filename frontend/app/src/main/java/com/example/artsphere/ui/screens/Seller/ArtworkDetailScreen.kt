package com.example.artsphere.ui.screens.Seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkDetailScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Szczegóły dzieła") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dane ze screena[cite: 11]
            Text(text = "Zachód słońca nad morzem", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Artysta: Jakub Artystowski", fontSize = 16.sp)
            Text(text = "Cena: 1500.00", fontWeight = FontWeight.Bold)
            Text(text = "Kategoria: Malarstwo")
            Text(text = "Wymiary: 60 x 40 x 3 cm")
            Text(text = "Opis: Piękny obraz przedstawiający zachód słońca nad spokojnym morzem. Wykonany techniką olejną na płótnie.")

            // Miejsce na zdjęcie ze screena[cite: 11]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { /* Edytuj */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), modifier = Modifier.weight(1f)) {
                    Text("Edytuj")
                }
                Button(onClick = { /* Usuń */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), modifier = Modifier.weight(1f)) {
                    Text("Usuń")
                }
                Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)), modifier = Modifier.weight(1f)) {
                    Text("Powrót")
                }
            }
        }
    }
}