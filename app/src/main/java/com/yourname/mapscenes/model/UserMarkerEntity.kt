package com.yourname.mapscenes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_markers")
data class UserMarkerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val markerType: String, // 存储 MarkerType 的枚举值
    val createdAt: Long,
    val tags: String, // JSON 字符串存储列表
    val color: String
)