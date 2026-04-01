package com.example.artsphere.ui.screens.Client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtistDto
import com.example.artsphere.api.DonationHistoryResponse
import com.example.artsphere.api.DonationRequest
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    userId: Long,
    currentBalance: Double,
    onBalanceChange: (Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var artists by remember { mutableStateOf<List<ArtistDto>>(emptyList()) }
    var history by remember { mutableStateOf<List<DonationHistoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var selectedArtist by remember { mutableStateOf<ArtistDto?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val fetchHistory: () -> Unit = {
        coroutineScope.launch {
            try {
                val res = RetrofitClient.authApi.getSupportHistory(userId)
                if (res.isSuccessful && res.body() != null) {
                    history = res.body()!!
                }
            } catch (e: Exception) { }
        }
    }

    LaunchedEffect(userId) {
        try {
            val artRes = RetrofitClient.authApi.getArtists()
            if (artRes.isSuccessful && artRes.body() != null) {
                artists = artRes.body()!!
            }
            fetchHistory()
        } catch (e: Exception) { } finally { isLoading = false }
    }

    // funkcja generująca pełne Imię i Nazwisko artysty
    val getArtistFullName: (ArtistDto) -> String = { artist ->
        val first = artist.firstName ?: ""
        val last = artist.lastName ?: ""
        val full = "$first $last".trim()

        // jeśli nie ma imienia/nazwiska, zwraca sam pseudonim
        full.ifEmpty { artist.username }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wesprzyj twórcę") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // PANEL WYŚLIJ WSPARCIE
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Twój portfel: ${String.format("%.2f", currentBalance)} zł", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedArtist?.let { getArtistFullName(it) } ?: "Wybierz artystę...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Artysta") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            artists.forEach { artist ->
                                DropdownMenuItem(
                                    text = { Text(getArtistFullName(artist)) }, // Na liście widać Imię i Nazwisko
                                    onClick = {
                                        selectedArtist = artist
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // --- Wyświetlanie pseudonimu  wybranego artysty ---
                    if (selectedArtist != null) {
                        Text(
                            text = "Pseudonim: @${selectedArtist!!.username}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amountText = it },
                        label = { Text("Kwota do wysłania (zł)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (selectedArtist == null) {
                                Toast.makeText(context, "Wybierz artystę", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (amount <= 0 || amount > currentBalance) {
                                Toast.makeText(context, "Błędna kwota lub brak środków!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSending = true
                            coroutineScope.launch {
                                try {
                                    val req = DonationRequest(userId, selectedArtist!!.id, amount)
                                    val res = RetrofitClient.authApi.sendSupport(req)
                                    if (res.isSuccessful && res.body() != null) {
                                        val newBal = res.body()!!["newBalance"] ?: 0.0
                                        onBalanceChange(newBal)
                                        amountText = ""
                                        selectedArtist = null
                                        fetchHistory() // Odświeżenie w czasie rzeczywistym!
                                        Toast.makeText(context, "Dziękujemy za wsparcie!", Toast.LENGTH_LONG).show()
                                    } else { Toast.makeText(context, "Błąd z serwerem.", Toast.LENGTH_SHORT).show() }
                                } catch (e: Exception) { Toast.makeText(context, "Brak sieci.", Toast.LENGTH_SHORT).show() }
                                finally { isSending = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isSending
                    ) {
                        if (isSending) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else {
                            Icon(Icons.Default.Favorite, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Wyślij wsparcie", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Twoja historia wsparcia", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (history.isEmpty()) {
                Text("Jeszcze nikogo nie wsparłeś. Bądź pierwszy!", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(history) { don ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Wsparcie dla:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(don.artistName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text("-${String.format("%.2f", don.amount)} zł", color = Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}