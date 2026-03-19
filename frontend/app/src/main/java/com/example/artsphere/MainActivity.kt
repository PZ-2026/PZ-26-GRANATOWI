package com.example.artsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.artsphere.ui.navigation.AppNavigation
import com.example.artsphere.ui.theme.ArtSphereTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSphereTheme {
                // AppNavigation zarządza tym co wyświetla się na ekranie
                AppNavigation()
            }
        }
    }
}