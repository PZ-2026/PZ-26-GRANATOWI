package com.example.artsphere.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.api.ArtworkResponse
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
    var currentBalance by remember { mutableStateOf(0.0) }
    var currentUserRole by remember { mutableStateOf("guest") }

    // STAN KOSZYKA (Współdzielony między ekranami)
    var cartItems by remember { mutableStateOf<List<ArtworkResponse>>(emptyList()) }

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                username = currentUsername,
                balance = currentBalance,
                role = currentUserRole,
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register/user") },
                onBrowseClick = { },
                onBecomeSellerClick = { navController.navigate("register/seller") },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUserId = 0L
                    currentUsername = ""
                    currentUserRole = "guest"
                    currentBalance = 0.0
                    // Koszyk nie jest czyszczony po wylogowaniu
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
                    navController.navigate("public_artwork_detail/$artworkId")
                }
            )
        }

        composable("public_artwork_detail/{artworkId}") { backStackEntry ->
            val artworkIdStr = backStackEntry.arguments?.getString("artworkId")
            val artworkId = artworkIdStr?.toLongOrNull() ?: 0L

            PublicArtworkDetailScreen(
                artworkId = artworkId,
                currentUserId = currentUserId,
                isLoggedIn = isLoggedIn,
                role = currentUserRole,
                cartItems = cartItems,
                onAddToCart = { item ->
                    if (!cartItems.any { it.id == item.id }) cartItems = cartItems + item
                },
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("cart") {
            CartScreen(
                cartItems = cartItems,
                onRemoveItem = { itemToRemove -> cartItems = cartItems.filter { it.id != itemToRemove.id } },
                onNavigateBack = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                userId = currentUserId,
                cartItems = cartItems,
                currentBalance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddAddress = { navController.navigate("address_add") },
                onPaymentSuccess = { totalPaid ->
                    currentBalance -= totalPaid
                    cartItems = emptyList()
                    navController.navigate("order_success") { popUpTo("home") }
                }
            )
        }

        composable("order_success") {
            OrderSuccessScreen(
                onBackToHome = { navController.navigate("home") { popUpTo(0) } }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateBack = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
                onNavigateToRegister = { navController.navigate("register/user") { popUpTo("login") { inclusive = true } } },
                onLoginSuccess = { userId, username, role, balance ->
                    isLoggedIn = true
                    currentUserId = userId
                    currentUsername = username
                    currentUserRole = role
                    currentBalance = balance
                    navController.navigate("home") { popUpTo(0) }
                }
            )
        }

        composable("register/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "user"
            RegisterScreen(
                initialRole = role,
                onNavigateBack = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
                onNavigateToLogin = { navController.navigate("login") { popUpTo("register/{role}") { inclusive = true } } }
            )
        }

        composable("client_panel") {
            ClientPanelScreen(
                username = currentUsername,
                balance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUserId = 0L
                    currentUsername = ""
                    currentUserRole = "guest"
                    currentBalance = 0.0
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

        composable("seller_panel") {
            SellerPanelScreen(
                username = currentUsername,
                balance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUserId = 0L
                    currentUsername = ""
                    currentUserRole = "guest"
                    currentBalance = 0.0
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

        composable("admin_panel") {
            AdminPanelScreen(
                username = currentUsername,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    isLoggedIn = false
                    currentUserId = 0L
                    currentUsername = ""
                    currentUserRole = "guest"
                    currentBalance = 0.0
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

        composable("edit_profile/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "client"
            EditProfileScreen(
                userId = currentUserId,
                role = role,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("finance/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "client"
            FinanceScreen(
                userId = currentUserId,
                role = role,
                currentBalance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onBalanceChange = { newBalance -> currentBalance = newBalance }
            )
        }

        composable("admin_dashboard") { AdminDashboardScreen(onBackClick = { navController.popBackStack() }) }

        composable("admin_users") {
            var selectedUser by remember { mutableStateOf<com.example.artsphere.ui.UserInfo?>(null) }
            if (selectedUser == null) {
                AdminUsersScreen(onBackClick = { navController.popBackStack() }, onUserClick = { user -> selectedUser = user })
            } else {
                AdminUserDetailScreen(user = selectedUser!!, onBackClick = { selectedUser = null }, onEditClick = { }, onDeleteClick = { selectedUser = null }, onToggleStatusClick = { selectedUser = selectedUser!!.copy(isActive = !selectedUser!!.isActive) }, onChangeRoleClick = { })
            }
        }

        composable("admin_artworks") {
            var selectedArtwork by remember { mutableStateOf<com.example.artsphere.ui.ArtworkInfo?>(null) }
            if (selectedArtwork == null) {
                AdminArtworksScreen(onBackClick = { navController.popBackStack() }, onArtworkClick = { artwork -> selectedArtwork = artwork })
            } else {
                AdminArtworkDetailScreen(artwork = selectedArtwork!!, onBackClick = { selectedArtwork = null }, onEditClick = { }, onDeleteClick = { selectedArtwork = null }, onChangeStatusClick = { newStatus -> selectedArtwork = selectedArtwork!!.copy(status = newStatus) })
            }
        }

        composable("admin_sellers") {
            var selectedSeller by remember { mutableStateOf<com.example.artsphere.ui.SellerInfo?>(null) }
            if (selectedSeller == null) {
                AdminSellersScreen(onBackClick = { navController.popBackStack() }, onSellerClick = { seller -> selectedSeller = seller })
            } else {
                AdminSellerDetailScreen(seller = selectedSeller!!, onBackClick = { selectedSeller = null }, onEditClick = { }, onDeleteClick = { selectedSeller = null }, onToggleStatusClick = { selectedSeller = selectedSeller!!.copy(isActive = !selectedSeller!!.isActive) }, onToggleVerificationClick = { selectedSeller = selectedSeller!!.copy(isVerified = !selectedSeller!!.isVerified) })
            }
        }

        composable("admin_orders") {
            var selectedOrder by remember { mutableStateOf<com.example.artsphere.ui.OrderInfo?>(null) }
            if (selectedOrder == null) {
                AdminOrdersScreen(onBackClick = { navController.popBackStack() }, onOrderClick = { order -> selectedOrder = order })
            } else {
                AdminOrderDetailScreen(order = selectedOrder!!, onBackClick = { selectedOrder = null }, onChangeStatusClick = { newStatus -> selectedOrder = selectedOrder!!.copy(status = newStatus) }, onCancelOrderClick = { selectedOrder = selectedOrder!!.copy(status = "CANCELLED", paymentStatus = if (selectedOrder!!.paymentStatus == "PAID") "REFUNDED" else "PENDING") }, onSendMessageClick = { })
            }
        }

        composable("admin_categories") {
            var selectedCategory by remember { mutableStateOf<com.example.artsphere.ui.CategoryInfo?>(null) }
            if (selectedCategory == null) {
                AdminCategoriesScreen(onBackClick = { navController.popBackStack() }, onCategoryClick = { category -> selectedCategory = category })
            } else {
                AdminCategoryDetailScreen(category = selectedCategory!!, onBackClick = { selectedCategory = null }, onEditClick = { }, onDeleteClick = { selectedCategory = null }, onToggleStatusClick = { selectedCategory = selectedCategory!!.copy(isActive = !selectedCategory!!.isActive) }, onManageSubcategoriesClick = { })
            }
        }

        composable("seller_dashboard") { SellerDashboardScreen(onBackClick = { navController.popBackStack() }, balance = currentBalance) }
        composable("client_dashboard") {
            ClientDashboardScreen(
                userId = currentUserId,
                onBackClick = { navController.popBackStack() },
                balance = currentBalance
            )
        }
        composable("addresses") { AddressesScreen(userId = currentUserId, isAdmin = false, onNavigateBack = { navController.popBackStack() }, onAddAddress = { navController.navigate("address_add") }, onEditAddress = { addressId -> navController.navigate("address_edit/$addressId") }) }
        composable("addresses_admin") { AddressesScreen(userId = currentUserId, isAdmin = true, onNavigateBack = { navController.popBackStack() }, onAddAddress = { navController.navigate("address_add_admin") }, onEditAddress = { addressId -> navController.navigate("address_edit_admin/$addressId") }) }
        composable("address_add") { AddressFormScreen(userId = currentUserId, addressId = null, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.popBackStack() }) }
        composable("address_edit/{addressId}") { backStackEntry -> val addressId = backStackEntry.arguments?.getString("addressId")?.toLongOrNull(); if (addressId != null) { AddressFormScreen(userId = currentUserId, addressId = addressId, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.popBackStack() }) } }
        composable("address_add_admin") { AddressFormScreen(userId = currentUserId, addressId = null, isAdmin = true, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.popBackStack() }) }
        composable("address_edit_admin/{addressId}") { backStackEntry -> val addressId = backStackEntry.arguments?.getString("addressId")?.toLongOrNull(); if (addressId != null) { AddressFormScreen(userId = currentUserId, addressId = addressId, isAdmin = true, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.popBackStack() }) } }

        composable("seller_artworks") { SellerArtworksScreen(userId = currentUserId, onNavigateBack = { navController.popBackStack() }, onAddArtwork = { navController.navigate("artwork_add") }, onEditArtwork = { artworkId -> navController.navigate("artwork_edit/$artworkId") }) }
        composable("artwork_add") { ArtworkFormScreen(userId = currentUserId, artworkId = null, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.navigate("seller_artworks") { popUpTo("seller_artworks") { inclusive = true } } }) }
        composable("artwork_edit/{artworkId}") { backStackEntry -> val artworkId = backStackEntry.arguments?.getString("artworkId")?.toLongOrNull(); if (artworkId != null) { ArtworkFormScreen(userId = currentUserId, artworkId = artworkId, onNavigateBack = { navController.popBackStack() }, onSuccess = { navController.navigate("seller_artworks") { popUpTo("seller_artworks") { inclusive = true } } }) } }

        composable("client_orders") {
            OrdersScreen(
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtwork = { artworkId -> navController.navigate("public_artwork_detail/$artworkId") }
            )
        }
        composable("client_support") {
            SupportScreen(
                userId = currentUserId,
                currentBalance = currentBalance,
                onBalanceChange = { newBal -> currentBalance = newBal },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("client_followed") {
            FollowedOffersScreen(
                userId = currentUserId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtwork = { artworkId -> navController.navigate("public_artwork_detail/$artworkId") }
            )
        }
        composable("seller_followers") {
            FollowersScreen(
                sellerId = currentUserId, // Zmiana tutaj (przekazanie id)
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("seller_sales_history") { SalesHistoryScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("seller_top_fans") {
            TopFansScreen(
                sellerId = currentUserId, // Przekazanie ID
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}