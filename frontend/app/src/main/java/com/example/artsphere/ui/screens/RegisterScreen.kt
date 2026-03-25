package com.example.artsphere.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RegisterRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    initialRole: String = "user",
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentStep by remember { mutableStateOf(1) }
    var selectedRole by remember { mutableStateOf(initialRole) }

    // Krok 1 zmienne
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Krok 2 zmienne
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var apartmentNumber by remember { mutableStateOf("") }
    var sellerDescription by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Utwórz konto",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage, 
                color = MaterialTheme.colorScheme.error, 
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage, 
                color = MaterialTheme.colorScheme.primary, 
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedRole == "user",
                onClick = { selectedRole = "user" },
                label = { Text("Kupujący") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = selectedRole == "seller",
                onClick = { selectedRole = "seller" },
                label = { Text("Sprzedawca") }
            )
        }

        // Pasek postępu
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Badge(containerColor = if (currentStep >= 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
                Text("1", color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(4.dp))
            }
            Text(" Dane", modifier = Modifier.padding(start = 4.dp, end = 8.dp))
            Divider(modifier = Modifier.width(40.dp))
            Badge(containerColor = if (currentStep >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
                Text("2", color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(4.dp))
            }
            Text(" Adres", modifier = Modifier.padding(start = 4.dp))
        }

        if (currentStep == 1) {
            // zakladanie konta
            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Nazwa użytkownika *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("Imię *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Nazwisko *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Adres e-mail *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Hasło *") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Potwierdź hasło *") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), singleLine = true
            )

            Button(
                onClick = { currentStep = 2 },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Dalej")
            }
        } else {
            // adres
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Miasto *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = postalCode, onValueChange = { postalCode = it },
                label = { Text("Kod pocztowy * (np. 00-000)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            OutlinedTextField(
                value = street, onValueChange = { street = it },
                label = { Text("Ulica *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(
                    value = houseNumber, onValueChange = { houseNumber = it },
                    label = { Text("Nr domu *") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true
                )
                OutlinedTextField(
                    value = apartmentNumber, onValueChange = { apartmentNumber = it },
                    label = { Text("Nr lokalu") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true
                )
            }

            if (selectedRole == "seller") {
                OutlinedTextField(
                    value = sellerDescription, onValueChange = { sellerDescription = it },
                    label = { Text("Opis sprzedawcy *") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(100.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = { currentStep = 1 }, 
                    modifier = Modifier.weight(1f).height(50.dp).padding(end = 8.dp),
                    enabled = !isLoading
                ) {
                    Text("Wstecz")
                }
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        
                        if (username.isBlank() || email.isBlank() || password.isBlank() || 
                            firstName.isBlank() || lastName.isBlank()) {
                            errorMessage = "Wypełnij wszystkie wymagane pola"
                            return@Button
                        }
                        
                        if (password != confirmPassword) {
                            errorMessage = "Hasła nie są identyczne"
                            return@Button
                        }
                        
                        isLoading = true
                        errorMessage = ""
                        successMessage = ""
                        
                        coroutineScope.launch {
                            try {
                                val roleName = if (selectedRole == "seller") "ARTIST" else "BUYER"
                                val response = RetrofitClient.authApi.register(
                                    RegisterRequest(
                                        username = username,
                                        email = email,
                                        password = password,
                                        firstName = firstName,
                                        lastName = lastName,
                                        roleName = roleName
                                    )
                                )
                                
                                if (response.isSuccessful) {
                                    successMessage = "Konto utworzone! Możesz się zalogować."
                                    kotlinx.coroutines.delay(2000)
                                    onNavigateToLogin()
                                } else {
                                    errorMessage = response.errorBody()?.string() ?: "Błąd rejestracji"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Błąd połączenia: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }, 
                    modifier = Modifier.weight(1f).height(50.dp).padding(start = 8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Utwórz konto")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Masz już konto?")
            TextButton(onClick = onNavigateToLogin) { Text("Zaloguj się") }
        }
        TextButton(onClick = onNavigateBack) {
            Text("Wróć do strony głównej", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}