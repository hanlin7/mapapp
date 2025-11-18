package com.yourname.mapscenes.model

import java.util.UUID

data class Scene(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val detailedDescription: String = "",
    val latitude: Double,
    val longitude: Double,
    val type: SceneType,
    val rating: Float,
    val imageUrl: String? = null,
    val address: String = "",
    val openingHours: String = "",
    val ticketPrice: String = "",
    val contactPhone: String = "",
    val website: String = "",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val visitCount: Int = 0,
    val lastVisited: Long = 0L
)