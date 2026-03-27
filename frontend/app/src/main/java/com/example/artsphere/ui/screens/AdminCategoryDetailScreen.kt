package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.CategoryInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryDetailScreen(
    category: CategoryInfo,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatusClick: () -> Unit,
    onManageSubcategoriesClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE94057),
            Color(0xFF8A2387)
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły kategorii", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Wróć", tint = Color.White)
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nagłówek z ikoną kategorii
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ikona kategorii z kolorem
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(16.dp),
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
                                    .size(48.dp)
                                    .padding(16.dp),
                                tint = Color.White
                            )
                        }
                        
                        Text(
                            category.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Hierarchia (jeśli podkategoria)
                        if (category.parentName != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    category.parentName,
                                    fontSize = 14.sp,
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
                                    fontSize = 14.sp,
                                    color = Color(0xFFE94057)
                                )
                            }
                        }
                        
                        // Slug
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Text(
                                "/${category.slug}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                color = Color(0xFFE94057),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        
                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (category.isActive) Color(0xFF4CAF50) else Color(0xFFF44336)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    if (category.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Text(
                                    if (category.isActive) "Aktywna" else "Nieaktywna",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Podstawowe informacje
            item {
                CategoryInfoSection(
                    title = "PODSTAWOWE INFORMACJE",
                    icon = Icons.Default.Info
                ) {
                    CategoryInfoRow("Nazwa", category.name)
                    CategoryInfoRow("Opis", category.description)
                    CategoryInfoRow("Slug URL", category.slug)
                    CategoryInfoRow("ID kategorii", "#${category.id}")
                    category.color?.let { color ->
                        CategoryInfoRow("Kolor", color, isHighlighted = true)
                    }
                    category.iconName?.let { icon ->
                        CategoryInfoRow("Ikona", icon)
                    }
                }
            }
            
            // Hierarchia
            item {
                CategoryInfoSection(
                    title = "HIERARCHIA",
                    icon = Icons.Default.AccountTree
                ) {
                    if (category.parentId != null) {
                        CategoryInfoRow("Kategoria nadrzędna", category.parentName ?: "Nieznana")
                        CategoryInfoRow("ID nadrzędnej", "#${category.parentId}")
                        CategoryInfoRow("Typ", "Podkategoria")
                    } else {
                        CategoryInfoRow("Typ", "Kategoria główna")
                        CategoryInfoRow("Pozycja", "Kategoria najwyższego poziomu")
                    }
                    CategoryInfoRow("Kolejność wyświetlania", category.displayOrder.toString())
                }
            }
            
            // Statystyki
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFE94057)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "STATYSTYKI",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF666666)
                            )
                        }
                        
                        // Karty statystyk
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CategoryStatColumn(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Brush,
                                value = category.artworkCount.toString(),
                                label = "Dzieła",
                                color = Color(0xFF2196F3)
                            )
                            CategoryStatColumn(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Visibility,
                                value = "N/A",
                                label = "Wyświetlenia",
                                color = Color(0xFF4CAF50)
                            )
                            CategoryStatColumn(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.TrendingUp,
                                value = "N/A",
                                label = "Popularność",
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }
            
            // Daty
            item {
                CategoryInfoSection(
                    title = "HISTORIA",
                    icon = Icons.Default.History
                ) {
                    CategoryInfoRow("Data utworzenia", category.createdDate)
                    CategoryInfoRow("Ostatnia modyfikacja", category.lastModified)
                }
            }
            
            // Akcje administratora
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFE94057)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "AKCJE ADMINISTRATORA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF666666)
                            )
                        }
                        
                        // Przyciski akcji
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2196F3)
                            )
                        ) {
                            Icon(Icons.Default.Edit, "Edytuj kategorię", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edytuj kategorię", fontSize = 16.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { showStatusDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (category.isActive) Color(0xFFF44336) else Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                if (category.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                "Zmień status", 
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (category.isActive) "Dezaktywuj" else "Aktywuj",
                                fontSize = 16.sp
                            )
                        }
                        
                        if (category.parentId == null) {
                            OutlinedButton(
                                onClick = onManageSubcategoriesClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF9C27B0)
                                )
                            ) {
                                Icon(Icons.Default.AccountTree, "Zarządzaj podkategoriami", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Zarządzaj podkategoriami", fontSize = 16.sp)
                            }
                        }
                        
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFF44336)
                            )
                        ) {
                            Icon(Icons.Default.Delete, "Usuń kategorię", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Usuń kategorię", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
    
    // Dialog usuwania kategorii
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336)
                )
            },
            title = { Text("Usuń kategorię") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Czy na pewno chcesz usunąć kategorię \"${category.name}\"?")
                    if (category.artworkCount > 0) {
                        Text(
                            "⚠️ Ta kategoria zawiera ${category.artworkCount} dzieł. Wszystkie zostaną przeniesione do kategorii \"Bez kategorii\".",
                            fontSize = 12.sp,
                            color = Color(0xFFF44336)
                        )
                    }
                    if (category.parentId == null) {
                        Text(
                            "⚠️ Usunięcie kategorii głównej spowoduje również usunięcie wszystkich jej podkategorii.",
                            fontSize = 12.sp,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    // Dialog zmiany statusu
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            icon = {
                Icon(
                    if (category.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = if (category.isActive) Color(0xFFF44336) else Color(0xFF4CAF50)
                )
            },
            title = { 
                Text(if (category.isActive) "Dezaktywuj kategorię" else "Aktywuj kategorię") 
            },
            text = {
                Text(
                    if (category.isActive) {
                        "Czy na pewno chcesz dezaktywować kategorię \"${category.name}\"? Dzieła w tej kategorii nie będą widoczne w wyszukiwaniu."
                    } else {
                        "Czy na pewno chcesz aktywować kategorię \"${category.name}\"?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleStatusClick()
                        showStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category.isActive) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(if (category.isActive) "Dezaktywuj" else "Aktywuj")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun CategoryInfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFE94057)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
            }
            content()
        }
    }
}

@Composable
fun CategoryInfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) Color(0xFFE94057) else Color.Black,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun CategoryStatColumn(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray
            )
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