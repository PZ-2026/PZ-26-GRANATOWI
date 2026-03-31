package com.example.artsphere.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RegisterRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.delay
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

    // Wyrażenia regularne do walidacji
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    val nameCityPattern = "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\-\\s]+\$".toRegex()
    val usernamePattern = "^[A-Za-z0-9_]+\$".toRegex()

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

        // Estetyczny baner błędu
        if (errorMessage.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Estetyczny baner sukcesu
        if (successMessage.isNotEmpty()) {
            Surface(
                color = Color(0xFFE8F5E9), // Jasny zielony
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = successMessage,
                    color = Color(0xFF2E7D32), // Ciemny zielony
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedRole == "user",
                onClick = { selectedRole = "user" },
                label = { Text("Kupujący") },
                modifier = Modifier.padding(end = 8.dp),
                enabled = !isLoading
            )
            FilterChip(
                selected = selectedRole == "seller",
                onClick = { selectedRole = "seller" },
                label = { Text("Sprzedawca / Artysta") },
                enabled = !isLoading
            )
        }

        if (currentStep == 1) {
            OutlinedTextField(
                value = username, onValueChange = { username = it.take(50); errorMessage = "" },
                label = { Text("Nazwa użytkownika *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true,
                enabled = !isLoading
            )
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(
                    value = firstName, onValueChange = { firstName = it.take(50); errorMessage = "" },
                    label = { Text("Imię *") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = lastName, onValueChange = { lastName = it.take(50); errorMessage = "" },
                    label = { Text("Nazwisko *") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true,
                    enabled = !isLoading
                )
            }
            OutlinedTextField(
                value = email, onValueChange = { email = it.take(100); errorMessage = "" },
                label = { Text("E-mail *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it.take(100); errorMessage = "" },
                label = { Text("Hasło *") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, enabled = !isLoading) {
                        Icon(imageVector = image, contentDescription = "Pokaż hasło")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it.take(100); errorMessage = "" },
                label = { Text("Potwierdź hasło *") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), singleLine = true,
                enabled = !isLoading
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    // Pełna walidacja Kroku 1
                    when {
                        username.isBlank() || firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Wypełnij wszystkie wymagane pola z gwiazdką"
                        }
                        username.length < 3 || !usernamePattern.matches(username) -> {
                            errorMessage = "Nazwa użytkownika musi mieć min. 3 znaki (tylko litery, cyfry i _)"
                        }
                        firstName.length < 2 || !nameCityPattern.matches(firstName) -> {
                            errorMessage = "Podaj poprawne imię (tylko litery)"
                        }
                        lastName.length < 2 || !nameCityPattern.matches(lastName) -> {
                            errorMessage = "Podaj poprawne nazwisko (tylko litery)"
                        }
                        !emailPattern.matches(email) -> {
                            errorMessage = "Podaj poprawny format e-mail"
                        }
                        password.length < 6 -> {
                            errorMessage = "Hasło musi mieć co najmniej 6 znaków"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Hasła nie są identyczne"
                        }
                        else -> {
                            errorMessage = ""
                            currentStep = 2
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                Text("Dalej")
            }

        } else {
            // KROK 2 - Adres i opcje
            OutlinedTextField(
                value = city, onValueChange = { city = it.take(50); errorMessage = "" },
                label = { Text("Miasto *") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true,
                enabled = !isLoading
            )

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { newValue ->
                        errorMessage = ""
                        // Automatyczne formatowanie kodu pocztowego (XX-XXX)
                        val digits = newValue.filter { it.isDigit() }.take(5)
                        postalCode = if (digits.length > 2) {
                            "${digits.take(2)}-${digits.drop(2)}"
                        } else {
                            digits
                        }
                    },
                    label = { Text("Kod pocztowy *", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(140.dp).padding(end = 4.dp),
                    singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = street, onValueChange = { street = it.take(100); errorMessage = "" },
                    label = { Text("Ulica *") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(
                    value = houseNumber, onValueChange = { houseNumber = it.take(20); errorMessage = "" },
                    label = { Text("Nr domu *") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = apartmentNumber, onValueChange = { apartmentNumber = it.take(20) },
                    label = { Text("Nr lokalu") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true,
                    enabled = !isLoading
                )
            }

            if (selectedRole == "seller") {
                OutlinedTextField(
                    value = sellerDescription, onValueChange = { sellerDescription = it.take(500); errorMessage = "" },
                    label = { Text("Opis sprzedawcy *") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(100.dp),
                    enabled = !isLoading
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = { currentStep = 1; errorMessage = "" },
                    modifier = Modifier.weight(1f).height(50.dp).padding(end = 8.dp),
                    enabled = !isLoading
                ) {
                    Text("Wstecz")
                }
                Button(
                    onClick = {
                        focusManager.clearFocus()

                        // Pełna walidacja Kroku 2
                        when {
                            city.isBlank() || postalCode.isBlank() || street.isBlank() || houseNumber.isBlank() -> {
                                errorMessage = "Wypełnij wszystkie wymagane pola adresu (*)"
                                return@Button
                            }
                            city.length < 2 || !nameCityPattern.matches(city) -> {
                                errorMessage = "Podaj poprawną nazwę miasta (tylko litery)"
                                return@Button
                            }
                            postalCode.length != 6 -> {
                                errorMessage = "Kod pocztowy musi mieć format XX-XXX"
                                return@Button
                            }
                            street.length < 2 -> {
                                errorMessage = "Podaj poprawną nazwę ulicy"
                                return@Button
                            }
                            selectedRole == "seller" && sellerDescription.isBlank() -> {
                                errorMessage = "Opis sprzedawcy jest wymagany"
                                return@Button
                            }
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
                                    successMessage = "Konto utworzone pomyślnie!\nZa chwilę nastąpi przekierowanie..."
                                    // Czekamy 2 sekundy, widoczny komunikat z zablokowanym formularzem
                                    delay(2000)
                                    onNavigateToLogin()
                                } else {
                                    errorMessage = response.errorBody()?.string() ?: "Błąd rejestracji"
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                errorMessage = "Błąd połączenia: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp).padding(start = 8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading && successMessage.isEmpty()) {
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
            TextButton(onClick = onNavigateToLogin, enabled = !isLoading) { Text("Zaloguj się") }
        }
        TextButton(onClick = onNavigateBack, enabled = !isLoading) {
            Text("Wróć do strony głównej", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}