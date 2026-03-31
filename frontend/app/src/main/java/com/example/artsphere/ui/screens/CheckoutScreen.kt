package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<ArtworkResponse>,
    currentBalance: Double,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: (Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    val total = cartItems.sumOf { it.price ?: 0.0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kasa") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                // Oznacz każde dzieło w koszyku jako sprzedane
                                cartItems.forEach { item ->
                                    try { RetrofitClient.artworkApi.markArtworkAsSold(item.id) } catch (e: Exception) { }
                                }
                                onPaymentSuccess(total)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isProcessing && currentBalance >= total
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Kupuję i płacę (${String.format("%.2f", total)} zł)", fontSize = 18.sp)
                        }
                    }
                    if (currentBalance < total) {
                        Text("Brak wystarczających środków na koncie.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dane dostawy", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Adres z profilu", fontWeight = FontWeight.Bold)
                    Text("Kupiecka 12\n80-001 Gdańsk", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("Metoda płatności", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Portfel ArtSphere", fontWeight = FontWeight.Bold)
                        Text("Dostępne środki: ${String.format("%.2f", currentBalance)} zł")
                    }
                }
            }

            Text("Podsumowanie zamówienia", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            cartItems.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.title, modifier = Modifier.weight(1f))
                    Text("${String.format("%.2f", item.price ?: 0.0)} zł", fontWeight = FontWeight.Bold)
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Suma:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${String.format("%.2f", total)} zł", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}