package com.example.artsphere.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.ui.screens.*
import com.example.artsphere.ui.screens.Client.FollowedOffersScreen
import com.example.artsphere.ui.screens.Client.OrdersScreen
import com.example.artsphere.ui.screens.Client.SupportScreen
import com.example.artsphere.ui.screens.Seller.FollowersScreen
import com.example.artsphere.ui.screens.Seller.SalesHistoryScreen
import com.example.artsphere.ui.screens.Seller.TopFansScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Globalny stan użytkownika
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUserId by remember { mutableStateOf(0L) }
    var currentUsername by remember { mutableStateOf("") }
    var currentBalance by remember { mutableStateOf(1500.00) }
    var currentUserRole by remember { mutableStateOf("guest") } // Zmieniono na guest jako domyślny

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                username = currentUsername,
                balance = currentBalance,
                role = currentUserRole,
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register/user") },
                onBrowseClick = { /* Przewijanie do wyszukiwarki pozostaje na frontendzie */ },
                onBecomeSellerClick = { navController.navigate("register/seller") },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUsername = ""
                    currentUserRole = "guest"
                },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    when (currentUserRole.uppercase()) {
                        "SELLER", "ARTIST" -> navController.navigate("seller_panel")
                        "ADMIN" -> navController.navigate("admin_panel")
                        else -> navController.navigate("client_panel")
                    }
                },
                onArtworkClick = { artworkId ->
                    // Przekazanie ID do publicznego ekranu dzieła
                    navController.navigate("public_artwork_detail/$artworkId")
                }
            )
        }

        // --- EKRAN SZCZEGÓŁÓW DZIEŁA DLA WSZYSTKICH ---
        composable("public_artwork_detail/{artworkId}") { backStackEntry ->
            val artworkIdStr = backStackEntry.arguments?.getString("artworkId")
            val artworkId = artworkIdStr?.toLongOrNull() ?: 0L

            PublicArtworkDetailScreen(
                artworkId = artworkId,
                isLoggedIn = isLoggedIn,
                role = currentUserRole,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate("login") }
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
                onLoginSuccess = { userId, username, role ->
                    isLoggedIn = true
                    currentUserId = userId
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
                    currentUserRole = "guest"
                    navController.navigate("home") { popUpTo(0) }
                },
                onEditProfileClick = { navController.navigate("edit_profile/client") },
                onStatisticsClick = { navController.navigate("client_dashboard") },
                onAddressesClick = { navController.navigate("addresses") },
                onFinanceClick = { navController.navigate("finance/client") },
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
                    currentUserRole = "guest"
                    navController.navigate("home") { popUpTo(0) }
                },
                onEditProfileClick = { navController.navigate("edit_profile/seller") },
                onStatisticsClick = { navController.navigate("seller_dashboard") },
                onArtworksClick = { navController.navigate("seller_artworks") },
                onAddArtworkClick = { navController.navigate("artwork_add") },
                onFinanceClick = { navController.navigate("finance/seller") },
                onSalesHistoryClick = { navController.navigate("seller_sales_history") },
                onTopFansClick = { navController.navigate("seller_top_fans") },
                onFollowersClick = { navController.navigate("seller_followers") },
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
                    currentUserRole = "guest"
                    navController.navigate("home") { popUpTo(0) }
                },
                onEditProfileClick = { navController.navigate("edit_profile/admin") },
                onStatisticsClick = { navController.navigate("admin_dashboard") },
                onAddressesClick = { navController.navigate("addresses_admin") },
                onUsersClick = { navController.navigate("admin_users") },
                onArtworksClick = { navController.navigate("admin_artworks") },
                onSellersClick = { navController.navigate("admin_sellers") },
                onOrdersClick = { navController.navigate("admin_orders") },
                onCategoriesClick = { navController.navigate("admin_categories") }
            )
        }

        // edycja profilu
        composable("edit_profile/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "client"
            EditProfileScreen(
                role = role,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // finanse
        composable("finance/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "client"
            FinanceScreen(
                role = role,
                currentBalance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onBalanceChange = { newBalance -> currentBalance = newBalance }
            )
        }

        // dashboard statystyk administratora
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // zarządzanie użytkownikami
        composable("admin_users") {
            var selectedUser by remember { mutableStateOf<com.example.artsphere.ui.UserInfo?>(null) }

            if (selectedUser == null) {
                AdminUsersScreen(
                    onBackClick = { navController.popBackStack() },
                    onUserClick = { user -> selectedUser = user }
                )
            } else {
                AdminUserDetailScreen(
                    user = selectedUser!!,
                    onBackClick = { selectedUser = null },
                    onEditClick = {
                        // Tu można dodać nawigację do ekranu edycji
                        // navController.navigate("admin_user_edit/${selectedUser!!.id}")
                    },
                    onDeleteClick = {
                        // Placeholder: symulacja usunięcia
                        selectedUser = null
                    },
                    onToggleStatusClick = {
                        // Placeholder: symulacja zmiany statusu
                        selectedUser = selectedUser!!.copy(isActive = !selectedUser!!.isActive)
                    },
                    onChangeRoleClick = {
                        // Placeholder: symulacja zmiany roli
                    }
                )
            }
        }

        // zarządzanie dziełami
        composable("admin_artworks") {
            var selectedArtwork by remember { mutableStateOf<com.example.artsphere.ui.ArtworkInfo?>(null) }

            if (selectedArtwork == null) {
                AdminArtworksScreen(
                    onBackClick = { navController.popBackStack() },
                    onArtworkClick = { artwork -> selectedArtwork = artwork }
                )
            } else {
                AdminArtworkDetailScreen(
                    artwork = selectedArtwork!!,
                    onBackClick = { selectedArtwork = null },
                    onEditClick = {
                        // Placeholder: nawigacja do edycji
                    },
                    onDeleteClick = {
                        // Placeholder: symulacja usunięcia
                        selectedArtwork = null
                    },
                    onChangeStatusClick = { newStatus ->
                        // Placeholder: symulacja zmiany statusu
                        selectedArtwork = selectedArtwork!!.copy(status = newStatus)
                    }
                )
            }
        }

        // zarządzanie sprzedawcami
        composable("admin_sellers") {
            var selectedSeller by remember { mutableStateOf<com.example.artsphere.ui.SellerInfo?>(null) }

            if (selectedSeller == null) {
                AdminSellersScreen(
                    onBackClick = { navController.popBackStack() },
                    onSellerClick = { seller -> selectedSeller = seller }
                )
            } else {
                AdminSellerDetailScreen(
                    seller = selectedSeller!!,
                    onBackClick = { selectedSeller = null },
                    onEditClick = {
                        // Placeholder: nawigacja do edycji
                    },
                    onDeleteClick = {
                        // Placeholder: symulacja usunięcia
                        selectedSeller = null
                    },
                    onToggleStatusClick = {
                        // Placeholder: symulacja zmiany statusu
                        selectedSeller = selectedSeller!!.copy(isActive = !selectedSeller!!.isActive)
                    },
                    onToggleVerificationClick = {
                        // Placeholder: symulacja weryfikacji
                        selectedSeller = selectedSeller!!.copy(isVerified = !selectedSeller!!.isVerified)
                    }
                )
            }
        }

        // zarządzanie zamówieniami
        composable("admin_orders") {
            var selectedOrder by remember { mutableStateOf<com.example.artsphere.ui.OrderInfo?>(null) }

            if (selectedOrder == null) {
                AdminOrdersScreen(
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { order -> selectedOrder = order }
                )
            } else {
                AdminOrderDetailScreen(
                    order = selectedOrder!!,
                    onBackClick = { selectedOrder = null },
                    onChangeStatusClick = { newStatus ->
                        // Placeholder: symulacja zmiany statusu
                        selectedOrder = selectedOrder!!.copy(status = newStatus)
                    },
                    onCancelOrderClick = {
                        // Placeholder: symulacja anulowania
                        selectedOrder = selectedOrder!!.copy(
                            status = "CANCELLED",
                            paymentStatus = if (selectedOrder!!.paymentStatus == "PAID") "REFUNDED" else "PENDING"
                        )
                    },
                    onSendMessageClick = {
                        // Placeholder: nawigacja do wiadomości
                    }
                )
            }
        }

        // zarządzanie kategoriami
        composable("admin_categories") {
            var selectedCategory by remember { mutableStateOf<com.example.artsphere.ui.CategoryInfo?>(null) }

            if (selectedCategory == null) {
                AdminCategoriesScreen(
                    onBackClick = { navController.popBackStack() },
                    onCategoryClick = { category -> selectedCategory = category }
                )
            } else {
                AdminCategoryDetailScreen(
                    category = selectedCategory!!,
                    onBackClick = { selectedCategory = null },
                    onEditClick = {
                        // Placeholder: nawigacja do edycji
                    },
                    onDeleteClick = {
                        // Placeholder: symulacja usunięcia
                        selectedCategory = null
                    },
                    onToggleStatusClick = {
                        // Placeholder: symulacja zmiany statusu
                        selectedCategory = selectedCategory!!.copy(isActive = !selectedCategory!!.isActive)
                    },
                    onManageSubcategoriesClick = {
                        // Placeholder: nawigacja do podkategorii
                    }
                )
            }
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

        // zarządzanie adresami - Kupujący
        composable("addresses") {
            AddressesScreen(
                userId = currentUserId,
                isAdmin = false,
                onNavigateBack = { navController.popBackStack() },
                onAddAddress = { navController.navigate("address_add") },
                onEditAddress = { addressId ->
                    navController.navigate("address_edit/$addressId")
                }
            )
        }

        // zarządzanie adresami - Admin
        composable("addresses_admin") {
            AddressesScreen(
                userId = currentUserId,
                isAdmin = true,
                onNavigateBack = { navController.popBackStack() },
                onAddAddress = { navController.navigate("address_add_admin") },
                onEditAddress = { addressId ->
                    navController.navigate("address_edit_admin/$addressId")
                }
            )
        }

        // dodawanie adresu
        composable("address_add") {
            AddressFormScreen(
                userId = currentUserId,
                addressId = null,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("addresses") {
                        popUpTo("addresses") { inclusive = true }
                    }
                }
            )
        }

        // edycja adresu
        composable("address_edit/{addressId}") { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")?.toLongOrNull()
            if (addressId != null) {
                AddressFormScreen(
                    userId = currentUserId,
                    addressId = addressId,
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("addresses") {
                            popUpTo("addresses") { inclusive = true }
                        }
                    }
                )
            }
        }

        // dodawanie adresu - Admin
        composable("address_add_admin") {
            AddressFormScreen(
                userId = currentUserId,
                addressId = null,
                isAdmin = true,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("addresses_admin") {
                        popUpTo("addresses_admin") { inclusive = true }
                    }
                }
            )
        }

        // edycja adresu - Admin
        composable("address_edit_admin/{addressId}") { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")?.toLongOrNull()
            if (addressId != null) {
                AddressFormScreen(
                    userId = currentUserId,
                    addressId = addressId,
                    isAdmin = true,
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("addresses_admin") {
                            popUpTo("addresses_admin") { inclusive = true }
                        }
                    }
                )
            }
        }

        // zarządzanie dziełami - Sprzedawca
        composable("seller_artworks") {
            SellerArtworksScreen(
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() },
                onAddArtwork = { navController.navigate("artwork_add") },
                onEditArtwork = { artworkId ->
                    navController.navigate("artwork_edit/$artworkId")
                }
            )
        }

        // dodawanie dzieła
        composable("artwork_add") {
            ArtworkFormScreen(
                userId = currentUserId,
                artworkId = null,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("seller_artworks") {
                        popUpTo("seller_artworks") { inclusive = true }
                    }
                }
            )
        }

        // edycja dzieła
        composable("artwork_edit/{artworkId}") { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getString("artworkId")?.toLongOrNull()
            if (artworkId != null) {
                ArtworkFormScreen(
                    userId = currentUserId,
                    artworkId = artworkId,
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("seller_artworks") {
                            popUpTo("seller_artworks") { inclusive = true }
                        }
                    }
                )
            }
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