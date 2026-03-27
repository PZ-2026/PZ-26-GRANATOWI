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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.OrderInfo
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    onBackClick: () -> Unit,
    onOrderClick: (OrderInfo) -> Unit
) {
    val orders = remember { MockStatisticsProvider.getMockOrders() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var selectedPaymentFilter by remember { mutableStateOf("ALL") }
    var selectedStatFilter by remember { mutableStateOf("ALL") }
    var showFilters by remember { mutableStateOf(false) }
    
    // Filtrowanie zamówień
    val filteredOrders = remember(searchQuery, selectedStatusFilter, selectedPaymentFilter, selectedStatFilter) {
        orders.filter { order ->
            val matchesSearch = searchQuery.isEmpty() || 
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.buyerName.contains(searchQuery, ignoreCase = true) ||
                order.sellerName.contains(searchQuery, ignoreCase = true) ||
                order.artworkTitle.contains(searchQuery, ignoreCase = true)
            
            val matchesStatus = when (selectedStatusFilter) {
                "ALL" -> true
                else -> order.status == selectedStatusFilter
            }
            
            val matchesPayment = when (selectedPaymentFilter) {
                "ALL" -> true
                else -> order.paymentStatus == selectedPaymentFilter
            }
            
            val matchesStat = when (selectedStatFilter) {
                "ALL" -> true
                "PENDING" -> order.status == "PENDING"
                "PROCESSING" -> order.status == "PROCESSING"
                "SHIPPED" -> order.status == "SHIPPED"
                "DELIVERED" -> order.status == "DELIVERED"
                "CANCELLED" -> order.status == "CANCELLED"
                else -> true
            }
            
            matchesSearch && matchesStatus && matchesPayment && matchesStat
        }
    }
    
    // Statystyki
    val totalOrders = orders.size
    val pendingOrders = orders.count { it.status == "PENDING" }
    val processingOrders = orders.count { it.status == "PROCESSING" }
    val shippedOrders = orders.count { it.status == "SHIPPED" }
    val deliveredOrders = orders.count { it.status == "DELIVERED" }
    val cancelledOrders = orders.count { it.status == "CANCELLED" }
    val totalRevenue = orders.filter { it.paymentStatus == "PAID" }.sumOf { it.totalAmount }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE94057),
            Color(0xFF8A2387)
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarządzanie zamówieniami", color = Color.White) },
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
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFE94057)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Zamówienia platformy",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Monitoruj i zarządzaj wszystkimi zamówieniami w systemie",
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
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Wszystkich",
                            value = totalOrders.toString(),
                            icon = Icons.Default.ShoppingCart,
                            color = Color(0xFF2196F3),
                            isSelected = selectedStatFilter == "ALL",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "ALL") "ALL" else "ALL"
                            }
                        )
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Oczekujące",
                            value = pendingOrders.toString(),
                            icon = Icons.Default.Schedule,
                            color = Color(0xFFFFA726),
                            isSelected = selectedStatFilter == "PENDING",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "PENDING") "ALL" else "PENDING"
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "W realizacji",
                            value = processingOrders.toString(),
                            icon = Icons.Default.Sync,
                            color = Color(0xFF42A5F5),
                            isSelected = selectedStatFilter == "PROCESSING",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "PROCESSING") "ALL" else "PROCESSING"
                            }
                        )
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Wysłane",
                            value = shippedOrders.toString(),
                            icon = Icons.Default.LocalShipping,
                            color = Color(0xFF9C27B0),
                            isSelected = selectedStatFilter == "SHIPPED",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "SHIPPED") "ALL" else "SHIPPED"
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Dostarczono",
                            value = deliveredOrders.toString(),
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF4CAF50),
                            isSelected = selectedStatFilter == "DELIVERED",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "DELIVERED") "ALL" else "DELIVERED"
                            }
                        )
                        OrderStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Anulowane",
                            value = cancelledOrders.toString(),
                            icon = Icons.Default.Cancel,
                            color = Color(0xFFF44336),
                            isSelected = selectedStatFilter == "CANCELLED",
                            onClick = { 
                                selectedStatFilter = if (selectedStatFilter == "CANCELLED") "ALL" else "CANCELLED"
                            }
                        )
                    }
                    
                    // Karta przychodu
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = if (selectedStatFilter == "ALL") 4.dp else 2.dp,
                        tonalElevation = if (selectedStatFilter == "ALL") 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Całkowity przychód",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    formatCurrency(totalRevenue),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
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
                    placeholder = { Text("Szukaj zamówienia...") },
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
                            
                            // Filtr statusu
                            Text("Status zamówienia:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedStatusFilter == "ALL",
                                    onClick = { selectedStatusFilter = "ALL" },
                                    label = { Text("Wszystkie", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == "PENDING",
                                    onClick = { selectedStatusFilter = "PENDING" },
                                    label = { Text("Oczekujące", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == "PROCESSING",
                                    onClick = { selectedStatusFilter = "PROCESSING" },
                                    label = { Text("W realizacji", fontSize = 12.sp) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedStatusFilter == "SHIPPED",
                                    onClick = { selectedStatusFilter = "SHIPPED" },
                                    label = { Text("Wysłane", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == "DELIVERED",
                                    onClick = { selectedStatusFilter = "DELIVERED" },
                                    label = { Text("Dostarczono", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == "CANCELLED",
                                    onClick = { selectedStatusFilter = "CANCELLED" },
                                    label = { Text("Anulowane", fontSize = 12.sp) }
                                )
                            }
                            
                            Divider()
                            
                            // Filtr płatności
                            Text("Status płatności:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedPaymentFilter == "ALL",
                                    onClick = { selectedPaymentFilter = "ALL" },
                                    label = { Text("Wszystkie", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedPaymentFilter == "PAID",
                                    onClick = { selectedPaymentFilter = "PAID" },
                                    label = { Text("Opłacone", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedPaymentFilter == "PENDING",
                                    onClick = { selectedPaymentFilter = "PENDING" },
                                    label = { Text("Oczekujące", fontSize = 12.sp) }
                                )
                                FilterChip(
                                    selected = selectedPaymentFilter == "REFUNDED",
                                    onClick = { selectedPaymentFilter = "REFUNDED" },
                                    label = { Text("Zwrócone", fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Informacja o liczbie wyników
            item {
                Text(
                    "Znaleziono: ${filteredOrders.size} zamówień",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Lista zamówień
            items(filteredOrders) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderClick(order) }
                )
            }
            
            // Komunikat gdy brak wyników
            if (filteredOrders.isEmpty()) {
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
                                "Nie znaleziono zamówień",
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
fun OrderStatCard(
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
fun OrderCard(
    order: OrderInfo,
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
            // Nagłówek: numer zamówienia i data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFE94057)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        order.orderNumber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    order.orderDate,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Informacje o dziele
            Text(
                order.artworkTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // Kupujący i sprzedawca
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Kupujący:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        order.buyerName,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Sprzedawca:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        order.sellerName,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Dolna sekcja: status, płatność, cena
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = getOrderStatusColor(order.status).copy(alpha = 0.15f)
                    ) {
                        Text(
                            getOrderStatusText(order.status),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOrderStatusColor(order.status)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Status płatności
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = getPaymentStatusColor(order.paymentStatus).copy(alpha = 0.15f)
                    ) {
                        Text(
                            getPaymentStatusText(order.paymentStatus),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = getPaymentStatusColor(order.paymentStatus)
                        )
                    }
                }
                
                // Cena
                Text(
                    formatCurrency(order.totalAmount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE94057)
                )
            }
        }
    }
}

// Funkcje pomocnicze

private fun getOrderStatusColor(status: String): Color {
    return when (status) {
        "PENDING" -> Color(0xFFFFA726)
        "PROCESSING" -> Color(0xFF42A5F5)
        "SHIPPED" -> Color(0xFF9C27B0)
        "DELIVERED" -> Color(0xFF4CAF50)
        "CANCELLED" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

private fun getOrderStatusText(status: String): String {
    return when (status) {
        "PENDING" -> "Oczekujące"
        "PROCESSING" -> "W realizacji"
        "SHIPPED" -> "Wysłane"
        "DELIVERED" -> "Dostarczono"
        "CANCELLED" -> "Anulowane"
        else -> status
    }
}

private fun getPaymentStatusColor(status: String): Color {
    return when (status) {
        "PAID" -> Color(0xFF4CAF50)
        "PENDING" -> Color(0xFFFFA726)
        "REFUNDED" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}

private fun getPaymentStatusText(status: String): String {
    return when (status) {
        "PAID" -> "Opłacone"
        "PENDING" -> "Oczekująca płatność"
        "REFUNDED" -> "Zwrócono"
        else -> status
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return format.format(amount)
}
