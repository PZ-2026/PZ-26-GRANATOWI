package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.SellerInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran zarządzania sprzedawcami dla administratora
 * Wyświetla listę wszystkich sprzedawców z możliwością filtrowania i szczegółowego widoku
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSellersScreen(
    onBackClick: () -> Unit = {},
    onSellerClick: (SellerInfo) -> Unit = {}
) {
    val sellers = remember { MockStatisticsProvider.getMockSellers() }
    var searchQuery by remember { mutableStateOf("") }
    var showActiveOnly by remember { mutableStateOf(false) }
    var showVerifiedOnly by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("revenue") } // "revenue", "artworks", "rating", "followers"
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    // Filtrowanie sprzedawców
    val filteredSellers = sellers.filter { seller ->
        val matchesSearch = searchQuery.isEmpty() || 
            seller.username.contains(searchQuery, ignoreCase = true) ||
            seller.email.contains(searchQuery, ignoreCase = true) ||
            seller.firstName.contains(searchQuery, ignoreCase = true) ||
            seller.lastName.contains(searchQuery, ignoreCase = true)
        
        val matchesActive = !showActiveOnly || seller.isActive
        val matchesVerified = !showVerifiedOnly || seller.isVerified
        
        matchesSearch && matchesActive && matchesVerified
    }.sortedByDescending { seller ->
        when (sortBy) {
            "revenue" -> seller.totalRevenue
            "artworks" -> seller.totalArtworks.toDouble()
            "rating" -> seller.averageRating.toDouble()
            "followers" -> seller.followerCount.toDouble()
            else -> seller.totalRevenue
        }
    }
    
    // Statystyki filtrowanych sprzedawców
    val totalFiltered = filteredSellers.size
    val verifiedSellers = filteredSellers.count { it.isVerified }
    val totalRevenue = filteredSellers.sumOf { it.totalRevenue }
    val totalArtworks = filteredSellers.sumOf { it.totalArtworks }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Zarządzanie sprzedawcami",
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
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Sprzedawcy platformy",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Monitoruj aktywność i wyniki sprzedażowe",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        "💡 Kliknij na statystykę aby posortować",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            // Statystyki skrócone - klikalne sortowanie
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SellerStatCard(
                    value = totalFiltered.toString(),
                    label = "Sprzedawców",
                    icon = Icons.Default.Storefront,
                    color = Color(0xFF6650a4),
                    modifier = Modifier.weight(1f),
                    isSelected = sortBy == "artworks",
                    onClick = { sortBy = "artworks" }
                )
                SellerStatCard(
                    value = verifiedSellers.toString(),
                    label = "Zweryfikowanych",
                    icon = Icons.Default.Verified,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    isSelected = sortBy == "rating",
                    onClick = { sortBy = "rating" }
                )
                SellerStatCard(
                    value = formatCurrencyShort(totalRevenue),
                    label = "Przychód",
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    isSelected = sortBy == "revenue",
                    onClick = { sortBy = "revenue" }
                )
            }
            
            // Pasek wyszukiwania
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Szukaj sprzedawcy...") },
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
                            "Filtry i sortowanie",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Filtry
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = showActiveOnly,
                                onCheckedChange = { showActiveOnly = it }
                            )
                            Text("Tylko aktywni")
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Checkbox(
                                checked = showVerifiedOnly,
                                onCheckedChange = { showVerifiedOnly = it }
                            )
                            Text("Tylko zweryfikowani")
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Sortowanie
                        Text("Sortuj według:", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = sortBy == "revenue",
                                onClick = { sortBy = "revenue" },
                                label = { Text("Przychodu") }
                            )
                            FilterChip(
                                selected = sortBy == "artworks",
                                onClick = { sortBy = "artworks" },
                                label = { Text("Dzieł") }
                            )
                            FilterChip(
                                selected = sortBy == "rating",
                                onClick = { sortBy = "rating" },
                                label = { Text("Oceny") }
                            )
                        }
                    }
                }
            }
            
            // Informacja o liczbie wyników
            Text(
                "Znaleziono: $totalFiltered ${if (totalFiltered == 1) "sprzedawca" else if (totalFiltered < 5) "sprzedawców" else "sprzedawców"} • ${totalArtworks} dzieł",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color = Color.Gray
            )
            
            // Lista sprzedawców
            if (filteredSellers.isEmpty()) {
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
                            "Nie znaleziono sprzedawców",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSellers) { seller ->
                        SellerCard(
                            seller = seller,
                            onClick = { onSellerClick(seller) }
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

/**
 * Karta ze statystyką sprzedawcy - klikalna
 */
@Composable
fun SellerStatCard(
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
 * Karta pojedynczego sprzedawcy
 */
@Composable
fun SellerCard(
    seller: SellerInfo,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9800).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = "Sprzedawca",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informacje o sprzedawcy
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = seller.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Weryfikacja badge
                    if (seller.isVerified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Zweryfikowany",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    // Status aktywności
                    if (!seller.isActive) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "Nieaktywny",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
                
                Text(
                    text = "${seller.firstName} ${seller.lastName}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Statystyki sprzedawcy
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dzieła
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Brush,
                            contentDescription = "Dzieła",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${seller.activeArtworks}/${seller.totalArtworks}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    // Ocena
                    if (seller.averageRating > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Ocena",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = String.format("%.1f", seller.averageRating),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                    
                    // Obserwujący
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "Obserwujący",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${seller.followerCount}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Przychód
                Text(
                    text = "Przychód: ${formatCurrency(seller.totalRevenue)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
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
private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}

private fun formatCurrencyShort(amount: Double): String {
    return when {
        amount >= 1000000 -> String.format("%.1fM", amount / 1000000)
        amount >= 1000 -> String.format("%.1fk", amount / 1000)
        else -> String.format("%.0f", amount)
    }
}
