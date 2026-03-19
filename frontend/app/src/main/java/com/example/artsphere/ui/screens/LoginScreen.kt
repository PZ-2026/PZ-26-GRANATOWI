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

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // blad dla zlych danych
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Witaj ponownie!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
        Text("Zaloguj się (użyj: test@gmail.com / test123)", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 32.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
        }

        OutlinedTextField(
            value = email, onValueChange = { email = it; errorMessage = "" },
            label = { Text("Adres E-mail") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), singleLine = true
        )

        OutlinedTextField(
            value = password, onValueChange = { password = it; errorMessage = "" },
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), singleLine = true
        )

        Button(
            onClick = {
                focusManager.clearFocus()
                // logowanie na sztywno
                if (email == "test@gmail.com" && password == "test123") {
                    onLoginSuccess("Jan Kowalski")
                } else {
                    errorMessage = "Nieprawidłowy e-mail lub hasło!"
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Zaloguj się")
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