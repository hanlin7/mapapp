package com.yourname.mapscenes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val latitude: Double,
    val longitude: Double,
    val type: String, // 存储 SceneType 的枚举值
    val rating: Float,
    val imageUrl: String?,
    val address: String,
    val openingHours: String,
    val ticketPrice: String,
    val contactPhone: String,
    val website: String,
    val tags: String, // JSON 字符串存储列表
    val isFavorite: Boolean,
    val visitCount: Int,
    val lastVisited: Long
)