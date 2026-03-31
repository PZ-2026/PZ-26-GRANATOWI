package com.example.artsphere.ui

import androidx.compose.runtime.mutableStateListOf
import com.example.artsphere.api.ArtworkResponse

object CartManager {
    val items = mutableStateListOf<ArtworkResponse>()

    fun addToCart(artwork: ArtworkResponse) {
        // Sprawdzamy czy nie ma już go w koszyku
        if (items.none { it.id == artwork.id }) {
            items.add(artwork)
        }
    }

    fun removeFromCart(artworkId: Long) {
        items.removeAll { it.id == artworkId }
    }

    fun clearCart() {
        items.clear()
    }

    fun getTotalPrice(): Double {
        return items.sumOf { it.price ?: 0.0 }
    }
}