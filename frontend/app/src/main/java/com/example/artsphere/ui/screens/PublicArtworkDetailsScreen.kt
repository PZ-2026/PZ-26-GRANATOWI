package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicArtworkDetailScreen(
    artworkId: Long,
    isLoggedIn: Boolean,
    role: String,
    cartItems: List<ArtworkResponse>, // Przekazujemy koszyk z góry
    onAddToCart: (ArtworkResponse) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var artwork by remember { mutableStateOf<ArtworkResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Sprawdzamy czy obraz jest już w koszyku po ID
    val isInCart = cartItems.any { it.id == artworkId }

    LaunchedEffect(artworkId) {
        try {
            val response = RetrofitClient.artworkApi.getArtworkById(artworkId)
            if (response.isSuccessful) {
                artwork = response.body()
            }
        } catch (e: Exception) { } finally { isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły dzieła") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Wróć") } }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (artwork == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nie znaleziono dzieła.") }
        } else {
            val art = artwork!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, "Zdjęcie", modifier = Modifier.size(64.dp), tint = Color.Gray)
                }

                Text(text = art.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)

                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                    Text("Kategoria: ${art.categoryName ?: "Inne"}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }

                Divider()

                Text(text = "Autor: ${art.artist ?: art.userUsername}", fontSize = 18.sp)

                Text(text = "Opis:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = art.description ?: "Brak opisu", fontSize = 16.sp, lineHeight = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                Text("Cena:", color = Color.Gray, fontSize = 14.sp)
                if (art.isPriceless) {
                    Text("Bezcenny", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("${String.format("%.2f", art.price ?: 0.0)} zł", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LOGIKA PRZYCISKÓW KOSZYKA
                if (!isLoggedIn || role == "guest") {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Zaloguj się, aby dodać do koszyka i kupić to dzieło.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                            Button(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) { Text("Zaloguj się") }
                        }
                    }
                } else if (role == "BUYER" || role == "user") {
                    if (art.isSold) {
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = false, shape = RoundedCornerShape(12.dp)) {
                            Text("Sprzedane", fontSize = 18.sp)
                        }
                    } else if (isInCart) {
                        // PRZEDMIOT JEST JUŻ W KOSZYKU
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onNavigateToCart,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Produkt w koszyku - Przejdź", fontSize = 18.sp)
                            }
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Kontynuuj przeglądanie", fontSize = 18.sp)
                            }
                        }
                    } else {
                        // NORMALNE DODAWANIE
                        Button(
                            onClick = {
                                onAddToCart(art)
                                Toast.makeText(context, "Dodano do koszyka!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dodaj do koszyka", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}