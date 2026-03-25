package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.AddressRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormScreen(
    userId: Long,
    addressId: Long? = null, // null = dodawanie, wartość = edycja
    isAdmin: Boolean = false, // czy admin edytuje
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var apartmentNumber by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isEditMode = addressId != null
    var targetUserId by remember { mutableStateOf(if (isAdmin && !isEditMode) "" else userId.toString()) }

    // Załaduj dane adresu w trybie edycji
    LaunchedEffect(addressId) {
        if (addressId != null) {
            isLoading = true
            try {
                val response = if (isAdmin) {
                    RetrofitClient.addressApi.getAddressById(addressId)
                } else {
                    RetrofitClient.addressApi.getUserAddresses(userId)
                }
                
                if (response.isSuccessful && response.body() != null) {
                    val address = if (isAdmin) {
                        response.body() as com.example.artsphere.api.AddressResponse
                    } else {
                        (response.body() as List<com.example.artsphere.api.AddressResponse>).find { it.id == addressId }
                    }
                    
                    if (address != null) {
                        city = address.city
                        postalCode = address.postalCode
                        street = address.street
                        houseNumber = address.houseNumber
                        apartmentNumber = address.apartmentNumber ?: ""
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Nie udało się załadować adresu"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edytuj adres" else "Dodaj adres") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEditMode) "Zaktualizuj dane adresu" else "Dodaj nowy adres dostawy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Pole userId dla admina (tylko dodawanie)
            if (isAdmin && !isEditMode) {
                OutlinedTextField(
                    value = targetUserId,
                    onValueChange = { targetUserId = it; errorMessage = "" },
                    label = { Text("ID użytkownika *") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Miasto
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; errorMessage = "" },
                label = { Text("Miasto *") },
                leadingIcon = { Icon(Icons.Default.LocationCity, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Kod pocztowy
            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it; errorMessage = "" },
                label = { Text("Kod pocztowy * (np. 00-000)") },
                leadingIcon = { Icon(Icons.Default.Markunread, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ulica
            OutlinedTextField(
                value = street,
                onValueChange = { street = it; errorMessage = "" },
                label = { Text("Ulica *") },
                leadingIcon = { Icon(Icons.Default.Signpost, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Numer domu
            OutlinedTextField(
                value = houseNumber,
                onValueChange = { houseNumber = it; errorMessage = "" },
                label = { Text("Numer domu *") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Numer lokalu (opcjonalny)
            OutlinedTextField(
                value = apartmentNumber,
                onValueChange = { apartmentNumber = it; errorMessage = "" },
                label = { Text("Numer lokalu (opcjonalnie)") },
                leadingIcon = { Icon(Icons.Default.Apartment, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Przycisk zapisu
            Button(
                onClick = {
                    // Walidacja
                    if (city.isBlank() || postalCode.isBlank() || street.isBlank() || houseNumber.isBlank()) {
                        errorMessage = "Wypełnij wszystkie wymagane pola"
                        return@Button
                    }

                    if (isAdmin && !isEditMode && targetUserId.isBlank()) {
                        errorMessage = "Wprowadź ID użytkownika"
                        return@Button
                    }

                    val userIdForRequest = if (isAdmin && !isEditMode) {
                        targetUserId.toLongOrNull() ?: run {
                            errorMessage = "ID użytkownika musi być liczbą"
                            return@Button
                        }
                    } else {
                        userId
                    }

                    isLoading = true
                    errorMessage = ""

                    coroutineScope.launch {
                        try {
                            val request = AddressRequest(
                                city = city,
                                postalCode = postalCode,
                                street = street,
                                houseNumber = houseNumber,
                                apartmentNumber = apartmentNumber.ifBlank { null }
                            )

                            val response = if (isEditMode) {
                                if (isAdmin) {
                                    RetrofitClient.addressApi.adminUpdateAddress(addressId!!, request)
                                } else {
                                    RetrofitClient.addressApi.updateAddress(addressId!!, userId, request)
                                }
                            } else {
                                RetrofitClient.addressApi.createAddress(userIdForRequest, request)
                            }

                            if (response.isSuccessful) {
                                onSuccess()
                            } else {
                                errorMessage = response.errorBody()?.string() ?: "Błąd zapisu"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Błąd połączenia: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditMode) "Zaktualizuj adres" else "Dodaj adres")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "* Pola wymagane",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
