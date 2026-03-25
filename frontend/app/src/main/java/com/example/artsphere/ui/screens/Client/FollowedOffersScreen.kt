package com.example.artsphere.ui.screens.Client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Model na podstawie screena[cite: 7]
data class FollowedOffer(
    val title: String,
    val author: String,
    val price: String,
    val category: String,
    val owner: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowedOffersScreen(onNavigateBack: () -> Unit) {
    // Dane testowe ze screena[cite: 7]
    val offers = listOf(
        FollowedOffer(
            title = "Zachód słońca nad morzem",
            author = "Jakub Artystowski",
            price = "1500.00 zł",
            category = "Malarstwo",
            owner = "jakub_art"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zaobserwowane oferty") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Oferty zaobserwowanych sprzedawców",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(offers) { offer ->
                    FollowedOfferCard(offer)
                }
            }
        }
    }
}

@Composable
fun FollowedOfferCard(offer: FollowedOffer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miejsce na zdjęcie[cite: 7]
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Obraz", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
                Text(text = offer.author, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Kat: ${offer.category}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Właściciel: ${offer.owner}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = offer.price,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            }

            // Przycisk "Dodaj do koszyka"[cite: 7] - na mobile jako ikona dla oszczędności miejsca
            FilledIconButton(
                onClick = { /* Dodaj do koszyka */ },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF1B5E20) // Ciemny zielony ze screena
                )
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Dodaj do koszyka")
            }
        }
    }
}