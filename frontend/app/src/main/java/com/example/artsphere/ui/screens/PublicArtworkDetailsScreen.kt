package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicArtworkDetailScreen(
    artworkId: Long,
    currentUserId: Long,
    isLoggedIn: Boolean,
    role: String,
    cartItems: List<ArtworkResponse>,
    onAddToCart: (ArtworkResponse) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var artwork by remember { mutableStateOf<ArtworkResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Status obserwowanego
    var isFollowing by remember { mutableStateOf(false) }
    var isFollowingLoading by remember { mutableStateOf(false) }

    val isInCart = cartItems.any { it.id == artworkId }

    LaunchedEffect(artworkId, currentUserId) {
        try {
            val response = RetrofitClient.artworkApi.getArtworkById(artworkId)
            if (response.isSuccessful && response.body() != null) {
                val art = response.body()!!
                artwork = art

                // Jeśli zalogowany, sprawdź czy obserwuje TEGO sprzedawcę
                if (isLoggedIn && currentUserId > 0 && art.userId > 0) {
                    try {
                        val followRes = RetrofitClient.authApi.checkFollow(currentUserId, art.userId)
                        if (followRes.isSuccessful && followRes.body() != null) {
                            isFollowing = followRes.body()!!["isFollowing"] == true
                        }
                    } catch (e: Exception) { }
                }
            }
        } catch (e: Exception) { } finally { isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły dzieła") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Wróć") } }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (artwork == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nie znaleziono dzieła.") }
        } else {
            val art = artwork!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (art.imagePath.isNullOrBlank()) {
                        Icon(Icons.Default.Image, "Zdjęcie", modifier = Modifier.size(64.dp), tint = Color.Gray)
                    } else {
                        val displayImage = if (art.imagePath.startsWith("uploads/")) 
                            RetrofitClient.BASE_URL + art.imagePath 
                        else art.imagePath

                        AsyncImage(
                            model = displayImage,
                            contentDescription = art.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }

                Text(text = art.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)

                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                    Text("Kategoria: ${art.categoryName ?: "Inne"}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }

                Divider()

                // --- SEKCJA AUTORA Z PRZYCISKIEM OBSERWUJ ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Autor:", fontSize = 14.sp, color = Color.Gray)
                        Text(text = art.artist ?: art.userUsername, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    if (isLoggedIn && role != "guest" && currentUserId != art.userId) {
                        OutlinedButton(
                            onClick = {
                                isFollowingLoading = true
                                coroutineScope.launch {
                                    try {
                                        if (isFollowing) {
                                            val res = RetrofitClient.authApi.unfollowSeller(currentUserId, art.userId)
                                            if (res.isSuccessful) {
                                                isFollowing = false
                                                Toast.makeText(context, "Odobserwowano artystę", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            val res = RetrofitClient.authApi.followSeller(currentUserId, art.userId)
                                            if (res.isSuccessful) {
                                                isFollowing = true
                                                Toast.makeText(context, "Zaczęto obserwować", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Błąd z siecią.", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isFollowingLoading = false
                                    }
                                }
                            },
                            enabled = !isFollowingLoading
                        ) {
                            if (isFollowingLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else if (isFollowing) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Obserwujesz")
                            } else {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Obserwuj")
                            }
                        }
                    }
                }

                Text(text = "Opis:", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                Text(text = art.description ?: "Brak opisu", fontSize = 16.sp, lineHeight = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                Text("Cena:", color = Color.Gray, fontSize = 14.sp)
                if (art.isPriceless) {
                    Text("Bezcenny", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("${String.format("%.2f", art.price ?: 0.0)} zł", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LOGIKA PRZYCISKÓW KOSZYKA
                if (!isLoggedIn || role == "guest") {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Zaloguj się, aby dodać do koszyka i kupić to dzieło.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                            Button(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) { Text("Zaloguj się") }
                        }
                    }
                } else if (role == "BUYER" || role == "user") {
                    if (art.isSold) {
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = false, shape = RoundedCornerShape(12.dp)) {
                            Text("Sprzedane", fontSize = 18.sp)
                        }
                    } else if (isInCart) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onNavigateToCart,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Produkt w koszyku - Przejdź", fontSize = 18.sp)
                            }
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Kontynuuj przeglądanie", fontSize = 18.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                onAddToCart(art)
                                Toast.makeText(context, "Dodano do koszyka!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dodaj do koszyka", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}