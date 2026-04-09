package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RegisterRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: Long, // ID pobierane z nawigacji
    role: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val (headerGradient, topBarColor) = when (role) {
        "admin" -> Pair(Brush.horizontalGradient(colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))), Color.Transparent)
        "seller", "artist" -> Pair(Brush.horizontalGradient(colors = listOf(Color(0xFF2E8B57), Color(0xFF3CB371))), Color(0xFF2E8B57))
        else -> Pair(Brush.horizontalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)), MaterialTheme.colorScheme.primary)
    }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Potwierdzenia
    var showSuccessCard by remember { mutableStateOf(false) }
    var showErrorCard by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId > 0L) {
            try {
                val response = RetrofitClient.authApi.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    username = user.username
                    email = user.email
                    firstName = user.firstName ?: ""
                    lastName = user.lastName ?: ""
                } else {
                    showErrorCard = "Nie udało się pobrać danych."
                }
            } catch (e: Exception) {
                showErrorCard = "Błąd łączenia z serwerem."
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edycja profilu", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor),
                modifier = if (role == "admin") Modifier.background(headerGradient) else Modifier
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (role != "admin") {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(headerGradient, shape = MaterialTheme.shapes.medium).padding(16.dp), contentAlignment = Alignment.CenterStart) {
                        Text("Zmień swoje dane", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Wiadomość o Sukcesie
                if (showSuccessCard) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Zmiany zostały poprawnie zapisane!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Wiadomość Błędu z Bazy
                if (showErrorCard != null) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(showErrorCard!!, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(value = username, onValueChange = { username = it; showErrorCard = null }, label = { Text("Nazwa użytkownika") }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it; showErrorCard = null }, label = { Text("Adres E-mail") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
                OutlinedTextField(value = firstName, onValueChange = { firstName = it; showErrorCard = null }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
                OutlinedTextField(value = lastName, onValueChange = { lastName = it; showErrorCard = null }, label = { Text("Nazwisko") }, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))

                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Zmiana hasła (Zostaw puste by zachować obecne)", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it; showErrorCard = null }, label = { Text("Nowe hasło") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp))

                Button(
                    onClick = {
                        isSaving = true
                        showSuccessCard = false
                        showErrorCard = null

                        coroutineScope.launch {
                            val request = RegisterRequest(username, email, newPassword, firstName, lastName, role)
                            try {
                                val response = RetrofitClient.authApi.updateUserProfile(userId, request)
                                if (response.isSuccessful) {
                                    showSuccessCard = true
                                    delay(1000)
                                    val fullName = if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
                                        "$firstName $lastName".trim()
                                    }
                                        else {
                                            username
                                    }
                                    onSaveSuccess(fullName)
                                } else {
                                    showErrorCard = "Ten e-mail lub nazwa są już zajęte."
                                }
                            } catch (e: Exception) {
                                showErrorCard = "Błąd połączenia: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zapisz zmiany")
                    }
                }
            }
        }
    }
}