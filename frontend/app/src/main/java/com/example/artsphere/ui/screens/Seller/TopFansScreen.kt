package com.example.artsphere.ui.screens.Seller

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RetrofitClient

data class Fan(
    val name: String,
    val purchaseCount: Int,
    val totalSpent: String,
    val memberSince: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopFansScreen(sellerId: Long, onNavigateBack: () -> Unit) {
    var fans by remember { mutableStateOf<List<Fan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sellerId) {
        try {
            val response = RetrofitClient.authApi.getTopFans(sellerId)
            if (response.isSuccessful) {
                fans = response.body() ?: emptyList()
            } else {
                errorMessage = "Błąd pobierania: ${response.code()}"
                Log.e("TopFansScreen", "Błąd API: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
            Log.e("TopFansScreen", "Wyjątek podczas pobierania", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Najlepsi fani") },
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
            // Nagłówek z ikoną pucharu
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color(0xFFD4AF37)) // Złoty kolor
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Twoi najwięksi mecenasi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Dziękujemy za ich wsparcie!", fontSize = 14.sp)
                    }
                }
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                fans.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Jeszcze nikt od Ciebie nie kupował.", color = MaterialTheme.colorScheme.secondary)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(fans) { index, fan ->
                            FanCard(index + 1, fan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FanCard(rank: Int, fan: Fan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numer w rankingu
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (rank <= 3) Color(0xFFD4AF37).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color(0xFFB8860B) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = fan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Fani od: ${fan.memberSince}", style = MaterialTheme.typography.bodySmall)
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${fan.purchaseCount} dzieł", fontWeight = FontWeight.SemiBold)
                }
                Text(text = fan.totalSpent, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }
        }
    }
}