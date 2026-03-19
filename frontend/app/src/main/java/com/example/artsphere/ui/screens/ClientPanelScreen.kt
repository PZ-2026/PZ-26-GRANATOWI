package com.example.artsphere.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPanelScreen(
    username: String,
    balance: Double,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Użytkownika") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Nagłówek powitalny w hamburgerze
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Zalogowano jako", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dostępne środki:", fontWeight = FontWeight.SemiBold)
                    Text("${String.format("%.2f", balance)} zł", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Text("Ustawienia i zarządzanie", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            // panel uzytkownika
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item { PanelCardItem("Edytuj moje dane", Icons.Default.ManageAccounts) }
                item { PanelCardItem("Zarządzanie adresami", Icons.Default.LocationOn) }
                item { PanelCardItem("Moje zakupy (historia)", Icons.Default.History) }
                item { PanelCardItem("Obserwowani artyści", Icons.Default.Group) }
                item { PanelCardItem("Mój portfel", Icons.Default.AccountBalanceWallet) }
                item { PanelCardItem("Wesprzyj sprzedawcę", Icons.Default.VolunteerActivism) }
                item { PanelCardItem("Statystyki", Icons.Default.QueryStats) }
            }

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 16.dp)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wyloguj się")
            }
        }
    }
}

@Composable
fun PanelCardItem(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { /* TODO: Przejście do danej sekcji po kliknięciu */ },
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}