package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var artwork by remember { mutableStateOf<ArtworkResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(artworkId) {
        try {
            val response = RetrofitClient.artworkApi.getArtworkById(artworkId)
            if (response.isSuccessful) {
                artwork = response.body()
            }
        } catch (e: Exception) {
            // Ignorujemy błędy dla czystości interfejsu (pokaże się Brak wyników)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły dzieła") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Wróć") }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (artwork == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nie znaleziono dzieła.")
            }
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
                // Domyślne zdjęcie dzieła
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Zdjęcie",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }

                Text(text = art.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Kategoria: ${art.categoryName ?: "Inne"}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider()

                Text(text = "Autor: ${art.artist ?: art.userUsername}", fontSize = 18.sp)

                val dimensions = listOfNotNull(
                    art.width?.let { "Szer: $it cm" },
                    art.height?.let { "Wys: $it cm" },
                    art.depth?.let { "Głęb: $it cm" }
                ).joinToString(" | ")

                if (dimensions.isNotEmpty()) {
                    Text(text = "Wymiary: $dimensions", fontSize = 16.sp, color = Color.Gray)
                }

                Text(text = "Opis:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = art.description ?: "Brak opisu", fontSize = 16.sp, lineHeight = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // SEKCJA ZAKUPOWA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cena:", color = Color.Gray)
                        if (art.isPriceless) {
                            Text("Bezcenny", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("${String.format("%.2f", art.price ?: 0.0)} zł", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Logika przycisków na wzór Galeriony:
                    if (!isLoggedIn) {
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Zaloguj się, aby kupić")
                        }
                    } else if (role == "user" || role == "BUYER") {
                        Button(
                            onClick = { Toast.makeText(context, "Dodano do koszyka!", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.height(50.dp),
                            enabled = !art.isSold
                        ) {
                            if (art.isSold) {
                                Text("Sprzedane")
                            } else {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dodaj do koszyka")
                            }
                        }
                    }
                }
            }
        }
    }
}