package com.example.artsphere.ui.screens.Seller

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.LocalShipping
import com.example.artsphere.api.RetrofitClient

// Model danych dla sprzedaży
data class Sale(
    val id: String,
    val artworkTitle: String,
    val buyer: String,
    val price: String,
    val date: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(sellerId: Long, onNavigateBack: () -> Unit) {
    var sales by remember { mutableStateOf<List<Sale>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sellerId) {
        try {
            val response = RetrofitClient.authApi.getSellerSalesHistory(sellerId)
            if (response.isSuccessful) {
                sales = response.body() ?: emptyList()
            } else {
                errorMessage = "Błąd pobierania: ${response.code()}"
                Log.e("SalesHistory", "Błąd API: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
            Log.e("SalesHistory", "Wyjątek API", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historia sprzedaży") },
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Twoje zrealizowane zamówienia", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                sales.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nie masz jeszcze żadnych sprzedaży.", color = MaterialTheme.colorScheme.secondary)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sales) { sale ->
                            SaleCard(sale)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaleCard(sale: Sale) {
    val statusColor = when (sale.status) {
        "Zakończono" -> Color(0xFF2E7D32) // Zielony
        "Wysłano" -> Color(0xFF1976D2)    // Niebieski
        else -> Color(0xFFF57C00)         // Pomarańczowy
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = sale.id, style = MaterialTheme.typography.labelMedium)
                Text(text = sale.date, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = sale.artworkTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Kupujący: ${sale.buyer}", style = MaterialTheme.typography.bodyMedium)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sale.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = sale.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

//            if (sale.status == "Opłacono") {
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = { /* Logika nadania przesyłki */ },
//                    modifier = Modifier.fillMaxWidth(),
//                    contentPadding = PaddingValues(0.dp)
//                ) {
//                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Nadaj przesyłkę")
//                }
//            }
        }
    }
}