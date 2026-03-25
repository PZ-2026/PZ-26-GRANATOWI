package com.example.artsphere.ui.screens.Client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Prosty model danych na podstawie screena[cite: 5]
data class Order(
    val id: Int,
    val title: String,
    val artist: String,
    val seller: String,
    val price: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onNavigateBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    // Dane testowe ze screena[cite: 5]
    val orders = listOf(
        Order(1, "Portret kobiety w kapeluszu", "Jakub Artystowski", "jakub_art", "765,00 zł", "10.06.2025 22:14"),
        Order(2, "Miejski pejzaż nocą", "Jakub Artystowski", "jakub_art", "1 620,00 zł", "10.06.2025 22:14")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twoje zakupy") },
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
        ) {
            // Wyszukiwarka wzorowana na screenie[cite: 5]
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Szukaj dzieła, artysty...") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Zamówienie #${order.id}", style = MaterialTheme.typography.labelMedium)
                Text(text = order.date, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = order.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Artysta: ${order.artist}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sprzedawca: ${order.seller}", style = MaterialTheme.typography.bodySmall)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Przycisk "Szczegóły" ze screena[cite: 5]
                OutlinedButton(
                    onClick = { /* Szczegóły */ },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Szczegóły")
                }
            }
        }
    }
}