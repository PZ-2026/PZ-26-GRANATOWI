package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.CategoryInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (CategoryInfo) -> Unit
) {
    val categories = remember { MockStatisticsProvider.getMockCategories() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var selectedStatFilter by remember { mutableStateOf("ALL") }
    var showFilters by remember { mutableStateOf(false) }
    
    // Filtrowanie kategorii
    val filteredCategories = remember(searchQuery, selectedTypeFilter, selectedStatFilter) {
        categories.filter { category ->
            val matchesSearch = searchQuery.isEmpty() || 
                category.name.contains(searchQuery, ignoreCase = true) ||
                category.description.contains(searchQuery, ignoreCase = true) ||
                category.slug.contains(searchQuery, ignoreCase = true)
            
            val matchesType = when (selectedTypeFilter) {
                "ALL" -> true
                "PARENT" -> category.parentId == null
                "SUBCATEGORY" -> category.parentId != null
                else -> true
            }
            
            val matchesStat = when (selectedStatFilter) {
                "ALL" -> true
                "ACTIVE" -> category.isActive
                "INACTIVE" -> !category.isActive
                "PARENT" -> category.parentId == null
                "SUBCATEGORY" -> category.parentId != null
                else -> true
            }
            
            matchesSearch && matchesType && matchesStat
        }
    }
    
    // Statystyki
    val totalCategories = categories.size
    val activeCategories = categories.count { it.isActive }
    val inactiveCategories = categories.count { !it.isActive }
    val parentCategories = categories.count { it.parentId == null }
    val subcategories = categories.count { it.parentId != null }
    val totalArtworks = categories.sumOf { it.artworkCount }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE94057),
            Color(0xFF8A2387)
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarządzanie kategoriami", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Wróć", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            if (showFilters) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
                            "Filtry",
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Nagłówek z ikoną
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFE94057)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Kategorie sztuki",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Zarządzaj kategoriami i organizuj dzieła sztuki w systemie",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "💡 Kliknij na statystykę aby filtrować",
                            fontSize = 12.sp,
                            color = Color(0xFFE94057)
                        )
                    }
                }
            }
            
            // Karty statystyk (klikalne)
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Wszystkich",
                            value = totalCategories.toString(),
                            icon = Icons.Default.Category,
                            color = Color(0xFF2196F3),
                            isSelected = selectedStatFilter == "ALL",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "ALL") "ALL" else "ALL"
                            }
                        )
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Aktywnych",
                            value = activeCategories.toString(),
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF4CAF50),
                            isSelected = selectedStatFilter == "ACTIVE",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "ACTIVE") "ALL" else "ACTIVE"
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Nieaktywnych",
                            value = inactiveCategories.toString(),
                            icon = Icons.Default.Cancel,
                            color = Color(0xFFF44336),
                            isSelected = selectedStatFilter == "INACTIVE",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "INACTIVE") "ALL" else "INACTIVE"
                            }
                        )
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Głównych",
                            value = parentCategories.toString(),
                            icon = Icons.Default.AccountTree,
                            color = Color(0xFF9C27B0),
                            isSelected = selectedStatFilter == "PARENT",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "PARENT") "ALL" else "PARENT"
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Podkategorii",
                            value = subcategories.toString(),
                            icon = Icons.Default.SubdirectoryArrowRight,
                            color = Color(0xFFFF9800),
                            isSelected = selectedStatFilter == "SUBCATEGORY",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "SUBCATEGORY") "ALL" else "SUBCATEGORY"
                            }
                        )
                        CategoryStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Dzieł",
                            value = totalArtworks.toString(),
                            icon = Icons.Default.Brush,
                            color = Color(0xFF607D8B),
                            isSelected = false,
                            onClick = { }
                        )
                    }
                }
            }
            
            // Pasek wyszukiwania
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj kategorii...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Szukaj") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Wyczyść")
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
            }
            
            // Panel filtrów (rozwijany)
            if (showFilters) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Filtry",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Filtr typu
                            Text("Typ kategorii:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedTypeFilter == "ALL",
                                    onClick = { selectedTypeFilter = "ALL" },
                                    label = { Text("Wszystkie", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedTypeFilter == "PARENT",
                                    onClick = { selectedTypeFilter = "PARENT" },
                                    label = { Text("Główne", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedTypeFilter == "SUBCATEGORY",
                                    onClick = { selectedTypeFilter = "SUBCATEGORY" },
                                    label = { Text("Podkategorie", fontSize = 12.sp) }
                                )
                            }
                            
                            Divider()
                            
                            // Filtr statusu
                            Text("Status:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedStatFilter == "ALL",
                                    onClick = { selectedStatFilter = "ALL" },
                                    label = { Text("Wszystkie", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatFilter == "ACTIVE",
                                    onClick = { selectedStatFilter = "ACTIVE" },
                                    label = { Text("Aktywne", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatFilter == "INACTIVE",
                                    onClick = { selectedStatFilter = "INACTIVE" },
                                    label = { Text("Nieaktywne", fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Informacja o liczbie wyników
            item {
                Text(
                    "Znaleziono: ${filteredCategories.size} kategorii",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Lista kategorii
            items(filteredCategories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
            
            // Komunikat gdy brak wyników
            if (filteredCategories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Text(
                                "Nie znaleziono kategorii",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            if (searchQuery.isNotEmpty()) {
                                Text(
                                    "Spróbuj zmienić kryteria wyszukiwania",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(85.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) color.copy(alpha = 0.1f) else Color.White,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null,
            shadowElevation = if (isSelected) 4.dp else 2.dp,
            tonalElevation = if (isSelected) 2.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                }
                Column {
                    Text(
                        value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        title,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryInfo,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Nagłówek z nazwą i hierarchią
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ikona kategorii z kolorem
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (category.color != null) {
                        try {
                            Color(android.graphics.Color.parseColor(category.color))
                        } catch (e: Exception) {
                            Color(0xFF2196F3)
                        }
                    } else Color(0xFF2196F3)
                ) {
                    Icon(
                        getCategoryIcon(category.iconName),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(8.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // Hierarchia (jeśli podkategoria)
                    if (category.parentName != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                category.parentName,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Text(
                                category.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Slug
                    Text(
                        "/${category.slug}",
                        fontSize = 12.sp,
                        color = Color(0xFFE94057),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                // Status badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (category.isActive) Color(0xFF4CAF50) else Color(0xFFF44336)
                ) {
                    Text(
                        if (category.isActive) "Aktywna" else "Nieaktywna",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Opis
            Text(
                category.description,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Dolna sekcja: statystyki
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Liczba dzieł
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Brush,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${category.artworkCount} dzieł",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // Typ kategorii
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (category.parentId == null) Icons.Default.AccountTree else Icons.Default.SubdirectoryArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (category.parentId == null) "Główna" else "Podkategoria",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // Data modyfikacji
                Text(
                    category.lastModified,
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

// Funkcje pomocnicze

private fun getCategoryIcon(iconName: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "palette" -> Icons.Default.Palette
        "brush" -> Icons.Default.Brush
        "water_drop" -> Icons.Default.WaterDrop
        "sculpture" -> Icons.Default.Architecture
        "precision_manufacturing" -> Icons.Default.Handyman
        "camera_alt" -> Icons.Default.CameraAlt
        "portrait" -> Icons.Default.Portrait
        "auto_fix_high" -> Icons.Default.AutoFixHigh
        "computer" -> Icons.Default.Computer
        "draw" -> Icons.Default.Draw
        "museum" -> Icons.Default.Museum
        else -> Icons.Default.Category
    }
}