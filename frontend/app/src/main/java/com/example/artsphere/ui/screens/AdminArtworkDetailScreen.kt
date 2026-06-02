package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.ui.ArtworkInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran szczegółów dzieła dla administratora
 * Wyświetla pełne informacje o dziele i dostępne akcje
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminArtworkDetailScreen(
    artwork: ArtworkInfo,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onChangeStatusClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Szczegóły dzieła",
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
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edytuj",
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
                .verticalScroll(scrollState)
        ) {
            // Nagłówek z obrazem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                if (artwork.imagePath.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Zdjęcie dzieła",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    val displayImage = if (artwork.imagePath.startsWith("uploads/")) 
                        RetrofitClient.BASE_URL + artwork.imagePath 
                    else artwork.imagePath

                    AsyncImage(
                        model = displayImage,
                        contentDescription = artwork.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }
            
            // Status badge na górze
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = getStatusColor(artwork.status).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                getStatusIcon(artwork.status),
                                contentDescription = "Status",
                                tint = getStatusColor(artwork.status),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = getStatusName(artwork.status),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = getStatusColor(artwork.status)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = artwork.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    if (artwork.artist != null) {
                        Text(
                            text = artwork.artist,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height((-10).dp))
            
            // Cena
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cena",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = if (artwork.isPriceless) "Bezcenne" else formatCurrency(artwork.price ?: 0.0),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (artwork.isPriceless) Color(0xFFFF9800) else Color(0xFF4CAF50)
                        )
                    }
                    
                    if (artwork.isPriceless) {
                        Icon(
                            Icons.Default.Diamond,
                            contentDescription = "Bezcenne",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Informacje podstawowe
            ArtworkInfoSection(title = "Informacje podstawowe") {
                ArtworkInfoRow(
                    icon = Icons.Default.Badge,
                    label = "ID dzieła",
                    value = "#${artwork.id}"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ArtworkInfoRow(
                    icon = Icons.Default.Category,
                    label = "Kategoria",
                    value = artwork.category ?: "Brak kategorii"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ArtworkInfoRow(
                    icon = Icons.Default.Person,
                    label = "Sprzedawca",
                    value = artwork.sellerUsername
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ArtworkInfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Data dodania",
                    value = artwork.createdAt
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Wymiary
            if (artwork.width != null || artwork.height != null || artwork.depth != null) {
                ArtworkInfoSection(title = "Wymiary") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (artwork.width != null) {
                            DimensionCard("Szerokość", artwork.width, "cm")
                        }
                        if (artwork.height != null) {
                            DimensionCard("Wysokość", artwork.height, "cm")
                        }
                        if (artwork.depth != null) {
                            DimensionCard("Głębokość", artwork.depth, "cm")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Opis
            if (artwork.description != null) {
                ArtworkInfoSection(title = "Opis dzieła") {
                    Text(
                        text = artwork.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Statystyki
            ArtworkInfoSection(title = "Statystyki") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        icon = Icons.Default.Visibility,
                        value = artwork.views.toString(),
                        label = "Wyświetleń",
                        color = Color(0xFF2196F3)
                    )
                    StatCard(
                        icon = if (artwork.isSold) Icons.Default.CheckCircle else Icons.Default.ShoppingCart,
                        value = if (artwork.isSold) "Tak" else "Nie",
                        label = "Sprzedane",
                        color = if (artwork.isSold) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Akcje administratora
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Akcje administratora",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Przycisk zmiany statusu
                    OutlinedButton(
                        onClick = { showStatusDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Icon(Icons.Default.ChangeCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zmień status dzieła")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Przycisk edycji
                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edytuj dane dzieła")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Przycisk usunięcia
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Usuń dzieło")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Dialog potwierdzenia usunięcia
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336)) },
            title = { Text("Potwierdź usunięcie") },
            text = { 
                Text("Czy na pewno chcesz usunąć dzieło \"${artwork.title}\"? Ta operacja jest nieodwracalna.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
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
            icon = { Icon(Icons.Default.ChangeCircle, contentDescription = null) },
            title = { Text("Zmień status dzieła") },
            text = {
                Column {
                    Text("Wybierz nowy status dla dzieła \"${artwork.title}\":")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    listOf(
                        Triple("AVAILABLE", "Dostępne", Icons.Default.Visibility),
                        Triple("SOLD", "Sprzedane", Icons.Default.Sell),
                        Triple("HIDDEN", "Ukryte", Icons.Default.VisibilityOff)
                    ).forEach { (status, name, icon) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showStatusDialog = false
                                    onChangeStatusClick(status)
                                }
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = if (artwork.status == status) 
                                    getStatusColor(status).copy(alpha = 0.1f) 
                                else 
                                    Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (artwork.status == status) getStatusColor(status) else Color.LightGray
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = name,
                                        tint = getStatusColor(status),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        name,
                                        fontWeight = if (artwork.status == status) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

/**
 * Sekcja z informacjami
 */
@Composable
fun ArtworkInfoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

/**
 * Wiersz z informacją o dziele
 */
@Composable
fun ArtworkInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Karta wymiaru
 */
@Composable
fun DimensionCard(label: String, value: Double, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6650a4)
        )
        Text(
            text = unit,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * Karta statystyki
 */
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
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

private fun getStatusIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        "AVAILABLE" -> Icons.Default.Visibility
        "SOLD" -> Icons.Default.Sell
        "HIDDEN" -> Icons.Default.VisibilityOff
        else -> Icons.Default.Help
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
