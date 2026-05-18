package com.example.artsphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ClientStatisticsDto
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.ui.components.ReportDialog
import com.example.artsphere.ui.components.StatMetricCard
import com.example.artsphere.ui.components.StatMetricCardCompact
import com.example.artsphere.ui.components.StatMetricCardWithTrend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClientDashboardScreen(
    userId: Long,
    onBackClick: () -> Unit,
    balance: Double = 0.0
) {
    var statistics by remember { mutableStateOf<ClientStatisticsDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPurchaseReportDialog by remember { mutableStateOf(false) }
    var showTransactionReportDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(userId) {
        try {
            val res = RetrofitClient.authApi.getClientStatistics(userId)
            if (res.isSuccessful) statistics = res.body()
        } catch (e: Exception) { } finally { isLoading = false }
    }

    // Dialog raportu zakupów
    if (showPurchaseReportDialog) {
        ReportDialog(
            title = "Raport zakupów",
            onDismiss = { showPurchaseReportDialog = false },
            onGenerateReport = { dateFrom, dateTo ->
                {
                    val response = RetrofitClient.reportApi.getClientPurchaseReport(
                        userId = userId,
                        dateFrom = dateFrom.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        dateTo = dateTo.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    )
                    if (response.isSuccessful) response.body() else null
                }
            }
        )
    }

    // Dialog raportu transakcji
    if (showTransactionReportDialog) {
        ReportDialog(
            title = "Raport transakcji",
            onDismiss = { showTransactionReportDialog = false },
            onGenerateReport = { dateFrom, dateTo ->
                {
                    val response = RetrofitClient.reportApi.getClientTransactionsReport(
                        userId = userId,
                        dateFrom = dateFrom.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        dateTo = dateTo.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    )
                    if (response.isSuccessful) response.body() else null
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
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
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                    }
                    Text("Moje Statystyki", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Twój bilans", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).format(balance),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
                        )
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CardMembership, contentDescription = "Członek", tint = Color.White, modifier = Modifier.size(18.dp))
                            Text(statistics?.memberSince ?: "Wczytywanie...", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (statistics != null) {
            val stats = statistics!!
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column {
                    SectionTitle("Finanse")
                    SpendingOverview(stats)
                }

                Column {
                    SectionTitle("Zamówienia")
                    PurchaseOverview(stats)
                }

                Column {
                    SectionTitle("Aktywność społecznościowa")
                    ActivityOverview(stats)
                }

                // Sekcja raportów
                Column {
                    SectionTitle("Generuj raporty PDF")
                    ReportButtons(
                        onPurchaseReportClick = { showPurchaseReportDialog = true },
                        onTransactionReportClick = { showTransactionReportDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReportButtons(
    onPurchaseReportClick: () -> Unit,
    onTransactionReportClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onPurchaseReportClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4))
        ) {
            Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Raport zakupów", fontSize = 14.sp)
        }

        Button(
            onClick = onTransactionReportClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Raport transakcji portfela", fontSize = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun SpendingOverview(stats: ClientStatisticsDto) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply { maximumFractionDigits = 0 }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatMetricCard(
            value = formatter.format(stats.totalSpent),
            label = "Całkowite wydatki",
            icon = Icons.Default.AccountBalanceWallet,
            modifier = Modifier.weight(1f),
            useGradient = true,
            gradientColors = listOf(Color(0xFF6650a4), Color(0xFF9C27B0))
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatMetricCardWithTrend(
            value = formatter.format(stats.spentThisMonth),
            label = "Wydane w tym miesiącu",
            icon = Icons.Default.ShoppingCart,
            trendValue = "",
            trendPositive = true,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE3F2FD)
        )
    }
}

@Composable
private fun PurchaseOverview(stats: ClientStatisticsDto) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatMetricCard(
            value = stats.totalPurchases.toString(), label = "Wszystkie zakupy", icon = Icons.Default.ShoppingBag, modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE8F5E9), iconColor = Color(0xFF4CAF50), valueColor = Color(0xFF4CAF50)
        )
        StatMetricCard(
            value = stats.purchasesThisMonth.toString(), label = "Zakupy w tym miesiącu", icon = Icons.Default.LocalMall, modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFF3E0), iconColor = Color(0xFFFF9800), valueColor = Color(0xFFFF9800)
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val avg = if (stats.totalPurchases > 0) stats.totalSpent / stats.totalPurchases else 0.0
        StatMetricCard(
            value = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply { maximumFractionDigits = 0 }.format(avg),
            label = "Średnia wartość zakupu", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFF3E5F5), iconColor = Color(0xFF9C27B0), valueColor = Color(0xFF9C27B0)
        )
    }
}

@Composable
private fun ActivityOverview(stats: ClientStatisticsDto) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatMetricCard(
            value = stats.wishlistCount.toString(), label = "Lista życzeń", icon = Icons.Default.FavoriteBorder, modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFFFEBEE), iconColor = Color(0xFFE91E63), valueColor = Color(0xFFE91E63)
        )
        StatMetricCard(
            value = stats.favoriteArtistsCount.toString(), label = "Obserwowani artyści", icon = Icons.Default.PersonSearch, modifier = Modifier.weight(1f),
            backgroundColor = Color(0xFFE0F7FA), iconColor = Color(0xFF00BCD4), valueColor = Color(0xFF00BCD4)
        )
    }
}
