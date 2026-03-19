package com.example.artsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.artsphere.ui.screens.HomeScreen
import com.example.artsphere.ui.theme.ArtSphereTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSphereTheme {
                HomeScreen(
                    onLoginClick = { /* Przejście do logowania */ },
                    onRegisterClick = { /* Przejście do rejestracji */ },
                    onBrowseClick = { /* Przewinięcie do sekcji wyszukiwania */ },
                    onBecomeSellerClick = { /* Ekran dla sprzedawcy */ }
                )
            }
        }
    }
}