package com.example.artsphere.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.ui.screens.*
import com.example.artsphere.ui.screens.Client.FollowedOffersScreen
import com.example.artsphere.ui.screens.Client.OrdersScreen
import com.example.artsphere.ui.screens.Client.SupportScreen
import com.example.artsphere.ui.screens.Seller.AddArtworkScreen
import com.example.artsphere.ui.screens.Seller.ArtworkDetailScreen
import com.example.artsphere.ui.screens.Seller.FollowersScreen
import com.example.artsphere.ui.screens.Seller.ManageArtworksScreen
import com.example.artsphere.ui.screens.Seller.SalesHistoryScreen
import com.example.artsphere.ui.screens.Seller.TopFansScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Globalny stan użytkownika
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUsername by remember { mutableStateOf("") }
    var currentBalance by remember { mutableStateOf(1500.00) }
    var currentUserRole by remember { mutableStateOf("user") } // "user", "seller", "admin"

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
                onProfileClick = {
                    when (currentUserRole) {
                        "seller" -> navController.navigate("seller_panel")
                        "admin" -> navController.navigate("admin_panel")
                        else -> navController.navigate("client_panel")
                    }
                }
            )
        }

        // logowanie
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

        // rejestracja
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

        // koszyk
        composable("cart") {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckoutClick = { /* Przejście do kasy */ }
            )
        }

        // ekran klienta
        composable("client_panel") {
            ClientPanelScreen(
                username = currentUsername,
                balance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    navController.navigate("home") { popUpTo(0) }
                },
                onStatisticsClick = { navController.navigate("client_dashboard") },
                onOrdersClick = { navController.navigate("client_orders") },
                onSupportClick = { navController.navigate("client_support") },
                onFollowedClick = { navController.navigate("client_followed") }
            )
        }

        // ekran sprzedawcy
        composable("seller_panel") {
            SellerPanelScreen(
                username = currentUsername,
                balance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    navController.navigate("home") { popUpTo(0) }
                },
                onStatisticsClick = { navController.navigate("seller_dashboard") },
                onFollowersClick = { navController.navigate("seller_followers") },
                onAddArtworkClick = { navController.navigate("seller_add_artwork") },
                onManageArtworksClick = { navController.navigate("seller_manage_artworks") },
                onSalesHistoryClick = { navController.navigate("seller_sales_history") },
                onTopFansClick = { navController.navigate("seller_top_fans") }
            )
        }

        // ekran admina
        composable("admin_panel") {
            AdminPanelScreen(
                username = currentUsername,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    navController.navigate("home") { popUpTo(0) }
                },
                onStatisticsClick = { navController.navigate("admin_dashboard") }
            )
        }

        // dashboard statystyk administratora
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // dashboard statystyk sprzedawcy
        composable("seller_dashboard") {
            SellerDashboardScreen(
                onBackClick = { navController.popBackStack() },
                balance = currentBalance
            )
        }

        // dashboard statystyk klienta
        composable("client_dashboard") {
            ClientDashboardScreen(
                onBackClick = { navController.popBackStack() },
                balance = currentBalance
            )
        }

        // ekran "moje zakupy" kupującego
        composable("client_orders") {
            OrdersScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ekran wesprzyj kupującego
        composable("client_support") {
            SupportScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ekran obserwowanych kupującego
        composable("client_followed") {
            FollowedOffersScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ekran obserwujących sprzedawcy
        composable("seller_followers") {
            FollowersScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ekran dodawania dzieła dla sprzedającego
        composable("seller_add_artwork") {
            AddArtworkScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("seller_artwork_detail") {
            ArtworkDetailScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Zaktualizowana trasa zarządzania
        composable("seller_manage_artworks") {
            ManageArtworksScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddNewClick = { navController.navigate("seller_add_artwork") },
                onPreviewClick = { navController.navigate("seller_artwork_detail") } // Podpięcie podglądu
            )
        }

        // ekran historii sprzedaży
        composable("seller_sales_history") {
            SalesHistoryScreen(onNavigateBack = { navController.popBackStack() })
        }

        // kran najlepszych fanów sprzedającego
        composable("seller_top_fans") {
            TopFansScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}