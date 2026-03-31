package com.example.artsphere.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient

@Composable
fun SearchSection(onArtworkClick: (Long) -> Unit) {
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }

    var artworks by remember { mutableStateOf<List<ArtworkResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Pobieranie wszystkich dostępnych dzieł z backendu przy załadowaniu komponentu
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = RetrofitClient.artworkApi.getAllAvailableArtworks()
            if (response.isSuccessful && response.body() != null) {
                artworks = response.body()!!
            } else {
                errorMessage = "Nie udało się załadować dzieł."
            }
        } catch (e: Exception) {
            errorMessage = "Błąd: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Filtrowanie listy na podstawie wyszukiwarki
    val filteredArtworks = artworks.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                (it.artist?.contains(searchQuery, ignoreCase = true) ?: false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(16.dp)
    ) {
        Text(
            text = "Przeglądaj dzieła",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Szukaj obrazów, rzeźb, artystów...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        } else if (filteredArtworks.isEmpty()) {
            Text("Brak wyników do wyświetlenia.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            // Wyświetlanie dzieł w siatce
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 600.dp) // Wysokość siatki, aby dało się przewijać stronę
            ) {
                items(filteredArtworks) { artwork ->
                    PublicArtworkCard(artwork = artwork, onClick = { onArtworkClick(artwork.id) })
                }
            }
        }
    }
}

@Composable
fun PublicArtworkCard(artwork: ArtworkResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Domyślny obrazek (Szare tło)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Brak zdjęcia",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = artwork.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = artwork.artist ?: "Nieznany artysta",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (artwork.isPriceless) {
                    Text(text = "Bezcenny", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(text = "${String.format("%.2f", artwork.price ?: 0.0)} zł", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}