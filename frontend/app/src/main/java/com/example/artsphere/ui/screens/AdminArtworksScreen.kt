package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.ui.ArtworkInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran zarządzania dziełami sztuki dla administratora
 * Wyświetla listę wszystkich dzieł z możliwością filtrowania
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminArtworksScreen(
    refreshTrigger: Int = 0,
    onBackClick: () -> Unit = {},
    onArtworkClick: (ArtworkInfo) -> Unit = {}
) {
    var artworks by remember { mutableStateOf<List<ArtworkInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = ""
        try {
            val response = RetrofitClient.adminApi.getAllArtworks()
            if (response.isSuccessful && response.body() != null) {
                artworks = response.body()!!.map { it.toArtworkInfo() }
            } else {
                errorMessage = "Nie udało się pobrać dzieł (${response.code()})"
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    // Filtrowanie dzieł
    val filteredArtworks = artworks.filter { artwork ->
        val matchesSearch = searchQuery.isEmpty() || 
            artwork.title.contains(searchQuery, ignoreCase = true) ||
            (artwork.artist?.contains(searchQuery, ignoreCase = true) ?: false) ||
            artwork.sellerUsername.contains(searchQuery, ignoreCase = true)
        
        val matchesCategory = selectedCategory == null || artwork.category == selectedCategory
        val matchesStatus = selectedStatus == null || artwork.status == selectedStatus
        
        matchesSearch && matchesCategory && matchesStatus
    }
    
    // Statystyki filtrowanych dzieł
    val totalFiltered = filteredArtworks.size
    val availableArtworks = filteredArtworks.count { it.status == "AVAILABLE" }
    val soldArtworks = filteredArtworks.count { it.status == "SOLD" }
    
    // Wszystkie kategorie
    val allCategories = artworks.mapNotNull { it.category }.distinct().sorted()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Zarządzanie dziełami",
                        color = Color.White,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtry",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(adminGradient)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Nagłówek z gradientem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(adminGradient)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Brush,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Galeria dzieł",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Przeglądaj i moderuj wszystkie dzieła na platformie",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                // Statystyki skrócone - klikalne filtry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArtworkStatCard(
                        value = totalFiltered.toString(),
                        label = "Wszystkie",
                        icon = Icons.Default.Brush,
                        color = Color(0xFF6650a4),
                        modifier = Modifier.weight(1f),
                        isSelected = selectedStatus == null,
                        onClick = { 
                            selectedStatus = null
                        }
                    )
                    ArtworkStatCard(
                        value = availableArtworks.toString(),
                        label = "Dostępne",
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        isSelected = selectedStatus == "AVAILABLE",
                        onClick = { 
                            selectedStatus = "AVAILABLE"
                        }
                    )
                    ArtworkStatCard(
                        value = soldArtworks.toString(),
                        label = "Sprzedane",
                        icon = Icons.Default.Sell,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        isSelected = selectedStatus == "SOLD",
                        onClick = { 
                            selectedStatus = "SOLD"
                        }
                    )
                }
                
                // Pasek wyszukiwania
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj dzieła, artysty...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Szukaj")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Wyczyść")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                
                // Panel filtrów
                if (showFilterMenu) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Filtry",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Filtr kategorii
                            Text("Kategoria:", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory ?: "Wszystkie",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Wszystkie") },
                                        onClick = {
                                            selectedCategory = null
                                            expanded = false
                                        }
                                    )
                                    allCategories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category) },
                                            onClick = {
                                                selectedCategory = category
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Filtr statusu
                            Text("Status:", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedStatus == null,
                                    onClick = { selectedStatus = null },
                                    label = { Text("Wszystkie") }
                                )
                                FilterChip(
                                    selected = selectedStatus == "AVAILABLE",
                                    onClick = { selectedStatus = if (selectedStatus == "AVAILABLE") null else "AVAILABLE" },
                                    label = { Text("Dostępne") }
                                )
                                FilterChip(
                                    selected = selectedStatus == "SOLD",
                                    onClick = { selectedStatus = if (selectedStatus == "SOLD") null else "SOLD" },
                                    label = { Text("Sprzedane") }
                                )
                            }
                        }
                    }
                }
                
                // Informacja o liczbie wyników
                Text(
                    "Znaleziono: $totalFiltered ${if (totalFiltered == 1) "dzieło" else if (totalFiltered < 5) "dzieła" else "dzieł"}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                
                // Lista dzieł
                if (filteredArtworks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Nie znaleziono dzieł",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredArtworks) { artwork ->
                            ArtworkCard(
                                artwork = artwork,
                                onClick = { onArtworkClick(artwork) }
                            )
                        }
                        
                        // Spacer na końcu
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Karta ze statystyką dzieł - klikalna
 */
@Composable
fun ArtworkStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(85.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Karta pojedynczego dzieła
 */
@Composable
fun ArtworkCard(
    artwork: ArtworkInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder obrazu
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getStatusColor(artwork.status).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Zdjęcie dzieła",
                    tint = getStatusColor(artwork.status),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informacje o dziele
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = artwork.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = getStatusColor(artwork.status).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = getStatusName(artwork.status),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = getStatusColor(artwork.status)
                        )
                    }
                }
                
                Text(
                    text = artwork.artist ?: "Nieznany artysta",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Kategoria
                    if (artwork.category != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = "Kategoria",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = artwork.category,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    // Sprzedawca
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Sprzedawca",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = artwork.sellerUsername,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Cena i wyświetlenia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (artwork.isPriceless) "Bezcenne" else formatCurrency(artwork.price ?: 0.0),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (artwork.isPriceless) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "Wyświetlenia",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${artwork.views}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Przycisk szczegółów
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Szczegóły",
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * Pomocnicze funkcje
 */
private fun getStatusColor(status: String): Color {
    return when (status) {
        "AVAILABLE" -> Color(0xFF4CAF50)
        "SOLD" -> Color(0xFFFF9800)
        "HIDDEN" -> Color(0xFF9E9E9E)
        else -> Color.Gray
    }
}

private fun getStatusName(status: String): String {
    return when (status) {
        "AVAILABLE" -> "Dostępne"
        "SOLD" -> "Sprzedane"
        "HIDDEN" -> "Ukryte"
        else -> status
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}

fun ArtworkResponse.toArtworkInfo(): ArtworkInfo {
    return ArtworkInfo(
        id = id,
        title = title,
        artist = artist,
        description = description,
        price = price,
        isPriceless = isPriceless,
        category = categoryName,
        imagePath = imagePath,
        width = width,
        height = height,
        depth = depth,
        sellerId = userId,
        sellerUsername = userUsername,
        isSold = isSold,
        status = status,
        createdAt = createdAt?.replace("T", " ")?.substringBefore(".") ?: "Brak danych",
        views = 0
    )
}
