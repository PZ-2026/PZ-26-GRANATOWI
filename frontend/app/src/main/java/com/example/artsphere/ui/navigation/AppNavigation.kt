package com.example.artsphere.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.ui.screens.HomeScreen
import com.example.artsphere.ui.screens.LoginScreen
import com.example.artsphere.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // Ekran Główny
        composable("home") {
            HomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register/user") },
                onBrowseClick = { /* przewijanie do listy dzieł */ },
                onBecomeSellerClick = { navController.navigate("register/seller") }
            )
        }

        // Ekran Logowania
        composable("login") {
            LoginScreen(
                onNavigateBack = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },

                onNavigateToRegister = {
                    navController.navigate("register/user") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                }
            )
        }

        // Ekran Rejestracji
        composable("register/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "user"
            RegisterScreen(
                initialRole = role,
                // Powrót do Home
                onNavigateBack = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                // przejście do logowania
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register/{role}") { inclusive = true }
                    }
                }
            )
        }
    }
}