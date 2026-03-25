package com.example.artsphere.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.LoginRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String, String) -> Unit // (Username, Role)
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Witaj ponownie!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))

        Text("Kupujący: buyer@gmail.pl / buyer123", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Sprzedawca: artist@gmail.com / artist123", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Admin: admin@gmail.com / admin123", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 32.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
        }

        OutlinedTextField(
            value = email, 
            onValueChange = { email = it; errorMessage = "" },
            label = { Text("Adres E-mail") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), 
            singleLine = true,
            enabled = !isLoading
        )

        OutlinedTextField(
            value = password, 
            onValueChange = { password = it; errorMessage = "" },
            label = { Text("Hasło") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Pokaż hasło")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), 
            singleLine = true,
            enabled = !isLoading
        )

        Button(
            onClick = {
                focusManager.clearFocus()
                
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Wypełnij wszystkie pola"
                    return@Button
                }
                
                isLoading = true
                errorMessage = ""
                
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.authApi.login(
                            LoginRequest(email = email, password = password)
                        )
                        
                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()!!
                            val role = when(loginResponse.role) {
                                "ADMIN" -> "admin"
                                "ARTIST" -> "seller"
                                "BUYER" -> "user"
                                else -> "user"
                            }
                            val displayName = "${loginResponse.firstName ?: ""} ${loginResponse.lastName ?: ""}".trim()
                                .ifEmpty { loginResponse.username }
                            
                            onLoginSuccess(displayName, role)
                        } else {
                            errorMessage = response.errorBody()?.string() ?: "Błąd logowania"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Błąd połączenia: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Zaloguj się")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Nie masz konta?")
            TextButton(onClick = onNavigateToRegister) { Text("Zarejestruj się") }
        }
        TextButton(onClick = onNavigateBack) {
            Text("Wróć do strony głównej", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}