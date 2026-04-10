package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.AdminUserResponse
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.ui.UserInfo
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    refreshTrigger: Int = 0,
    onBackClick: () -> Unit = {},
    onUserClick: (UserInfo) -> Unit = {}
) {
    var users by remember { mutableStateOf<List<UserInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showActiveOnly by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = ""
        try {
            val response = RetrofitClient.adminApi.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                users = response.body()!!.map { it.toUserInfo() }
            } else {
                errorMessage = "Nie udało się pobrać użytkowników (${response.code()})"
            }
        } catch (e: Exception) {
            errorMessage = "Błąd połączenia: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val filteredUsers = users.filter { user ->
        val normalizedRole = normalizeRole(user.role)
        val matchesSearch = searchQuery.isEmpty() ||
            user.username.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true) ||
            user.firstName.contains(searchQuery, ignoreCase = true) ||
            user.lastName.contains(searchQuery, ignoreCase = true)

        val matchesRole = selectedRole == null || normalizedRole == selectedRole
        val matchesActive = !showActiveOnly || user.isActive

        matchesSearch && matchesRole && matchesActive
    }

    val totalFiltered = filteredUsers.size
    val activeBuyers = filteredUsers.count { normalizeRole(it.role) == "BUYER" && it.isActive }
    val activeSellers = filteredUsers.count { normalizeRole(it.role) == "ARTIST" && it.isActive }
    val inactiveUsers = filteredUsers.count { !it.isActive }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarządzanie użytkownikami", color = Color.White, fontSize = 18.sp) },
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
                        Icon(Icons.Default.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Baza użytkowników", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Zarządzaj kontami i uprawnieniami użytkowników", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
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
                        isSelected = selectedRole == "ARTIST",
                        onClick = {
                            selectedRole = "ARTIST"
                            showActiveOnly = true
                        }
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj użytkownika...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj") },
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

                if (showFilterMenu) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Filtry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Rola użytkownika:", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(selected = selectedRole == null, onClick = { selectedRole = null }, label = { Text("Wszystkie") })
                                FilterChip(selected = selectedRole == "BUYER", onClick = { selectedRole = if (selectedRole == "BUYER") null else "BUYER" }, label = { Text("Kupujący") })
                                FilterChip(selected = selectedRole == "ARTIST", onClick = { selectedRole = if (selectedRole == "ARTIST") null else "ARTIST" }, label = { Text("Sprzedawcy") })
                                FilterChip(selected = selectedRole == "ADMIN", onClick = { selectedRole = if (selectedRole == "ADMIN") null else "ADMIN" }, label = { Text("Admin") })
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material3.Checkbox(
                                    checked = showActiveOnly,
                                    onCheckedChange = { showActiveOnly = it }
                                )
                                Text("Pokaż tylko aktywnych użytkowników")
                            }
                        }
                    }
                }

                Text(
                    "Znaleziono: $totalFiltered użytkowników (nieaktywnych: $inactiveUsers)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                if (filteredUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Nie znaleziono użytkowników", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredUsers) { user ->
                            UserCard(user = user, onClick = { onUserClick(user) })
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

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
        modifier = modifier.height(85.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = label, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = user.username, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!user.isActive) {
                            androidx.compose.material3.Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFEBEE)) {
                                Text(text = "Nieaktywny", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFF44336))
                            }
                        }
                    }

                    Text(text = "${user.firstName} ${user.lastName}", fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getRoleColor(user.role).copy(alpha = 0.15f),
                            modifier = Modifier.border(1.dp, getRoleColor(user.role).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = getRoleName(user.role),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = getRoleColor(user.role)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.AccountBalance, contentDescription = "Saldo", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                            Text(text = formatCurrency(user.balance), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }

            IconButton(onClick = onClick) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Szczegóły", tint = Color.Gray)
            }
        }
    }
}

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

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amount)
}

private fun normalizeRole(role: String): String {
    return if (role.equals("SELLER", ignoreCase = true)) "ARTIST" else role.uppercase()
}

private fun AdminUserResponse.toUserInfo(): UserInfo {
    return UserInfo(
        id = id,
        username = username,
        email = email,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        role = normalizeRole(role),
        balance = balance,
        registrationDate = createdAt?.replace("T", " ")?.substringBefore(".") ?: "Brak danych",
        isActive = active ?: true,
        lastLogin = null
    )
}
