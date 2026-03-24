package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.AdminStatistics
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.components.StatMetricCard
import com.example.artsphere.ui.components.StatMetricCardCompact
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
    val statistics = MockStatisticsProvider.getAdminStatistics()
    val scrollState = rememberScrollState()
    
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sekcja: Użytkownicy
            SectionTitle("Użytkownicy")
            UserStatisticsGrid(statistics)
            
            // Sekcja: Transakcje
            SectionTitle("Transakcje i Przychody")
            TransactionStatisticsGrid(statistics)
            
            // Sekcja: Dzieła Sztuki
            SectionTitle("Dzieła Sztuki")
            ArtworkStatisticsGrid(statistics)
            
            // Sekcja: Zamówienia
            SectionTitle("Zamówienia")
            OrderStatisticsRow(statistics)
            
            Spacer(modifier = Modifier.height(16.dp))
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
private fun UserStatisticsGrid(stats: AdminStatistics) {
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
private fun TransactionStatisticsGrid(stats: AdminStatistics) {
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
            value = stats.completedOrders.toString(),
            label = "Zrealizowane zamówienia",
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF4CAF50),
            valueColor = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun ArtworkStatisticsGrid(stats: AdminStatistics) {
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
}

@Composable
private fun OrderStatisticsRow(stats: AdminStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardCompact(
            value = stats.pendingOrders.toString(),
            label = "Oczekujące zamówienia",
            icon = Icons.Default.HourglassEmpty,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF9C4)
        )
        
        StatMetricCardCompact(
            value = stats.completedOrders.toString(),
            label = "Zakończone",
            icon = Icons.Default.Done,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFC8E6C9)
        )
    }
}
