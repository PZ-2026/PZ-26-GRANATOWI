package com.example.artsphere.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    isLoggedIn: Boolean,
    username: String,
    balance: Double,
    role: String,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = "ArtSphere Logo", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ArtSphere", fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            if (isLoggedIn) {
                // sprzedawca nie ma koszyka
                if (role != "seller") {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Koszyk")
                    }
                }

                Box {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Witaj, $username!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = { menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Saldo: ${String.format("%.2f", balance)} zł") },
                            onClick = { menuExpanded = false }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = {
                                Text(if (role == "seller") "Panel Sprzedawcy" else "Panel Użytkownika")
                            },
                            onClick = { menuExpanded = false; onProfileClick() },
                            leadingIcon = {
                                Icon(if (role == "seller") Icons.Default.Storefront else Icons.Default.Settings, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Wyloguj", color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onLogoutClick() },
                            leadingIcon = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            } else {
                TextButton(onClick = onLoginClick) { Text("Zaloguj się") }
                Button(onClick = onRegisterClick) { Text("Rejestracja") }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}