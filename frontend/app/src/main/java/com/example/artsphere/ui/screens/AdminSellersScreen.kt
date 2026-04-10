package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
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
import com.example.artsphere.api.AdminSellerResponse
import com.example.artsphere.api.RetrofitClient
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
    refreshTrigger: Int = 0,
    onBackClick: () -> Unit = {},
    onSellerClick: (SellerInfo) -> Unit = {}
) {
    var sellers by remember { mutableStateOf<List<SellerInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    var searchQuery by remember { mutableStateOf("") }
    var showActiveOnly by remember { mutableStateOf(false) }
    var showVerifiedOnly by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("revenue") }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = ""
        try {
            val response = RetrofitClient.adminApi.getAllSellers()
            if (response.isSuccessful && response.body() != null) {
                sellers = response.body()!!.map { it.toSellerInfo() }
            } else {
                errorMessage = "Nie udało się pobrać sprzedawców (${response.code()})"
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    // Filtrowanie i Sortowanie sprzedawców
    val filteredSellers = sellers.filter { seller ->
        val matchesSearch = searchQuery.isEmpty() || 
            seller.username.contains(searchQuery, ignoreCase = true) ||
            seller.email.contains(searchQuery, ignoreCase = true) ||
            seller.firstName.contains(searchQuery, ignoreCase = true) ||
            seller.lastName.contains(searchQuery, ignoreCase = true)
        
        val matchesActive = !showActiveOnly || seller.isActive
        val matchesVerified = !showVerifiedOnly || seller.isVerified
        
        matchesSearch && matchesActive && matchesVerified
    }.sortedWith(compareByDescending { seller ->
        when (sortBy) {
            "revenue" -> seller.totalRevenue
            "artworks" -> seller.totalArtworks.toDouble()
            "rating" -> seller.averageRating.toDouble()
            "followers" -> seller.followerCount.toDouble()
            else -> seller.totalRevenue
        }
    })
    
    // Obliczanie danych dla dynamicznego kafelka lidera
    val topValue = when (sortBy) {
        "revenue" -> formatCurrencyShort(sellers.maxOfOrNull { it.totalRevenue } ?: 0.0)
        "artworks" -> (sellers.maxOfOrNull { it.totalArtworks } ?: 0).toString()
        "rating" -> String.format("%.1f", sellers.maxOfOrNull { it.averageRating } ?: 0.0f)
        "followers" -> (sellers.maxOfOrNull { it.followerCount } ?: 0).toString()
        else -> "0"
    }

    val dynamicLabel = when (sortBy) {
        "revenue" -> "Top Przychód"
        "artworks" -> "Top Dzieł"
        "rating" -> "Top Ocena"
        "followers" -> "Top Fanów"
        else -> "Przychód"
    }

    val dynamicIcon = when (sortBy) {
        "revenue" -> Icons.Default.TrendingUp
        "artworks" -> Icons.Default.Brush
        "rating" -> Icons.Default.Star
        "followers" -> Icons.Default.Group
        else -> Icons.Default.TrendingUp
    }

    val totalFiltered = filteredSellers.size
    val verifiedSellersCount = sellers.count { it.isVerified }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarządzanie sprzedawcami", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtry", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(adminGradient)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sprzedawcy platformy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Monitoruj aktywność i wyniki sprzedażowe", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
            }
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SellerStatCard(
                        value = sellers.size.toString(),
                        label = "Łącznie",
                        icon = Icons.Default.Storefront,
                        color = Color(0xFF6650a4),
                        modifier = Modifier.weight(1f),
                        isSelected = !showVerifiedOnly && searchQuery.isEmpty(),
                        onClick = {
                            showVerifiedOnly = false
                            searchQuery = ""
                        }
                    )
                    SellerStatCard(
                        value = verifiedSellersCount.toString(),
                        label = "Zweryfikowanych",
                        icon = Icons.Default.Verified,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        isSelected = showVerifiedOnly,
                        onClick = { showVerifiedOnly = !showVerifiedOnly }
                    )
                    // DYNAMICZNY KAFELEK LIDERÓW
                    SellerStatCard(
                        value = topValue,
                        label = dynamicLabel,
                        icon = dynamicIcon,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        isSelected = true,
                        onClick = { showFilterMenu = true }
                    )
                }
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj sprzedawcy...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null) }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                
                if (showFilterMenu) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Filtry i sortowanie", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = showActiveOnly, onCheckedChange = { showActiveOnly = it })
                                Text("Tylko aktywni")
                                Spacer(modifier = Modifier.width(16.dp))
                                Checkbox(checked = showVerifiedOnly, onCheckedChange = { showVerifiedOnly = it })
                                Text("Tylko zweryfikowani")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Sortuj według (pokaż rekordzistę):", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(selected = sortBy == "revenue", onClick = { sortBy = "revenue" }, label = { Text("Przychodu") })
                                FilterChip(selected = sortBy == "artworks", onClick = { sortBy = "artworks" }, label = { Text("Dzieł") })
                                FilterChip(selected = sortBy == "rating", onClick = { sortBy = "rating" }, label = { Text("Oceny") })
                                FilterChip(selected = sortBy == "followers", onClick = { sortBy = "followers" }, label = { Text("Fanów") })
                            }
                        }
                    }
                }
                
                Text(
                    "Wyniki: $totalFiltered",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 13.sp, color = Color.Gray
                )
                
                if (filteredSellers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nie znaleziono sprzedawców", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSellers) { seller ->
                            SellerCard(seller = seller, onClick = { onSellerClick(seller) })
                        }
                    }
                }
            }
        }
    }
}

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
        modifier = modifier.height(85.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(label, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SellerCard(seller: SellerInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFFF9800).copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Storefront, null, tint = Color(0xFFFF9800), modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(seller.username, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (seller.isVerified) Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    if (!seller.isActive) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFEBEE)) {
                            Text("Nieaktywny", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFF44336))
                        }
                    }
                }
                Text("${seller.firstName} ${seller.lastName}", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Brush, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text("${seller.activeArtworks}/${seller.totalArtworks}", fontSize = 12.sp, color = Color.Gray)
                    }
                    if (seller.averageRating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Text(String.format("%.1f", seller.averageRating), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFFFD700))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Group, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text("${seller.followerCount}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Przychód: ${formatCurrency(seller.totalRevenue)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

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

private fun AdminSellerResponse.toSellerInfo(): SellerInfo {
    return SellerInfo(
        id = id,
        username = username,
        email = email,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        balance = 0.0,
        registrationDate = createdAt?.replace("T", " ")?.substringBefore(".") ?: "Brak danych",
        isActive = active ?: true,
        lastLogin = null,
        totalArtworks = totalArtworks,
        soldArtworks = 0,
        activeArtworks = totalArtworks,
        totalRevenue = totalRevenue,
        followerCount = followerCount,
        averageRating = averageRating,
        isVerified = verified ?: false
    )
}
