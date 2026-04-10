package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.artsphere.ui.UserInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran szczegółów użytkownika dla administratora
 * Wyświetla pełne informacje o użytkowniku i dostępne akcje
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    user: UserInfo,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onToggleStatusClick: () -> Unit = {},
    onChangeRoleClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showToggleStatusDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(normalizeRole(user.role)) }
    LaunchedEffect(user.id, user.role) {
        selectedRole = normalizeRole(user.role)
    }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Szczegóły użytkownika",
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
            // Nagłówek użytkownika
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
                            imageVector = getRoleIcon(user.role),
                            contentDescription = user.role,
                            tint = getRoleColor(user.role),
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = user.username,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (user.isActive) 
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
                                        if (user.isActive) Color(0xFF4CAF50) 
                                        else Color(0xFFF44336)
                                    )
                            )
                            Text(
                                text = if (user.isActive) "Konto aktywne" else "Konto nieaktywne",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informacje podstawowe
            InfoSection(title = "Informacje podstawowe") {
                InfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = user.email
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    icon = Icons.Default.Badge,
                    label = "ID użytkownika",
                    value = "#${user.id}"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Imię",
                    value = user.firstName
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Nazwisko",
                    value = user.lastName
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rola i uprawnienia
            InfoSection(title = "Rola i uprawnienia") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = getRoleIcon(user.role),
                            contentDescription = "Rola",
                            tint = getRoleColor(user.role),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Rola w systemie",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = getRoleName(user.role),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = getRoleColor(user.role)
                            )
                        }
                    }
                    
                    OutlinedButton(
                        onClick = { showRoleDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getRoleColor(user.role)
                        )
                    ) {
                        Text("Zmień rolę")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informacje finansowe
            InfoSection(title = "Informacje finansowe") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = "Saldo",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Saldo konta",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = formatCurrency(user.balance),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Aktywność
            InfoSection(title = "Aktywność") {
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Data rejestracji",
                    value = user.registrationDate
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Ostatnie logowanie",
                    value = user.lastLogin ?: "Brak danych"
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
                    
                    // Przycisk zmiany statusu
                    OutlinedButton(
                        onClick = { showToggleStatusDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (user.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (user.isActive) "Dezaktywuj konto" else "Aktywuj konto")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Przycisk edycji
                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edytuj dane użytkownika")
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
                        Text("Usuń użytkownika")
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
                Text("Czy na pewno chcesz usunąć użytkownika ${user.username}? Ta operacja jest nieodwracalna.")
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
    if (showToggleStatusDialog) {
        AlertDialog(
            onDismissRequest = { showToggleStatusDialog = false },
            icon = { 
                Icon(
                    if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (user.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                ) 
            },
            title = { Text(if (user.isActive) "Dezaktywuj konto" else "Aktywuj konto") },
            text = { 
                Text(
                    if (user.isActive)
                        "Czy chcesz dezaktywować konto użytkownika ${user.username}? Użytkownik nie będzie mógł się zalogować."
                    else
                        "Czy chcesz aktywować konto użytkownika ${user.username}? Użytkownik będzie mógł ponownie korzystać z platformy."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showToggleStatusDialog = false
                        onToggleStatusClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.isActive) Color(0xFFF57C00) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(if (user.isActive) "Dezaktywuj" else "Aktywuj")
                }
            },
            dismissButton = {
                TextButton(onClick = { showToggleStatusDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    // Dialog zmiany roli
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
            title = { Text("Zmień rolę użytkownika") },
            text = {
                Column {
                    Text("Wybierz nową rolę dla użytkownika ${user.username}:")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                     listOf(
                         Triple("BUYER", "Kupujący", Icons.Default.ShoppingBag),
                        Triple("ARTIST", "Sprzedawca", Icons.Default.Storefront),
                         Triple("ADMIN", "Administrator", Icons.Default.Shield)
                     ).forEach { (role, name, icon) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedRole = role },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedRole == role)
                                getRoleColor(role).copy(alpha = 0.1f) 
                            else 
                                Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (selectedRole == role) getRoleColor(role) else Color.LightGray
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
                                    tint = getRoleColor(role),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    name,
                                    fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showRoleDialog = false
                    onChangeRoleClick(selectedRole)
                }) {
                    Text("Zmień rolę")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) {
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
fun InfoSection(
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
 * Wiersz z informacją
 */
@Composable
fun InfoRow(
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
 * Pomocnicze funkcje (współdzielone z AdminUsersScreen)
 */
private fun getRoleColor(role: String): Color {
    return when (normalizeRole(role)) {
        "ADMIN" -> Color(0xFFE94057)
        "ARTIST" -> Color(0xFFFF9800)
        "BUYER" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}

private fun getRoleIcon(role: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (normalizeRole(role)) {
        "ADMIN" -> Icons.Default.Shield
        "ARTIST" -> Icons.Default.Storefront
        "BUYER" -> Icons.Default.ShoppingBag
        else -> Icons.Default.Person
    }
}

private fun getRoleName(role: String): String {
    return when (normalizeRole(role)) {
        "ADMIN" -> "Administrator"
        "ARTIST" -> "Sprzedawca"
        "BUYER" -> "Kupujący"
        else -> role
    }
}

private fun normalizeRole(role: String): String {
    return if (role.equals("SELLER", ignoreCase = true)) "ARTIST" else role.uppercase()
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}
