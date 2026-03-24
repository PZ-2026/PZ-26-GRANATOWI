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
import com.example.artsphere.ui.ClientStatistics
import com.example.artsphere.ui.MockStatisticsProvider
import com.example.artsphere.ui.components.StatMetricCard
import com.example.artsphere.ui.components.StatMetricCardCompact
import com.example.artsphere.ui.components.StatMetricCardWithTrend
import java.text.NumberFormat
import java.util.Locale

/**
 * Ekran statystyk dla kupującego/klienta
 * Wyświetla statystyki zakupów, ulubionych artystów i aktywności
 */
@Composable
fun ClientDashboardScreen(
    onBackClick: () -> Unit,
    balance: Double = 1500.0
) {
    val statistics = MockStatisticsProvider.getClientStatistics()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header (zgodny z ClientPanelScreen - Material theme)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.primary)
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
                        text = "Moje Statystyki",
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
                                imageVector = Icons.Default.CardMembership,
                                contentDescription = "Członek",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statistics.memberSince,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
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
            // Sekcja: Wydatki
            SectionTitle("Moje Wydatki")
            SpendingOverview(statistics)
            
            // Sekcja: Zakupy
            SectionTitle("Historia Zakupów")
            PurchaseOverview(statistics)
            
            // Sekcja: Aktywność
            SectionTitle("Aktywność")
            ActivityOverview(statistics)
            
            // Sekcja: Zaangażowanie
            SectionTitle("Zaangażowanie")
            EngagementOverview(statistics)
            
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
private fun SpendingOverview(stats: ClientStatistics) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
        maximumFractionDigits = 0
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = formatter.format(stats.totalSpent),
            label = "Całkowite wydatki",
            icon = Icons.Default.AccountBalanceWallet,
            modifier = Modifier.weight(1f),
            useGradient = true,
            gradientColors = listOf(Color(0xFF6650a4), Color(0xFF9C27B0))
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardWithTrend(
            value = formatter.format(stats.spentThisMonth),
            label = "Wydane w tym miesiącu",
            icon = Icons.Default.ShoppingCart,
            trendValue = "+8%",
            trendPositive = false,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE3F2FD)
        )
    }
}

@Composable
private fun PurchaseOverview(stats: ClientStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.totalPurchases.toString(),
            label = "Wszystkie zakupy",
            icon = Icons.Default.ShoppingBag,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9),
            iconColor = Color(0xFF4CAF50),
            valueColor = Color(0xFF4CAF50)
        )
        
        StatMetricCard(
            value = stats.purchasesThisMonth.toString(),
            label = "Zakupy w tym miesiącu",
            icon = Icons.Default.LocalMall,
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
        StatMetricCard(
            value = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
                maximumFractionDigits = 0
            }.format(stats.totalSpent / stats.totalPurchases),
            label = "Średnia wartość zakupu",
            icon = Icons.Default.TrendingUp,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFF3E5F5),
            iconColor = Color(0xFF9C27B0),
            valueColor = Color(0xFF9C27B0)
        )
    }
}

@Composable
private fun ActivityOverview(stats: ClientStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.wishlistCount.toString(),
            label = "Lista życzeń",
            icon = Icons.Default.FavoriteBorder,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFCE4EC),
            iconColor = Color(0xFFE91E63),
            valueColor = Color(0xFFE91E63)
        )
        
        StatMetricCard(
            value = stats.savedArtworks.toString(),
            label = "Zapisane dzieła",
            icon = Icons.Default.Bookmark,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE1F5FE),
            iconColor = Color(0xFF03A9F4),
            valueColor = Color(0xFF03A9F4)
        )
    }
}

@Composable
private fun EngagementOverview(stats: ClientStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCardCompact(
            value = stats.favoriteArtistsCount.toString(),
            label = "Ulubieni artyści",
            icon = Icons.Default.Group,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFF3E5F5)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatMetricCard(
            value = stats.reviewsGiven.toString(),
            label = "Wystawione opinie",
            icon = Icons.Default.RateReview,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF9C4),
            iconColor = Color(0xFFFBC02D),
            valueColor = Color(0xFFFBC02D)
        )
        
        StatMetricCard(
            value = String.format("%.1f", stats.averageRating),
            label = "Średnia ocena",
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFECB3),
            iconColor = Color(0xFFFFD700),
            valueColor = Color(0xFFFFD700)
        )
    }
    
    // Karta z podsumowaniem
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
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Status",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(40.dp)
                )
                
                Column {
                    Text(
                        text = "Członek od",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stats.memberSince,
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
                    text = "Aktywny klient",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
