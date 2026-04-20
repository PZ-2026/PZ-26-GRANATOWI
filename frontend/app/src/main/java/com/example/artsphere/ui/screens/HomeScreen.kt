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
import com.example.artsphere.ui.components.*

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    username: String,
    balance: Double,
    role: String,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBrowseClick: () -> Unit,
    onBecomeSellerClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onArtworkClick: (Long) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            Header(
                isLoggedIn = isLoggedIn,
                username = username,
                balance = balance,
                role = role,
                onLoginClick = onLoginClick,
                onRegisterClick = onRegisterClick,
                onLogoutClick = onLogoutClick,
                onCartClick = onCartClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            item {
                HeroSection(
                    role = role,
                    onBrowseClick = onBrowseClick,
                    onBecomeSellerClick = onBecomeSellerClick
                )
            }
            item {
                SearchSection(onArtworkClick = onArtworkClick)
            }
            item { StatisticsSection() }
        }
    }
}
