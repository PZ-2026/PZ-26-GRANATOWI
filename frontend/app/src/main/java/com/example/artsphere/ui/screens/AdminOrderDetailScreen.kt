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
import com.example.artsphere.ui.OrderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    order: OrderInfo,
    onBackClick: () -> Unit,
    onChangeStatusClick: (String) -> Unit,
    onCancelOrderClick: () -> Unit,
    onSendMessageClick: () -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedNewStatus by remember { mutableStateOf(order.status) }
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE94057),
            Color(0xFF8A2387)
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły zamówienia", color = Color.White) },
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
            // Nagłówek z numerem zamówienia i statusem
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
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFE94057)
                        )
                        Text(
                            order.orderNumber,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            order.orderDate,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = getOrderStatusColor(order.status)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    getOrderStatusIcon(order.status),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Text(
                                    getOrderStatusText(order.status),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Informacje o zamówieniu
            item {
                OrderInfoSection(
                    title = "INFORMACJE O ZAMÓWIENIU",
                    icon = Icons.Default.ShoppingCart
                ) {
                    OrderInfoRow("Dzieło", order.artworkTitle)
                    OrderInfoRow("ID dzieła", "#${order.artworkId}")
                    OrderInfoRow("Ilość", order.quantity.toString())
                    OrderInfoRow("Cena jednostkowa", formatCurrency(order.unitPrice))
                    OrderInfoRow("Całkowita kwota", formatCurrency(order.totalAmount), isHighlighted = true)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    OrderInfoRow("Status płatności", getPaymentStatusText(order.paymentStatus))
                    OrderInfoRow("Metoda płatności", order.paymentMethod)
                }
            }
            
            // Kupujący
            item {
                OrderInfoSection(
                    title = "KUPUJĄCY",
                    icon = Icons.Default.Person
                ) {
                    OrderInfoRow("Imię i nazwisko", order.buyerName)
                    OrderInfoRow("ID kupującego", "#${order.buyerId}")
                    OrderInfoRow("Email", order.buyerEmail)
                }
            }
            
            // Sprzedawca
            item {
                OrderInfoSection(
                    title = "SPRZEDAWCA",
                    icon = Icons.Default.Storefront
                ) {
                    OrderInfoRow("Imię i nazwisko", order.sellerName)
                    OrderInfoRow("ID sprzedawcy", "#${order.sellerId}")
                }
            }
            
            // Adres dostawy
            item {
                OrderInfoSection(
                    title = "ADRES DOSTAWY",
                    icon = Icons.Default.LocationOn
                ) {
                    OrderInfoRow("Ulica", order.shippingAddress)
                    OrderInfoRow("Miasto", order.shippingCity)
                    OrderInfoRow("Kod pocztowy", order.shippingPostalCode)
                    OrderInfoRow("Kraj", order.shippingCountry)
                }
            }
            
            // Informacje o dostawie
            item {
                OrderInfoSection(
                    title = "DOSTAWA",
                    icon = Icons.Default.LocalShipping
                ) {
                    order.trackingNumber?.let {
                        OrderInfoRow("Numer śledzenia", it)
                    }
                    order.estimatedDelivery?.let {
                        OrderInfoRow("Przewidywana dostawa", it)
                    }
                    order.actualDelivery?.let {
                        OrderInfoRow("Rzeczywista dostawa", it)
                    }
                    if (order.trackingNumber == null && order.estimatedDelivery == null && order.actualDelivery == null) {
                        Text(
                            "Brak informacji o dostawie",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Notatki
            if (order.notes != null) {
                item {
                    OrderInfoSection(
                        title = "NOTATKI",
                        icon = Icons.Default.Note
                    ) {
                        Text(
                            order.notes,
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Historia statusu
            item {
                OrderInfoSection(
                    title = "HISTORIA STATUSU",
                    icon = Icons.Default.History
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusHistoryItem(
                            status = "PENDING",
                            date = order.orderDate,
                            isActive = order.status == "PENDING"
                        )
                        if (order.status in listOf("PROCESSING", "SHIPPED", "DELIVERED")) {
                            StatusHistoryItem(
                                status = "PROCESSING",
                                date = order.orderDate, // Placeholder - w prawdziwej aplikacji byłyby różne daty
                                isActive = order.status == "PROCESSING"
                            )
                        }
                        if (order.status in listOf("SHIPPED", "DELIVERED")) {
                            StatusHistoryItem(
                                status = "SHIPPED",
                                date = order.orderDate,
                                isActive = order.status == "SHIPPED"
                            )
                        }
                        if (order.status == "DELIVERED") {
                            StatusHistoryItem(
                                status = "DELIVERED",
                                date = order.actualDelivery ?: order.orderDate,
                                isActive = true
                            )
                        }
                        if (order.status == "CANCELLED") {
                            StatusHistoryItem(
                                status = "CANCELLED",
                                date = order.orderDate,
                                isActive = true
                            )
                        }
                    }
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
                            onClick = { showStatusDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2196F3)
                            )
                        ) {
                            Icon(Icons.Default.Edit, "Zmień status", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Zmień status", fontSize = 16.sp)
                        }
                        
                        OutlinedButton(
                            onClick = onSendMessageClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Message, "Wyślij wiadomość", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Wyślij wiadomość", fontSize = 16.sp)
                        }
                        
                        if (order.status != "CANCELLED") {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFF44336)
                                )
                            ) {
                                Icon(Icons.Default.Cancel, "Anuluj zamówienie", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Anuluj zamówienie", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialog zmiany statusu
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            icon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF2196F3)
                )
            },
            title = { Text("Zmień status zamówienia") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Wybierz nowy status dla zamówienia ${order.orderNumber}:")
                    
                    listOf("PENDING", "PROCESSING", "SHIPPED", "DELIVERED").forEach { status ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedNewStatus == status,
                                onClick = { selectedNewStatus = status }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(getOrderStatusText(status))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onChangeStatusClick(selectedNewStatus)
                        showStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Zmień")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    // Dialog anulowania zamówienia
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336)
                )
            },
            title = { Text("Anuluj zamówienie") },
            text = {
                Text("Czy na pewno chcesz anulować zamówienie ${order.orderNumber}? Ta akcja nie może być cofnięta.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelOrderClick()
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Anuluj zamówienie")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Wróć")
                }
            }
        )
    }
}

@Composable
fun OrderInfoSection(
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
fun OrderInfoRow(
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
fun StatusHistoryItem(
    status: String,
    date: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isActive) getOrderStatusColor(status) else Color.LightGray
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                getOrderStatusText(status),
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) Color.Black else Color.Gray
            )
            Text(
                date,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}

fun getOrderStatusIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        "PENDING" -> Icons.Default.Schedule
        "PROCESSING" -> Icons.Default.Sync
        "SHIPPED" -> Icons.Default.LocalShipping
        "DELIVERED" -> Icons.Default.CheckCircle
        "CANCELLED" -> Icons.Default.Cancel
        else -> Icons.Default.Help
    }
}

// Funkcje pomocnicze dla AdminOrderDetailScreen

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

private fun getPaymentStatusText(status: String): String {
    return when (status) {
        "PAID" -> "Opłacone"
        "PENDING" -> "Oczekująca płatność"
        "REFUNDED" -> "Zwrócono"
        else -> status
    }
}

private fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pl", "PL"))
    return format.format(amount)
}
