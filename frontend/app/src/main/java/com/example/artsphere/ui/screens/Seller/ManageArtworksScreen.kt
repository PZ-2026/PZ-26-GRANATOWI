package com.example.artsphere.ui.screens.Seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Model danych
data class Artwork(
    val title: String,
    val artist: String,
    val price: String,
    val category: String,
    val dimensions: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageArtworksScreen(
    onNavigateBack: () -> Unit,
    onAddNewClick: () -> Unit,
    onPreviewClick: () -> Unit // Dodany parametr nawigacji do szczegółów
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Stan dla dialogu usuwania
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedArtwork by remember { mutableStateOf<Artwork?>(null) }

    // Dane testowe ze screena[cite: 10]
    val artworks = listOf(
        Artwork("Zachód słońca nad morzem", "Jakub Artystowski", "1500.00", "Malarstwo", "60 x 40 x 3"),
        Artwork("Portret kobiety w kapeluszu", "Jakub Artystowski", "850.00", "Malarstwo", "35 x 50 x 2"),
        Artwork("Miejski pejzaż nocą", "Jakub Artystowski", "1800.00", "Malarstwo", "70 x 50 x 3")
    )

    // Dialog potwierdzenia usunięcia
    if (showDeleteDialog && selectedArtwork != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń dzieło") },
            text = { Text("Czy na pewno chcesz usunąć „${selectedArtwork?.title}”?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Usunięto: ${selectedArtwork?.title}")
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Usuń") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Anuluj") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Twoje dzieła") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewClick,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(text = "Dodaj nowe")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(artworks) { artwork ->
                ArtworkManageCard(
                    artwork = artwork,
                    onPreview = onPreviewClick,
                    onEdit = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Edycja: ${artwork.title}")
                        }
                    },
                    onDelete = {
                        selectedArtwork = artwork
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun ArtworkManageCard(
    artwork: Artwork,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artwork.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Artysta: ${artwork.artist}", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Cena: ${artwork.price} zł", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Text(text = artwork.category, style = MaterialTheme.typography.bodySmall)
            }

            Text(text = "Wymiary: ${artwork.dimensions} cm", style = MaterialTheme.typography.bodySmall)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Przyciski Akcji ze screena[cite: 10, 11]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Podgląd", fontSize = 12.sp)
                }
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Edytuj", fontSize = 12.sp)
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Usuń", fontSize = 12.sp)
                }
            }
        }
    }
}