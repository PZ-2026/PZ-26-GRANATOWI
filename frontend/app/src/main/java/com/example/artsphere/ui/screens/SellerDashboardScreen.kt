package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.SellerStatistics
import com.example.artsphere.ui.components.StatMetricCard
import com.example.artsphere.ui.components.StatMetricCardCompact
import com.example.artsphere.ui.components.StatMetricCardWithTrend
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran statystyk dla sprzedawcy
 * Wyświetla statystyki sprzedaży, oceny i dzieła
 */
@Composable
fun SellerDashboardScreen(
    onBackClick: () -> Unit,
    balance: Double = 1500.0
) {
    val statistics = MockStatisticsProvider.getSellerStatistics()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header (zgodny z SellerPanelScreen - zielony kolor)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color(0xFF2E8B57))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
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
                        text = "Statystyki Sprzedaży",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Twój bilans",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).format(balance),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Ocena",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = String.format("%.1f", statistics.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
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
            // Sekcja: Przegląd finansowy
            SectionTitle("Przegląd Finansowy")
            FinancialOverview(statistics)
            
            // Sekcja: Sprzedaż i Zamówienia
            SectionTitle("Sprzedaż")
            SalesOverview(statistics)
            
            // Sekcja: Dzieła Sztuki
            SectionTitle("Twoje Dzieła")
            ArtworkOverview(statistics)
            
            // Sekcja: Zaangażowanie
            SectionTitle("Zaangażowanie")
            EngagementOverview(statistics)
            
            // Najlepiej sprzedające się dzieło
            TopArtworkCard(statistics)
            
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
private fun FinancialOverview(stats: SellerStatistics) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
        maximumFractionDigits = 0
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = formatter.format(stats.totalRevenue),
            label = "Całkowity przychód",
            icon = Icons.Default.AccountBalance,
            modifier = Modifier.weight(1f),
            useGradient = true,
            gradientColors = listOf(Color(0xFF2E8B57), Color(0xFF3CB371))
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardWithTrend(
            value = formatter.format(stats.revenueThisMonth),
            label = "Przychód w tym miesiącu",
            icon = Icons.Default.TrendingUp,
            trendValue = "+15%",
            trendPositive = true,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9)
        )
    }
}

@Composable
private fun SalesOverview(stats: SellerStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalSales.toString(),
            label = "Wszystkie sprzedaże",
            icon = Icons.Default.ShoppingBag,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE3F2FD),
            iconColor = Color(0xFF2196F3),
            valueColor = Color(0xFF2196F3)
        )
        
        StatMetricCard(
            value = stats.soldThisMonth.toString(),
            label = "Sprzedane w tym miesiącu",
            icon = Icons.Default.LocalOffer,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF3E0),
            iconColor = Color(0xFFFF9800),
            valueColor = Color(0xFFFF9800)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardCompact(
            value = stats.completedOrders.toString(),
            label = "Zrealizowane zamówienia",
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFC8E6C9)
        )
        
        StatMetricCardCompact(
            value = stats.pendingOrders.toString(),
            label = "Oczekujące",
            icon = Icons.Default.HourglassEmpty,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF9C4)
        )
    }
}

@Composable
private fun ArtworkOverview(stats: SellerStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalArtworks.toString(),
            label = "Wszystkie dzieła",
            icon = Icons.Default.Image,
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
private fun EngagementOverview(stats: SellerStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.followerCount.toString(),
            label = "Obserwujący",
            icon = Icons.Default.Group,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFCE4EC),
            iconColor = Color(0xFFE91E63),
            valueColor = Color(0xFFE91E63)
        )
        
        StatMetricCard(
            value = String.format("%.1f", stats.averageRating),
            label = "Średnia ocena",
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF9C4),
            iconColor = Color(0xFFFFD700),
            valueColor = Color(0xFFFFD700)
        )
    }
}

@Composable
private fun TopArtworkCard(stats: SellerStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Top",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(40.dp)
                )
                
                Column {
                    Text(
                        text = "Najlepiej sprzedające się",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stats.topArtworkTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF4CAF50)
            ) {
                Text(
                    text = "${stats.topArtworkSales} sprzedaży",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
