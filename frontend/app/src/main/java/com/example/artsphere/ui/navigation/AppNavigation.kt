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
    var currentBalance by remember { mutableStateOf(1500.00) }
    var currentUserRole by remember { mutableStateOf("user") }

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                username = currentUsername,
                balance = currentBalance,
                role = currentUserRole,
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register/user") },
                onBrowseClick = { /* Przewijanie do wyszukiwarki */ },
                onBecomeSellerClick = { navController.navigate("register/seller") },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    currentUserRole = "user"
                },
                onCartClick = { navController.navigate("cart") },
                // wybranie panelu
                onProfileClick = {
                    if (currentUserRole == "seller") {
                        navController.navigate("seller_panel")
                    } else {
                        navController.navigate("client_panel")
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateBack = {
                    navController.navigate("home") { popUpTo("home") { inclusive = false } }
                },
                onNavigateToRegister = {
                    navController.navigate("register/user") { popUpTo("login") { inclusive = true } }
                },
                onLoginSuccess = { username, role ->
                    isLoggedIn = true
                    currentUsername = username
                    currentUserRole = role
                    navController.navigate("home") { popUpTo(0) }
                }
            )
        }

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

        composable("cart") {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckoutClick = { /* Przejście do kasy */ }
            )
        }

        // panel uzytkownika
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

        // panel sprzedawcy
        composable("seller_panel") {
            SellerPanelScreen(
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