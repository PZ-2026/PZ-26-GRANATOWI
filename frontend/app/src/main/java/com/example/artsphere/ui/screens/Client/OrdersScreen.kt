package com.example.artsphere.ui.screens.Client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.PurchaseResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(userId: Long, onNavigateBack: () -> Unit, onNavigateToArtwork: (Long) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var orders by remember { mutableStateOf<List<PurchaseResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Zmienna trzymająca zamówienie wybrane aktualnie do usunięcia
    var orderToDelete by remember { mutableStateOf<PurchaseResponse?>(null) }

    LaunchedEffect(userId) {
        try {
            val response = RetrofitClient.authApi.getUserPurchases(userId)
            if (response.isSuccessful && response.body() != null) {
                orders = response.body()!!
            }
        } catch (e: Exception) { } finally { isLoading = false }
    }

    val filteredOrders = orders.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true) ||
                it.sellerUsername.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twoje zakupy") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Szukaj dzieła, artysty...") },
                trailingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nie znaleziono żadnych zakupów.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order,
                            onNavigateToArtwork = onNavigateToArtwork,
                            onDeleteClick = { orderToDelete = order }
                        )
                    }
                }
            }
        }
    }

    // --- OKIENKO POTWIERDZAJĄCE USUNIĘCIE ---
    if (orderToDelete != null) {
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("Usuń z historii", fontWeight = FontWeight.Bold) },
            text = { Text("Czy na pewno chcesz trwale usunąć to zamówienie ze swojej historii zakupów? Tego kroku nie można cofnąć.") },
            confirmButton = {
                Button(
                    onClick = {
                        val orderIdToRemove = orderToDelete!!.orderId
                        orderToDelete = null

                        coroutineScope.launch {
                            try {
                                val response = RetrofitClient.authApi.deleteOrder(orderIdToRemove)
                                if (response.isSuccessful) {
                                    // Filtruje bieżącą listę odświeżając ją na żywo
                                    orders = orders.filter { it.orderId != orderIdToRemove }
                                    Toast.makeText(context, "Usunięto z historii", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Wystąpił błąd po stronie serwera.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Błąd z połączeniem", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Usuń trwale")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
fun OrderCard(order: PurchaseResponse, onNavigateToArtwork: (Long) -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Zamówienie #${order.orderId}", style = MaterialTheme.typography.labelMedium)
                Text(text = order.date, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = order.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Artysta: ${order.artist}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Sprzedawca: ${order.sellerUsername}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Dolny pasek z ceną i dwoma przyciskami (Szczegóły / Usuń)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${String.format("%.2f", order.price)} zł", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { onNavigateToArtwork(order.artworkId) }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Szczegóły", fontSize = 12.sp)
                    }

                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}