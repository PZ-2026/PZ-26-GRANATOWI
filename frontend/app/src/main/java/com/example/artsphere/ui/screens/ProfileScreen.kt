package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel użytkownika") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text("Witaj, użytkowniku!", fontSize = 24.sp, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))

            ProfileMenuItem(icon = Icons.Default.ShoppingBag, title = "Moje zamówienia") { /* TODO */ }
            ProfileMenuItem(icon = Icons.Default.Favorite, title = "Zaobserwowani") { /* TODO */ }
            ProfileMenuItem(icon = Icons.Default.AccountBalanceWallet, title = "Portfel / Saldo") { /* TODO */ }
            ProfileMenuItem(icon = Icons.Default.LocationOn, title = "Adresy dostawy") { /* TODO */ }
            ProfileMenuItem(icon = Icons.Default.Settings, title = "Ustawienia konta") { /* TODO */ }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onLogoutClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wyloguj się")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}