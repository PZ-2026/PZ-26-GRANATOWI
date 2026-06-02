package com.example.artsphere.ui.navigation

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artsphere.api.*
import com.example.artsphere.ui.UserInfo
import com.example.artsphere.ui.screens.*
import com.example.artsphere.ui.screens.Client.FollowedOffersScreen
import com.example.artsphere.ui.screens.Client.OrdersScreen
import com.example.artsphere.ui.screens.Client.SupportScreen
import com.example.artsphere.ui.screens.Seller.FollowersScreen
import com.example.artsphere.ui.screens.Seller.SalesHistoryScreen
import com.example.artsphere.ui.screens.Seller.TopFansScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Globalny stan użytkownika
    var isLoggedIn by remember { mutableStateOf(TokenManager.isLoggedIn) }
    var currentUserId by remember { mutableStateOf(TokenManager.userId) }
    var currentUsername by remember { mutableStateOf(TokenManager.username ?: "") }
    var currentBalance by remember { mutableStateOf(TokenManager.balance) }
    var currentUserRole by remember { mutableStateOf(TokenManager.role ?: "guest") }

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
                    TokenManager.clear()
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
                    TokenManager.balance = currentBalance
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
                    // Stan lokalny
                    isLoggedIn = true
                    currentUserId = userId
                    currentUsername = username
                    currentUserRole = role
                    currentBalance = balance
                    
                    // TokenManager jest już zaktualizowany wewnątrz LoginScreen.kt przed tym wywołaniem,
                    // ale dla pewności możemy zsynchronizować stan tutaj jeśli zajdzie potrzeba.
                    
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
                    TokenManager.clear()
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
                    TokenManager.clear()
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
                    TokenManager.clear()
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
                onSaveSuccess = { newName ->
                    currentUsername = newName
                    TokenManager.username = newName
                    navController.popBackStack()
                }
            )
        }

        composable("finance/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "client"
            FinanceScreen(
                userId = currentUserId,
                role = role,
                currentBalance = currentBalance,
                onNavigateBack = { navController.popBackStack() },
                onBalanceChange = { newBalance -> 
                    currentBalance = newBalance
                    TokenManager.balance = newBalance
                }
            )
        }

        composable("admin_dashboard") { AdminDashboardScreen(onBackClick = { navController.popBackStack() }) }

        composable("admin_users") {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var refreshTrigger by remember { mutableIntStateOf(0) }
            var selectedUser by remember { mutableStateOf<UserInfo?>(null) }
            var isEditingUser by remember { mutableStateOf(false) }
            val alpha by animateFloatAsState(if (selectedUser == null && !isEditingUser) 1f else 0f, label = "screen_alpha")
            val detailAlpha by animateFloatAsState(if (selectedUser != null && !isEditingUser) 1f else 0f, label = "detail_alpha")
            val editAlpha by animateFloatAsState(if (isEditingUser) 1f else 0f, label = "edit_alpha")

            BackHandler(enabled = selectedUser != null && !isEditingUser) {
                selectedUser = null
            }

            BackHandler(enabled = isEditingUser) {
                isEditingUser = false
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedUser == null && !isEditingUser) {
                    Box(modifier = Modifier.fillMaxSize().alpha(alpha)) {
                        AdminUsersScreen(
                            refreshTrigger = refreshTrigger,
                            onBackClick = { navController.popBackStack() },
                            onUserClick = { user -> selectedUser = user }
                        )
                    }
                }

                if (selectedUser != null && !isEditingUser) {
                    Box(modifier = Modifier.fillMaxSize().alpha(detailAlpha)) {
                        AdminUserDetailScreen(
                            user = selectedUser!!,
                            onBackClick = { selectedUser = null },
                            onEditClick = { isEditingUser = true },
                            onDeleteClick = {
                                val current = selectedUser
                                if (current != null) {
                                    coroutineScope.launch {
                                        try {
                                            val response = RetrofitClient.adminApi.deleteUser(current.id)
                                            if (response.isSuccessful) {
                                                selectedUser = null
                                                refreshTrigger++
                                                Toast.makeText(context, "Użytkownik został usunięty", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Log.e("AdminUsers", "Delete user failed: ${response.code()}")
                                                Toast.makeText(context, "Nie udało się usunąć użytkownika", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("AdminUsers", "Delete user exception: ${e.message}", e)
                                            Toast.makeText(context, "Błąd usuwania użytkownika", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onToggleStatusClick = {
                                val current = selectedUser
                                if (current != null) {
                                    coroutineScope.launch {
                                        try {
                                            val response = RetrofitClient.adminApi.updateUserStatus(
                                                current.id,
                                                UpdateUserStatusRequest(active = !current.isActive)
                                            )
                                            if (response.isSuccessful && response.body() != null) {
                                                val updated = response.body()!!
                                                selectedUser = current.copy(
                                                    role = normalizeRoleForApi(updated.role),
                                                    isActive = updated.active ?: true
                                                )
                                                refreshTrigger++
                                                Toast.makeText(context, if (selectedUser?.isActive == true) "Konto aktywowane" else "Konto dezaktywowane", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Log.e("AdminUsers", "Update status failed: ${response.code()}")
                                                Toast.makeText(context, "Nie udało się zmienić statusu konta", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("AdminUsers", "Update status exception: ${e.message}", e)
                                            Toast.makeText(context, "Błąd zmiany statusu konta", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onChangeRoleClick = { role ->
                                val current = selectedUser
                                if (current != null) {
                                    coroutineScope.launch {
                                        try {
                                            val response = RetrofitClient.adminApi.updateUserRole(
                                                current.id,
                                                UpdateUserRoleRequest(role = role)
                                            )
                                            if (response.isSuccessful && response.body() != null) {
                                                val updated = response.body()!!
                                                selectedUser = current.copy(
                                                    role = normalizeRoleForApi(updated.role),
                                                    isActive = updated.active ?: true
                                                )
                                                refreshTrigger++
                                                Toast.makeText(context, "Rola została zaktualizowana", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Log.e("AdminUsers", "Update role failed: ${response.code()}")
                                                Toast.makeText(context, "Nie udało się zmienić roli", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("AdminUsers", "Update role exception: ${e.message}", e)
                                            Toast.makeText(context, "Błąd zmiany roli", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (isEditingUser) {
                    Box(modifier = Modifier.fillMaxSize().alpha(editAlpha)) {
                        EditProfileScreen(
                            userId = selectedUser!!.id,
                            role = normalizeRoleForApi(selectedUser!!.role).lowercase(),
                            onNavigateBack = { isEditingUser = false },
                            onSaveSuccess = {
                                coroutineScope.launch {
                                    try {
                                        val response = RetrofitClient.adminApi.getAllUsers()
                                        val refreshed = response.body()?.firstOrNull { it.id == selectedUser?.id }
                                        if (response.isSuccessful && refreshed != null) {
                                            selectedUser = refreshed.toUserInfo()
                                            refreshTrigger++
                                            Toast.makeText(context, "Dane użytkownika zapisane", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Nie udało się odświeżyć danych", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("AdminUsers", "Refresh after edit exception: ${e.message}", e)
                                        Toast.makeText(context, "Błąd połączenia podczas odświeżania", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isEditingUser = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        composable("admin_artworks") {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var refreshTrigger by remember { mutableIntStateOf(0) }
            var selectedArtwork by remember { mutableStateOf<com.example.artsphere.ui.ArtworkInfo?>(null) }
            var isEditingArtwork by remember { mutableStateOf(false) }

            BackHandler(enabled = selectedArtwork != null && !isEditingArtwork) {
                selectedArtwork = null
            }

            BackHandler(enabled = isEditingArtwork) {
                isEditingArtwork = false
            }

            if (selectedArtwork == null && !isEditingArtwork) {
                AdminArtworksScreen(
                    refreshTrigger = refreshTrigger,
                    onBackClick = { navController.popBackStack() },
                    onArtworkClick = { artwork -> selectedArtwork = artwork }
                )
            } else if (selectedArtwork != null && !isEditingArtwork) {
                AdminArtworkDetailScreen(
                    artwork = selectedArtwork!!,
                    onBackClick = { selectedArtwork = null },
                    onEditClick = { isEditingArtwork = true },
                    onDeleteClick = {
                        val current = selectedArtwork
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.deleteArtwork(current.id)
                                    if (response.isSuccessful) {
                                        selectedArtwork = null
                                        refreshTrigger++
                                        Toast.makeText(context, "Dzieło usunięte", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd usuwania", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onChangeStatusClick = { newStatus ->
                        val current = selectedArtwork
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.updateArtworkStatus(
                                        current.id,
                                        mapOf("status" to newStatus)
                                    )
                                    if (response.isSuccessful) {
                                        selectedArtwork = current.copy(status = newStatus)
                                        refreshTrigger++
                                        Toast.makeText(context, "Status zaktualizowany", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd zmiany statusu", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            } else if (isEditingArtwork) {
                ArtworkFormScreen(
                    userId = selectedArtwork!!.sellerId,
                    artworkId = selectedArtwork!!.id,
                    onNavigateBack = { isEditingArtwork = false },
                    onSuccess = {
                        isEditingArtwork = false
                        refreshTrigger++
                        selectedArtwork = null
                        Toast.makeText(context, "Dzieło zaktualizowane", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        composable("admin_sellers") {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var refreshTrigger by remember { mutableIntStateOf(0) }
            var selectedSeller by remember { mutableStateOf<com.example.artsphere.ui.SellerInfo?>(null) }
            var isEditingSeller by remember { mutableStateOf(false) }

            BackHandler(enabled = selectedSeller != null && !isEditingSeller) {
                selectedSeller = null
            }

            BackHandler(enabled = isEditingSeller) {
                isEditingSeller = false
            }

            if (selectedSeller == null && !isEditingSeller) {
                AdminSellersScreen(
                    refreshTrigger = refreshTrigger,
                    onBackClick = { navController.popBackStack() },
                    onSellerClick = { seller -> selectedSeller = seller }
                )
            } else if (selectedSeller != null && !isEditingSeller) {
                AdminSellerDetailScreen(
                    seller = selectedSeller!!,
                    onBackClick = { selectedSeller = null },
                    onEditClick = { isEditingSeller = true },
                    onDeleteClick = {
                        val current = selectedSeller
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.deleteUser(current.id)
                                    if (response.isSuccessful) {
                                        selectedSeller = null
                                        refreshTrigger++
                                        Toast.makeText(context, "Sprzedawca został usunięty", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd usuwania", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onToggleStatusClick = {
                        val current = selectedSeller
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.updateUserStatus(
                                        current.id,
                                        UpdateUserStatusRequest(active = !current.isActive)
                                    )
                                    if (response.isSuccessful && response.body() != null) {
                                        val updated = response.body()!!
                                        selectedSeller = current.copy(isActive = updated.active ?: true)
                                        refreshTrigger++
                                        Toast.makeText(context, "Status zmieniony", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd zmiany statusu", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onToggleVerificationClick = {
                        val current = selectedSeller
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.verifySeller(
                                        current.id,
                                        UpdateUserVerificationRequest(verified = !current.isVerified)
                                    )
                                    if (response.isSuccessful && response.body() != null) {
                                        val updated = response.body()!!
                                        selectedSeller = current.copy(isVerified = updated.verified ?: false)
                                        refreshTrigger++
                                        Toast.makeText(context, if (selectedSeller!!.isVerified) "Zweryfikowano" else "Cofnięto weryfikację", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd weryfikacji", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            } else if (isEditingSeller) {
                EditProfileScreen(
                    userId = selectedSeller!!.id,
                    role = "seller",
                    onNavigateBack = { isEditingSeller = false },
                    onSaveSuccess = {
                        refreshTrigger++
                        isEditingSeller = false
                        selectedSeller = null // Refresh from list
                    }
                )
            }
        }

        composable("admin_orders") {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var refreshTrigger by remember { mutableIntStateOf(0) }
            var selectedOrder by remember { mutableStateOf<com.example.artsphere.ui.OrderInfo?>(null) }
            if (selectedOrder == null) {
                AdminOrdersScreen(
                    refreshTrigger = refreshTrigger,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { order -> selectedOrder = order }
                )
            } else {
                AdminOrderDetailScreen(
                    order = selectedOrder!!,
                    onBackClick = { selectedOrder = null },
                    onChangeStatusClick = { newStatus ->
                        val current = selectedOrder
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.updateOrderStatus(
                                        current.id,
                                        mapOf("status" to newStatus)
                                    )
                                    if (response.isSuccessful) {
                                        val allOrders = RetrofitClient.adminApi.getAllOrders().body()
                                        val refreshed = allOrders
                                            ?.firstOrNull { it.id == current.id && it.artworkId == current.artworkId }
                                            ?: allOrders?.firstOrNull { it.id == current.id }
                                        selectedOrder = refreshed ?: current.copy(status = newStatus)
                                        refreshTrigger++
                                        Toast.makeText(context, "Status zamówienia zaktualizowany", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Nie udało się zaktualizować statusu", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd zmiany statusu", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onCancelOrderClick = {
                        val current = selectedOrder
                        if (current != null) {
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.adminApi.cancelOrder(current.id)
                                    if (response.isSuccessful) {
                                        val allOrders = RetrofitClient.adminApi.getAllOrders().body()
                                        val refreshed = allOrders
                                            ?.firstOrNull { it.id == current.id && it.artworkId == current.artworkId }
                                            ?: allOrders?.firstOrNull { it.id == current.id }
                                        selectedOrder = refreshed ?: current.copy(status = "CANCELLED", paymentStatus = "REFUNDED")
                                        refreshTrigger++
                                        Toast.makeText(context, "Zamówienie anulowane", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Nie udało się anulować zamówienia", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Błąd anulowania zamówienia", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
        }

        composable("admin_categories") {
            var refreshTrigger by remember { mutableIntStateOf(0) }
            var selectedCategory by remember { mutableStateOf<com.example.artsphere.api.CategoryBackendResponse?>(null) }

            BackHandler(enabled = selectedCategory != null) {
                selectedCategory = null
            }

            if (selectedCategory == null) {
                AdminCategoriesScreen(
                    refreshTrigger = refreshTrigger,
                    onBackClick = { navController.popBackStack() },
                    onCategoryClick = { info -> selectedCategory = info }
                )
            } else {
                AdminCategoryDetailScreen(
                    category = selectedCategory!!,
                    onBack = { selectedCategory = null },
                    onRefresh = {
                        refreshTrigger++
                        selectedCategory = null
                    }
                )
            }
        }

        composable("seller_dashboard") {
            SellerDashboardScreen(
                userId = currentUserId, // ZMIANA TUTAJ
                onBackClick = { navController.popBackStack() },
                balance = currentBalance
            )
        }
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
        composable("seller_sales_history") {
            SalesHistoryScreen(
                sellerId = currentUserId, // ZMIANA
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("seller_top_fans") {
            TopFansScreen(
                sellerId = currentUserId, // Przekazanie ID
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun normalizeRoleForApi(role: String): String {
    return if (role.equals("SELLER", ignoreCase = true)) "ARTIST" else role.uppercase()
}

private fun AdminUserResponse.toUserInfo(): UserInfo {
    return UserInfo(
        id = id,
        username = username,
        email = email,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        role = normalizeRoleForApi(role),
        balance = balance,
        registrationDate = createdAt?.replace("T", " ")?.substringBefore(".") ?: "Brak danych",
        isActive = active ?: true,
        lastLogin = null
    )
}
