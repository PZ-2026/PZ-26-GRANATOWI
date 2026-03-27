package com.example.artsphere.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.ui.components.DeleteAccountDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPanelScreen(
    username: String,
    balance: Double,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onFollowedClick: () -> Unit = {},
    onAddressesClick: () -> Unit = {},
    onFinanceClick: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onLogoutClick() // Symulacja: wylogowanie po "usunięciu"
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Użytkownika") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Wróć") }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onFinanceClick() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Zalogowano jako", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dostępne środki:", fontWeight = FontWeight.SemiBold)
                        Text(text = "${String.format("%.2f", balance)} zł", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            item {
                Text(
                    text = "Ustawienia i zarządzanie",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) { PanelCardItem("Edytuj profil", Icons.Default.ManageAccounts, onClick = onEditProfileClick) }
                        Box(modifier = Modifier.weight(1f)) { PanelCardItem("Zarządzanie adresami", Icons.Default.LocationOn, onClick = onAddressesClick) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PanelCardItem("Moje zakupy", Icons.Default.History, onClick = onOrdersClick)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PanelCardItem("Obserwowani", Icons.Default.Group, onClick = onFollowedClick)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) { PanelCardItem("Mój portfel", Icons.Default.AccountBalanceWallet, onClick = onFinanceClick) }
                        Box(modifier = Modifier.weight(1f)) { PanelCardItem("Wesprzyj", Icons.Default.VolunteerActivism, onClick = onSupportClick) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) { PanelCardItem("Statystyki", Icons.Default.QueryStats, onClick = onStatisticsClick) }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Wyloguj się")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Usuń konto na zawsze", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PanelCardItem(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp).padding(bottom = 8.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}
