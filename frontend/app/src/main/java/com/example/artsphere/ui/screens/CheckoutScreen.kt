package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.artsphere.api.AddressResponse
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.CreateOrderRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: Long,
    cartItems: List<ArtworkResponse>,
    currentBalance: Double,
    onNavigateBack: () -> Unit,
    onNavigateToAddAddress: () -> Unit,
    onPaymentSuccess: (Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }
    val total = cartItems.sumOf { it.price ?: 0.0 }

    var userAddresses by remember { mutableStateOf<List<AddressResponse>>(emptyList()) }
    var selectedAddress by remember { mutableStateOf<AddressResponse?>(null) }
    var isFetchingAddress by remember { mutableStateOf(true) }
    var showAddressDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableStateOf(0) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) refreshTrigger++ }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(userId, refreshTrigger) {
        if (userId > 0L) {
            isFetchingAddress = true
            try {
                val response = RetrofitClient.addressApi.getUserAddresses(userId)
                if (response.isSuccessful && response.body() != null) {
                    userAddresses = response.body()!!
                    if (selectedAddress == null || !userAddresses.any { it.id == selectedAddress?.id }) {
                        selectedAddress = userAddresses.firstOrNull()
                    }
                }
            } catch (e: Exception) { } finally { isFetchingAddress = false }
        } else isFetchingAddress = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kasa", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }) },
        bottomBar = {
            Surface(shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                try {
                                    // POBIERANIE ŚRODKÓW BEZPOŚREDNIO Z BAZY PORTFELA
                                    val deductResponse = RetrofitClient.authApi.deductBalance(userId, total)

                                    if (deductResponse.isSuccessful) {
                                        // 1. Zmieniamy status dzieł na Sprzedane
                                        cartItems.forEach { item -> try { RetrofitClient.artworkApi.markArtworkAsSold(item.id) } catch (e: Exception) { } }

                                        // 2. ZAPISUJEMY ZAMÓWIENIE
                                        try {
                                            val orderRequest = CreateOrderRequest(userId, total, cartItems.map { it.id })
                                            RetrofitClient.authApi.createOrder(orderRequest)
                                        } catch (e: Exception) {
                                        }

                                        Toast.makeText(context, "Zakup udany! Zlecono wysyłkę.", Toast.LENGTH_LONG).show()
                                        onPaymentSuccess(total)
                                    } else {
                                        Toast.makeText(context, "Transakcja odrzucona przez serwer banku.", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd z API.", Toast.LENGTH_SHORT).show()
                                } finally { isProcessing = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isProcessing && currentBalance >= total && selectedAddress != null
                    ) {
                        if (isProcessing) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        else Text("Kupuję i płacę (${String.format("%.2f", total)} zł)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    if (currentBalance < total) Text("Brak wystarczających środków na koncie.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
                    else if (selectedAddress == null && !isFetchingAddress) Text("⚠️ Dodaj i wybierz adres do wysyłki!", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dostawa na adres", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (userAddresses.isNotEmpty()) TextButton(onClick = { showAddressDialog = true }) { Text("Zmień adres") }
            }
            if (isFetchingAddress) Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else if (selectedAddress == null) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Książka adresowa jest pusta.", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Medium)
                    }
                }
                Button(onClick = onNavigateToAddAddress, modifier = Modifier.fillMaxWidth()) { Text("Dodaj adres doręczenia") }
            } else {
                Card(modifier = Modifier.fillMaxWidth().clickable { showAddressDialog = true }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Wybrany adres wysyłkowy", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("${selectedAddress!!.street ?: ""} ${selectedAddress!!.houseNumber ?: ""}${if(!selectedAddress!!.apartmentNumber.isNullOrEmpty()) "/${selectedAddress!!.apartmentNumber}" else ""}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("${selectedAddress!!.postalCode ?: ""} ${selectedAddress!!.city ?: ""}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Metoda płatności", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = MaterialTheme.colorScheme.secondary); Spacer(modifier = Modifier.width(16.dp))
                    Column { Text("Portfel ArtSphere", fontWeight = FontWeight.Bold); Text("Dostępne środki: ${String.format("%.2f", currentBalance)} zł") }
                }
            }
        }
    }

    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false }, title = { Text("Wybierz z książki adresowej", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userAddresses) { addr ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedAddress = addr; showAddressDialog = false },
                            colors = CardDefaults.cardColors(containerColor = if (selectedAddress?.id == addr.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${addr.street ?: ""} ${addr.houseNumber ?: ""}${if(!addr.apartmentNumber.isNullOrEmpty()) "/${addr.apartmentNumber}" else ""}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${addr.postalCode ?: ""} ${addr.city ?: ""}", fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAddressDialog = false; onNavigateToAddAddress() }) { Text("Dodaj nowy") } },
            dismissButton = { TextButton(onClick = { showAddressDialog = false }) { Text("Anuluj") } }
        )
    }
}