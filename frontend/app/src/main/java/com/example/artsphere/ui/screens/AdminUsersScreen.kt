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
import com.example.artsphere.ui.UserInfo
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran zarządzania użytkownikami dla administratora
 * Wyświetla listę użytkowników z możliwością filtrowania i szczegółowego widoku
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    onBackClick: () -> Unit = {},
    onUserClick: (UserInfo) -> Unit = {}
) {
    val users = remember { MockStatisticsProvider.getMockUsers() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showActiveOnly by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    // Filtrowanie użytkowników
    val filteredUsers = users.filter { user ->
        val matchesSearch = searchQuery.isEmpty() || 
            user.username.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true) ||
            user.firstName.contains(searchQuery, ignoreCase = true) ||
            user.lastName.contains(searchQuery, ignoreCase = true)
        
        val matchesRole = selectedRole == null || user.role == selectedRole
        val matchesActive = !showActiveOnly || user.isActive
        
        matchesSearch && matchesRole && matchesActive
    }
    
    // Statystyki filtrowanych użytkowników
    val totalFiltered = filteredUsers.size
    val activeBuyers = filteredUsers.count { it.role == "BUYER" && it.isActive }
    val activeSellers = filteredUsers.count { it.role == "SELLER" && it.isActive }
    val inactiveUsers = filteredUsers.count { !it.isActive }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Zarządzanie użytkownikami",
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
                            Icons.Default.People,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Baza użytkowników",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Zarządzaj kontami i uprawnieniami użytkowników",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        "💡 Kliknij na statystykę aby filtrować",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            // Statystyki skrócone - klikalne filtry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    value = totalFiltered.toString(),
                    label = "Użytkowników",
                    icon = Icons.Default.People,
                    color = Color(0xFF6650a4),
                    modifier = Modifier.weight(1f),
                    isSelected = selectedRole == null,
                    onClick = { 
                        selectedRole = null
                        showActiveOnly = false
                    }
                )
                QuickStatCard(
                    value = activeBuyers.toString(),
                    label = "Kupujących",
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f),
                    isSelected = selectedRole == "BUYER",
                    onClick = { 
                        selectedRole = "BUYER"
                        showActiveOnly = true
                    }
                )
                QuickStatCard(
                    value = activeSellers.toString(),
                    label = "Sprzedawców",
                    icon = Icons.Default.Storefront,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    isSelected = selectedRole == "SELLER",
                    onClick = { 
                        selectedRole = "SELLER"
                        showActiveOnly = true
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
                placeholder = { Text("Szukaj użytkownika...") },
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
                        
                        // Filtr roli
                        Text("Rola użytkownika:", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedRole == null,
                                onClick = { selectedRole = null },
                                label = { Text("Wszystkie") }
                            )
                            FilterChip(
                                selected = selectedRole == "BUYER",
                                onClick = { selectedRole = if (selectedRole == "BUYER") null else "BUYER" },
                                label = { Text("Kupujący") }
                            )
                            FilterChip(
                                selected = selectedRole == "SELLER",
                                onClick = { selectedRole = if (selectedRole == "SELLER") null else "SELLER" },
                                label = { Text("Sprzedawcy") }
                            )
                            FilterChip(
                                selected = selectedRole == "ADMIN",
                                onClick = { selectedRole = if (selectedRole == "ADMIN") null else "ADMIN" },
                                label = { Text("Admin") }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Filtr statusu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = showActiveOnly,
                                onCheckedChange = { showActiveOnly = it }
                            )
                            Text("Pokaż tylko aktywnych użytkowników")
                        }
                    }
                }
            }
            
            // Informacja o liczbie wyników
            Text(
                "Znaleziono: $totalFiltered ${if (totalFiltered == 1) "użytkownik" else if (totalFiltered < 5) "użytkowników" else "użytkowników"}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color = Color.Gray
            )
            
            // Lista użytkowników
            if (filteredUsers.isEmpty()) {
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
                            "Nie znaleziono użytkowników",
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
                    items(filteredUsers) { user ->
                        UserCard(
                            user = user,
                            onClick = { onUserClick(user) }
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
 * Karta z szybką statystyką - klikalna
 */
@Composable
fun QuickStatCard(
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
 * Karta pojedynczego użytkownika
 */
@Composable
fun UserCard(
    user: UserInfo,
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar z ikoną
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(getRoleColor(user.role).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getRoleIcon(user.role),
                        contentDescription = user.role,
                        tint = getRoleColor(user.role),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Informacje użytkownika
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = user.username,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Status aktywności
                        if (!user.isActive) {
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
                        text = "${user.firstName} ${user.lastName}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rola
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = getRoleColor(user.role).copy(alpha = 0.15f),
                                modifier = Modifier.border(
                                    width = 1.dp,
                                    color = getRoleColor(user.role).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                            ) {
                                Text(
                                    text = getRoleName(user.role),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getRoleColor(user.role)
                                )
                            }
                        }
                        
                        // Saldo
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = "Saldo",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = formatCurrency(user.balance),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50)
                            )
                        }
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
private fun getRoleColor(role: String): Color {
    return when (role) {
        "ADMIN" -> Color(0xFFE94057)
        "SELLER" -> Color(0xFFFF9800)
        "BUYER" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}

private fun getRoleIcon(role: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (role) {
        "ADMIN" -> Icons.Default.Shield
        "SELLER" -> Icons.Default.Storefront
        "BUYER" -> Icons.Default.ShoppingBag
        else -> Icons.Default.Person
    }
}

private fun getRoleName(role: String): String {
    return when (role) {
        "ADMIN" -> "Administrator"
        "SELLER" -> "Sprzedawca"
        "BUYER" -> "Kupujący"
        else -> role
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}
