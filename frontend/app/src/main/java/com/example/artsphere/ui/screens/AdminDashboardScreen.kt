package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.artsphere.api.AdminDashboardStatsDto
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.ui.components.StatMetricCard
import com.example.artsphere.ui.components.StatMetricCardCompact
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran statystyk dla administratora
 * Wyświetla statystyki całej platformy ArtSphere
 */
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit
) {
    var statistics by remember { mutableStateOf<AdminDashboardStatsDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.adminApi.getAdminDashboardStatistics()
                
                if (response.isSuccessful) {
                    statistics = response.body()
                    errorMessage = null
                } else {
                    errorMessage = "Błąd podczas ładowania danych: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Błąd: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header z gradientem (zgodny z AdminPanelScreen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE94057),
                            Color(0xFF8A2387)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Statystyki Platformy",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Pełny przegląd aktywności ArtSphere",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(start = 56.dp)
                )
            }
        }
        
        // Content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Błąd",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Błąd podczas ładowania danych",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.getAdminDashboardStatistics()
                                    
                                    if (response.isSuccessful) {
                                        statistics = response.body()
                                        errorMessage = null
                                    } else {
                                        errorMessage = "Błąd: ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Błąd: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
                statistics != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Sekcja: Użytkownicy
                        SectionTitle("Użytkownicy")
                        UserStatisticsGrid(statistics!!)
                        
                        // Sekcja: Transakcje
                        SectionTitle("Transakcje i Przychody")
                        TransactionStatisticsGrid(statistics!!)
                        
                        // Sekcja: Dzieła Sztuki
                        SectionTitle("Dzieła Sztuki")
                        ArtworkStatisticsGrid(statistics!!)
                        
                        // Sekcja: Zamówienia
                        SectionTitle("Zamówienia")
                        OrderStatisticsRow(statistics!!)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                else -> {
                    Text(
                        text = "Brak danych",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun UserStatisticsGrid(stats: AdminDashboardStatsDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalUsers.toString(),
            label = "Wszyscy użytkownicy",
            icon = Icons.Default.People,
            modifier = Modifier.weight(1f),
            useGradient = true,
            gradientColors = listOf(Color(0xFF6650a4), Color(0xFF9C27B0))
        )
        
        StatMetricCard(
            value = "+${stats.newUsersThisMonth}",
            label = "Nowi w tym miesiącu",
            icon = Icons.Default.PersonAdd,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF4CAF50),
            valueColor = Color(0xFF4CAF50)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalSellers.toString(),
            label = "Sprzedawcy",
            icon = Icons.Default.Store,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF3E0),
            iconColor = Color(0xFFFF9800),
            valueColor = Color(0xFFFF9800)
        )
        
        StatMetricCard(
            value = stats.totalBuyers.toString(),
            label = "Kupujący",
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE3F2FD),
            iconColor = Color(0xFF2196F3),
            valueColor = Color(0xFF2196F3)
        )
    }
}

@Composable
private fun TransactionStatisticsGrid(stats: AdminDashboardStatsDto) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
        maximumFractionDigits = 0
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = formatter.format(stats.totalTransactionValue),
            label = "Całkowita wartość transakcji",
            icon = Icons.Default.AttachMoney,
            modifier = Modifier.weight(1f),
            useGradient = true,
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = formatter.format(stats.averageOrderValue),
            label = "Średnia wartość zamówienia",
            icon = Icons.Default.TrendingUp,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFCE4EC),
            iconColor = Color(0xFFE91E63),
            valueColor = Color(0xFFE91E63)
        )
        
        StatMetricCard(
            value = stats.platformRevenue.toString().substringBefore(".").toDoubleOrNull()?.toInt().toString() + " zł",
            label = "Przychód platformy",
            icon = Icons.Default.MonetizationOn,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF8E1),
            iconColor = Color(0xFFFBC02D),
            valueColor = Color(0xFFFBC02D)
        )
    }
}

@Composable
private fun ArtworkStatisticsGrid(stats: AdminDashboardStatsDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalArtworks.toString(),
            label = "Wszystkie dzieła",
            icon = Icons.Default.Palette,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFF3E5F5),
            iconColor = Color(0xFF9C27B0),
            valueColor = Color(0xFF9C27B0)
        )
        
        StatMetricCard(
            value = stats.activeListings.toString(),
            label = "Aktywne ogłoszenia",
            icon = Icons.Default.Visibility,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE1F5FE),
            iconColor = Color(0xFF03A9F4),
            valueColor = Color(0xFF03A9F4)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.soldArtworks.toString(),
            label = "Sprzedane dzieła",
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF4CAF50),
            valueColor = Color(0xFF4CAF50)
        )
        
        StatMetricCard(
            value = stats.totalCategories.toString(),
            label = "Kategorie",
            icon = Icons.Default.Category,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFCE4EC),
            iconColor = Color(0xFFEC407A),
            valueColor = Color(0xFFEC407A)
        )
    }
}

@Composable
private fun OrderStatisticsRow(stats: AdminDashboardStatsDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardCompact(
            value = stats.totalOrders.toString(),
            label = "Wszystkie zamówienia",
            icon = Icons.Default.ShoppingBag,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE3F2FD)
        )
        
        StatMetricCardCompact(
            value = stats.pendingOrders.toString(),
            label = "Oczekujące",
            icon = Icons.Default.HourglassEmpty,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF9C4)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardCompact(
            value = stats.completedOrders.toString(),
            label = "Zakończone",
            icon = Icons.Default.Done,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFC8E6C9)
        )
        
        StatMetricCardCompact(
            value = String.format("%.2f zł", stats.averageUserBalance),
            label = "Średnie saldo użyt.",
            icon = Icons.Default.AccountBalance,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE0F2F1)
        )
    }
}
