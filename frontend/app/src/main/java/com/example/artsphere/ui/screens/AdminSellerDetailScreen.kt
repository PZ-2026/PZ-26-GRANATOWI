package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.artsphere.ui.SellerInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran szczegółów sprzedawcy dla administratora
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSellerDetailScreen(
    seller: SellerInfo,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onToggleStatusClick: () -> Unit = {},
    onToggleVerificationClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Szczegóły sprzedawcy",
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
            // Nagłówek sprzedawcy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(adminGradient)
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Sprzedawca",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = seller.username,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        if (seller.isVerified) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Zweryfikowany",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "${seller.firstName} ${seller.lastName}",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (seller.isActive) 
                            Color(0xFF4CAF50).copy(alpha = 0.2f) 
                        else 
                            Color(0xFFF44336).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (seller.isActive) Color(0xFF4CAF50) 
                                        else Color(0xFFF44336)
                                    )
                            )
                            Text(
                                text = if (seller.isActive) "Aktywny" else "Nieaktywny",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statystyki sprzedaży
            SellerInfoSection(title = "Statystyki sprzedaży") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SellerStatColumn(
                        icon = Icons.Default.AttachMoney,
                        value = formatCurrency(seller.totalRevenue),
                        label = "Całkowity przychód",
                        color = Color(0xFF4CAF50)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SellerStatColumn(
                        icon = Icons.Default.Brush,
                        value = "${seller.activeArtworks}/${seller.totalArtworks}",
                        label = "Aktywne/Wszystkie",
                        color = Color(0xFF6650a4)
                    )
                    SellerStatColumn(
                        icon = Icons.Default.ShoppingCart,
                        value = seller.soldArtworks.toString(),
                        label = "Sprzedane",
                        color = Color(0xFFFF9800)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Społeczność
            SellerInfoSection(title = "Społeczność") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SellerStatColumn(
                        icon = Icons.Default.Group,
                        value = seller.followerCount.toString(),
                        label = "Obserwujący",
                        color = Color(0xFF2196F3)
                    )
                    SellerStatColumn(
                        icon = Icons.Default.Star,
                        value = if (seller.averageRating > 0) String.format("%.1f", seller.averageRating) else "Brak",
                        label = "Średnia ocena",
                        color = Color(0xFFFFD700)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informacje podstawowe
            SellerInfoSection(title = "Informacje podstawowe") {
                SellerInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = seller.email
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SellerInfoRow(
                    icon = Icons.Default.Badge,
                    label = "ID sprzedawcy",
                    value = "#${seller.id}"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SellerInfoRow(
                    icon = Icons.Default.AccountBalance,
                    label = "Saldo konta",
                    value = formatCurrency(seller.balance)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Aktywność
            SellerInfoSection(title = "Aktywność") {
                SellerInfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Data rejestracji",
                    value = seller.registrationDate
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SellerInfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Ostatnie logowanie",
                    value = seller.lastLogin ?: "Brak danych"
                )
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
                    
                    // Weryfikacja
                    OutlinedButton(
                        onClick = { showVerificationDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (seller.isVerified) Color(0xFFFF9800) else Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            if (seller.isVerified) Icons.Default.Unpublished else Icons.Default.Verified,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (seller.isVerified) "Cofnij weryfikację" else "Zweryfikuj sprzedawcę")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Status
                    OutlinedButton(
                        onClick = { showStatusDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (seller.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            if (seller.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (seller.isActive) "Dezaktywuj konto" else "Aktywuj konto")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Edycja
                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edytuj dane sprzedawcy")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Usunięcie
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Usuń sprzedawcę")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Dialogi
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336)) },
            title = { Text("Potwierdź usunięcie") },
            text = { 
                Text("Czy na pewno chcesz usunąć sprzedawcę ${seller.username}? Wszystkie jego dzieła również zostaną usunięte. Ta operacja jest nieodwracalna.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
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
    
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            icon = { 
                Icon(
                    if (seller.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (seller.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                ) 
            },
            title = { Text(if (seller.isActive) "Dezaktywuj konto" else "Aktywuj konto") },
            text = { 
                Text(
                    if (seller.isActive)
                        "Czy chcesz dezaktywować konto sprzedawcy ${seller.username}? Sprzedawca nie będzie mógł się zalogować, a jego dzieła zostaną ukryte."
                    else
                        "Czy chcesz aktywować konto sprzedawcy ${seller.username}? Sprzedawca będzie mógł ponownie korzystać z platformy."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStatusDialog = false
                        onToggleStatusClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (seller.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(if (seller.isActive) "Dezaktywuj" else "Aktywuj")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationDialog = false },
            icon = { 
                Icon(
                    if (seller.isVerified) Icons.Default.Unpublished else Icons.Default.Verified,
                    contentDescription = null,
                    tint = if (seller.isVerified) Color(0xFFFF9800) else Color(0xFF4CAF50)
                ) 
            },
            title = { Text(if (seller.isVerified) "Cofnij weryfikację" else "Zweryfikuj sprzedawcę") },
            text = { 
                Text(
                    if (seller.isVerified)
                        "Czy chcesz cofnąć weryfikację sprzedawcy ${seller.username}? Ikona weryfikacji zostanie usunięta."
                    else
                        "Czy chcesz zweryfikować sprzedawcę ${seller.username}? Sprzedawca otrzyma zieloną ikonę weryfikacji."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVerificationDialog = false
                        onToggleVerificationClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (seller.isVerified) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(if (seller.isVerified) "Cofnij" else "Zweryfikuj")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerificationDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun SellerInfoSection(
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

@Composable
fun SellerInfoRow(
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

@Composable
fun SellerStatColumn(
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

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}
