package com.example.artsphere.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.AdminDashboardStatsDto
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatisticsSection() {
    var stats by remember { mutableStateOf<AdminDashboardStatsDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.adminApi.getAdminDashboardStatistics()
                if (response.isSuccessful) {
                    stats = response.body()
                    isVisible = true
                }
            } catch (e: Exception) {
                // Pokaż placeholder na błąd
            } finally {
                isLoading = false
            }
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (stats != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 32.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedStatItem(
                    targetValue = stats!!.totalUsers.toFloat(),
                    label = "Zarejestrowanych\nużytkowników",
                    isAnimating = isVisible
                )
                AnimatedStatItem(
                    targetValue = stats!!.totalArtworks.toFloat(),
                    label = "Dostępnych\ndzieł",
                    isAnimating = isVisible
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CurrencyStatItem(
                    targetValue = stats!!.totalTransactionValue,
                    label = "Wartość\ntransakcji",
                    isAnimating = isVisible
                )
                StatItem(
                    value = "${stats!!.completedOrders}/${stats!!.totalOrders}",
                    label = "Zrealizowanych\nzamówień"
                )
            }
        }
    }
}

@Composable
fun AnimatedStatItem(
    targetValue: Float,
    label: String,
    isAnimating: Boolean
) {
    val animatedValue = remember { Animatable(0f) }
    
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            animatedValue.animateTo(
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = 1500,
                    delayMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = animatedValue.value.toInt().toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun CurrencyStatItem(
    targetValue: Double,
    label: String,
    isAnimating: Boolean
) {
    val animatedValue = remember { Animatable(0f) }
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL")).apply {
        maximumFractionDigits = 0
    }
    
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            animatedValue.animateTo(
                targetValue = targetValue.toFloat(),
                animationSpec = tween(
                    durationMillis = 1500,
                    delayMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatter.format(animatedValue.value.toInt()),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}