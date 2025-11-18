package com.yourname.mapscenes.model

import java.util.UUID

data class UserMarker(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val markerType: MarkerType = MarkerType.PERSONAL,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(),
    val color: String = "#2196F3" // 默认蓝色
)

enum class MarkerType(val displayName: String) {
    PERSONAL("自然风光"),
    FAVORITE("历史古迹"),
    PLAN("探险挑战"),
    VISITED("美食探店")
}