package com.example.artsphere.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Globalny stan użytkownika
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUsername by remember { mutableStateOf("") }
    var currentBalance by remember { mutableStateOf(1500.00) } // Przykładowe saldo

    NavHost(navController = navController, startDestination = "home") {

        // Ekran Główny
        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                username = currentUsername,
                balance = currentBalance,
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register/user") },
                onBrowseClick = { /* Przewijanie do listy dzieł */ },
                onBecomeSellerClick = { navController.navigate("register/seller") },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = { navController.navigate("client_panel") }
            )
        }

        // Ekran Logowania
        composable("login") {
            LoginScreen(
                onNavigateBack = {
                    navController.navigate("home") { popUpTo("home") { inclusive = false } }
                },
                onNavigateToRegister = {
                    navController.navigate("register/user") { popUpTo("login") { inclusive = true } }
                },
                onLoginSuccess = { username ->
                    isLoggedIn = true
                    currentUsername = username
                    navController.navigate("home") { popUpTo(0) }
                }
            )
        }

        // Ekran Rejestracji
        composable("register/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "user"
            RegisterScreen(
                initialRole = role,
                onNavigateBack = {
                    navController.navigate("home") { popUpTo("home") { inclusive = false } }
                },
                onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("register/{role}") { inclusive = true } }
                }
            )
        }


        // Ekran Koszyka
        composable("cart") {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckoutClick = { /* Logika zakupów */ }
            )
        }

        // Ekran Panelu Klienta
        composable("client_panel") {
            ClientPanelScreen(
                username = currentUsername,
                balance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    navController.navigate("home") { popUpTo(0) }
                }
            )
        }
    }
}