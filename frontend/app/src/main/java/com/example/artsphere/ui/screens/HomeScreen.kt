package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.artsphere.ui.components.Header
import com.example.artsphere.ui.components.HeroSection
import com.example.artsphere.ui.components.SearchSection
import com.example.artsphere.ui.components.StatisticsSection

@Composable
fun HomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBrowseClick: () -> Unit,
    onBecomeSellerClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Header(onLoginClick = onLoginClick, onRegisterClick = onRegisterClick)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HeroSection(
                    onBrowseClick = onBrowseClick,
                    onBecomeSellerClick = onBecomeSellerClick
                )
            }
            item {
                SearchSection()
            }
            item {
                StatisticsSection()
            }
        }
    }
}