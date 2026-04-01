package com.example.artsphere.ui.screens.Client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowedOffersScreen(userId: Long, onNavigateBack: () -> Unit, onNavigateToArtwork: (Long) -> Unit) {
    var artworks by remember { mutableStateOf<List<ArtworkResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        try {
            val response = RetrofitClient.authApi.getFollowedArtworks(userId)
            if (response.isSuccessful && response.body() != null) {
                artworks = response.body()!!
            }
        } catch (e: Exception) { } finally { isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dzieła obserwowanych") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (artworks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nie ma żadnych ofert od obserwowanych artystów.") }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(artworks) { art ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(art.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Artysta: ${art.artist}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("${String.format("%.2f", art.price)} zł", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Button(onClick = { onNavigateToArtwork(art.id) }) { Text("Zobacz") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}