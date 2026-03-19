package com.example.artsphere.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeroSection(
    role: String,
    onBrowseClick: () -> Unit,
    onBecomeSellerClick: () -> Unit
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(heroGradient)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Odkryj niepowtarzalne dzieła sztuki",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Największa platforma sprzedaży autentycznych dzieł sztuki. Od klasyki po nowoczesność.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onBrowseClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Przeglądaj kolekcje", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk zostan sprzedawca tylko dla klienta
        if (role != "seller" && role != "admin") {
            OutlinedButton(
                onClick = onBecomeSellerClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zostań sprzedawcą")
            }
        }
    }
}