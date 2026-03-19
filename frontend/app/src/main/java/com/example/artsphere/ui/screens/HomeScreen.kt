package com.example.artsphere.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
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
    // Menadżer fokusu do ukrywania klawiatury
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
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