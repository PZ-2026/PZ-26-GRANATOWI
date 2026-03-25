package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.AddressResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    userId: Long,
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit,
    onAddAddress: () -> Unit,
    onEditAddress: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var addresses by remember { mutableStateOf<List<AddressResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<Long?>(null) }

    // Załaduj adresy
    LaunchedEffect(userId) {
        isLoading = true
        try {
            val response = if (isAdmin) {
                RetrofitClient.addressApi.getAllAddresses()
            } else {
                RetrofitClient.addressApi.getUserAddresses(userId)
            }
            
            if (response.isSuccessful && response.body() != null) {
                addresses = response.body()!!
            } else {
                errorMessage = "Nie udało się pobrać adresów"
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdmin) "Wszystkie adresy" else "Moje adresy") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAddress) {
                Icon(Icons.Default.Add, "Dodaj adres")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            } else if (addresses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Brak adresów", fontSize = 18.sp)
                        if (!isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Dodaj swój pierwszy adres", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { address ->
                        AddressCard(
                            address = address,
                            isAdmin = isAdmin,
                            onEdit = { onEditAddress(address.id) },
                            onDelete = {
                                addressToDelete = address.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Dialog potwierdzenia usunięcia
        if (showDeleteDialog && addressToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Usuń adres") },
                text = { Text("Czy na pewno chcesz usunąć ten adres?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val response = if (isAdmin) {
                                        RetrofitClient.addressApi.adminDeleteAddress(addressToDelete!!)
                                    } else {
                                        RetrofitClient.addressApi.deleteAddress(addressToDelete!!, userId)
                                    }
                                    if (response.isSuccessful) {
                                        addresses = addresses.filter { it.id != addressToDelete }
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Nie udało się usunąć adresu"
                                }
                                showDeleteDialog = false
                                addressToDelete = null
                            }
                        }
                    ) {
                        Text("Usuń", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}

@Composable
fun AddressCard(
    address: AddressResponse,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${address.street} ${address.houseNumber}${if (address.apartmentNumber != null) "/${address.apartmentNumber}" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${address.postalCode} ${address.city}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Użytkownik: ${address.username} (ID: ${address.userId})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edytuj", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Usuń", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
