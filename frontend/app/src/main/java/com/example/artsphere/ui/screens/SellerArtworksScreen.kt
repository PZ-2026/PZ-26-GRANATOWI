package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerArtworksScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    onAddArtwork: () -> Unit,
    onEditArtwork: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var artworks by remember { mutableStateOf<List<ArtworkResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var artworkToDelete by remember { mutableStateOf<Long?>(null) }

    // Załaduj dzieła
    LaunchedEffect(userId) {
        isLoading = true
        try {
            val response = RetrofitClient.artworkApi.getSellerArtworks(userId)
            
            if (response.isSuccessful && response.body() != null) {
                artworks = response.body()!!
            } else {
                errorMessage = "Nie udało się pobrać dzieł"
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje dzieła") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddArtwork) {
                Icon(Icons.Default.Add, "Dodaj dzieło")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            } else if (artworks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Brak dzieł", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dodaj swoje pierwsze dzieło sztuki", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(artworks) { artwork ->
                        ArtworkCard(
                            artwork = artwork,
                            onEdit = { onEditArtwork(artwork.id) },
                            onDelete = {
                                artworkToDelete = artwork.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Dialog potwierdzenia usunięcia
        if (showDeleteDialog && artworkToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Usuń dzieło") },
                text = { Text("Czy na pewno chcesz usunąć to dzieło?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.artworkApi.deleteArtwork(
                                        artworkToDelete!!,
                                        userId
                                    )
                                    if (response.isSuccessful) {
                                        artworks = artworks.filter { it.id != artworkToDelete }
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Nie udało się usunąć dzieła"
                                }
                                showDeleteDialog = false
                                artworkToDelete = null
                            }
                        }
                    ) {
                        Text("Usuń", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}

@Composable
fun ArtworkCard(
    artwork: ArtworkResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = artwork.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!artwork.artist.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Artysta: ${artwork.artist}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (artwork.isPriceless) {
                        Text(
                            text = "Bezcenne",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "${artwork.price?.let { "%.2f".format(it) } ?: "0.00"} zł",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (artwork.categoryName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kategoria: ${artwork.categoryName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edytuj", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Usuń", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}