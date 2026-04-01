package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var addresses by remember { mutableStateOf<List<AddressResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(userId, refreshTrigger) {
        if (userId > 0L || isAdmin) {
            isLoading = true
            try {
                val response = if (isAdmin) RetrofitClient.addressApi.getAllAddresses() else RetrofitClient.addressApi.getUserAddresses(userId)
                if (response.isSuccessful && response.body() != null) {
                    addresses = response.body()!!
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Błąd z wczytywaniem z bazy.", Toast.LENGTH_LONG).show()
            } finally { isLoading = false }
        } else { isLoading = false }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (isAdmin) "Wszystkie adresy" else "Moje Adresy") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Wróć") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onAddAddress, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, "Dodaj", tint = Color.White) } }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (addresses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOff, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Brak zapisanych adresów.", color = Color.Gray, fontSize = 18.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(addresses) { address ->
                    AddressCard(
                        address = address, isAdmin = isAdmin, onEdit = { onEditAddress(address.id) },
                        onDelete = { addressToDelete = address.id; showDeleteDialog = true }
                    )
                }
            }
        }
        if (showDeleteDialog && addressToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false }, title = { Text("Usuń adres") }, text = { Text("Czy na pewno chcesz usunąć ten adres z książki?") },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val response = if (isAdmin) RetrofitClient.addressApi.adminDeleteAddress(addressToDelete!!) else RetrofitClient.addressApi.deleteAddress(addressToDelete!!, userId)
                                if (response.isSuccessful) { Toast.makeText(context, "Usunięto pomyślnie!", Toast.LENGTH_SHORT).show(); refreshTrigger++ }
                            } catch (e: Exception) { }
                            showDeleteDialog = false
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Usuń", color = Color.White) }
                }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Anuluj") } }
            )
        }
    }
}

@Composable
fun AddressCard(address: AddressResponse, isAdmin: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if(isAdmin) "Należy do: ${address.username ?: "Nieznany"}" else "Adres wysyłkowy", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("${address.street ?: "Brak"} ${address.houseNumber ?: ""}${if (!address.apartmentNumber.isNullOrEmpty()) "/${address.apartmentNumber}" else ""}", fontSize = 18.sp)
            Text("${address.postalCode ?: "Brak"} ${address.city ?: "Brak"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Edytuj") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}