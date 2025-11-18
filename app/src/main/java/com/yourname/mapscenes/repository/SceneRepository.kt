package com.yourname.mapscenes.repository

import com.yourname.mapscenes.database.dao.SceneDao
import com.yourname.mapscenes.database.dao.UserMarkerDao
import com.yourname.mapscenes.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SceneRepository(
    private val sceneDao: SceneDao,
    private val userMarkerDao: UserMarkerDao
) {
    // 场景相关 Flow
    val scenes: Flow<List<Scene>> = sceneDao.getAllScenes().map { entities ->
        entities.map { it.toScene() }
    }

    val userMarkers: Flow<List<UserMarker>> = userMarkerDao.getAllMarkers().map { entities ->
        entities.map { it.toUserMarker() }
    }

    // 场景相关方法
    suspend fun getScenes(): List<Scene> {
        return sceneDao.getAllScenes().map { entities ->
            entities.map { it.toScene() }
        }.first()
    }

    suspend fun getSceneById(id: String): Scene? {
        return sceneDao.getSceneById(id)?.toScene()
    }

    suspend fun addScene(scene: Scene) {
        sceneDao.insertScene(scene.toEntity())
    }

    fun getScenesByType(type: SceneType): Flow<List<Scene>> {
        return sceneDao.getScenesByType(type.name).map { entities ->
            entities.map { it.toScene() }
        }
    }

    suspend fun searchScenes(query: String): List<Scene> {
        return sceneDao.searchScenes(query).map { it.toScene() }
    }

    suspend fun toggleFavorite(sceneId: String): Boolean {
        val scene = getSceneById(sceneId)
        scene?.let {
            sceneDao.updateFavoriteStatus(sceneId, !it.isFavorite)
            return !it.isFavorite
        }
        return false
    }

    fun getFavoriteScenes(): Flow<List<Scene>> {
        return sceneDao.getFavoriteScenes().map { entities ->
            entities.map { it.toScene() }
        }
    }

    // 用户标记相关方法
    suspend fun addUserMarker(marker: UserMarker) {
        userMarkerDao.insertMarker(marker.toEntity())
    }

    suspend fun getUserMarkers(): List<UserMarker> {
        return userMarkerDao.getAllMarkers().map { entities ->
            entities.map { it.toUserMarker() }
        }.first()
    }

    suspend fun deleteUserMarker(markerId: String): Boolean {
        userMarkerDao.deleteMarker(markerId)
        return true
    }

    suspend fun updateUserMarker(marker: UserMarker): Boolean {
        userMarkerDao.updateMarker(marker.toEntity())
        return true
    }

    fun getUserMarkersByType(type: MarkerType): Flow<List<UserMarker>> {
        return userMarkerDao.getMarkersByType(type.name).map { entities ->
            entities.map { it.toUserMarker() }
        }
    }

    // 初始化示例数据
    suspend fun initializeSampleData() {
        if (getScenes().isEmpty()) {
            val sampleScenes = listOf(
                Scene(
                    id = "1",
                    name = "天安门广场",
                    description = "中国北京天安门广场",
                    detailedDescription = "天安门广场位于北京市中心，是世界上最大的城市广场之一。它见证了中国的许多重大历史事件，是中国的象征之一。",
                    latitude = 39.9042,
                    longitude = 116.4074,
                    type = SceneType.HISTORICAL,
                    rating = 4.8f,
                    address = "北京市东城区东长安街",
                    openingHours = "全天开放",
                    ticketPrice = "免费",
                    tags = listOf("历史", "政治", "广场"),
                    visitCount = 15000
                ),
                Scene(
                    id = "2",
                    name = "故宫博物院",
                    description = "明清两代的皇家宫殿",
                    detailedDescription = "故宫又称紫禁城，是中国明清两代的皇家宫殿，是世界上现存规模最大、保存最为完整的木质结构古建筑群之一。",
                    latitude = 39.9163,
                    longitude = 116.3972,
                    type = SceneType.HISTORICAL,
                    rating = 4.9f,
                    address = "北京市东城区景山前街4号",
                    openingHours = "08:30-17:00",
                    ticketPrice = "60元",
                    tags = listOf("博物馆", "古建筑", "皇家"),
                    visitCount = 12000
                ),
                Scene(
                    id = "3",
                    name = "颐和园",
                    description = "清代皇家园林",
                    detailedDescription = "颐和园是中国清朝时期皇家园林，以昆明湖、万寿山为基址，以杭州西湖为蓝本，汲取江南园林的设计手法而建成的一座大型山水园林。",
                    latitude = 39.9999,
                    longitude = 116.2735,
                    type = SceneType.NATURAL,
                    rating = 4.7f,
                    address = "北京市海淀区新建宫门路19号",
                    openingHours = "06:30-18:00",
                    ticketPrice = "30元",
                    tags = listOf("园林", "湖泊", "皇家"),
                    visitCount = 8000
                ),
                Scene(
                    id = "4",
                    name = "798艺术区",
                    description = "现代艺术聚集地",
                    detailedDescription = "798艺术区是北京的文化创意产业集聚区，原为国营798厂等电子工业的老厂区所在地，如今已成为画廊、艺术中心、艺术家工作室、设计公司等各种空间的聚合。",
                    latitude = 39.9834,
                    longitude = 116.4951,
                    type = SceneType.CULTURAL,
                    rating = 4.5f,
                    address = "北京市朝阳区酒仙桥路4号798艺术区",
                    openingHours = "10:00-18:00",
                    ticketPrice = "免费",
                    tags = listOf("艺术", "创意", "展览"),
                    visitCount = 5000
                )
            )
            sceneDao.insertAllScenes(sampleScenes.map { it.toEntity() })
        }
    }
}

// 扩展函数：实体与领域模型转换
private fun SceneEntity.toScene(): Scene {
    return Scene(
        id = id,
        name = name,
        description = description,
        detailedDescription = detailedDescription,
        latitude = latitude,
        longitude = longitude,
        type = SceneType.valueOf(type),
        rating = rating,
        imageUrl = imageUrl,
        address = address,
        openingHours = openingHours,
        ticketPrice = ticketPrice,
        contactPhone = contactPhone,
        website = website,
        tags = Json.decodeFromString(tags),
        isFavorite = isFavorite,
        visitCount = visitCount,
        lastVisited = lastVisited
    )
}

private fun Scene.toEntity(): SceneEntity {
    return SceneEntity(
        id = id,
        name = name,
        description = description,
        detailedDescription = detailedDescription,
        latitude = latitude,
        longitude = longitude,
        type = type.name,
        rating = rating,
        imageUrl = imageUrl,
        address = address,
        openingHours = openingHours,
        ticketPrice = ticketPrice,
        contactPhone = contactPhone,
        website = website,
        tags = Json.encodeToString(tags),
        isFavorite = isFavorite,
        visitCount = visitCount,
        lastVisited = lastVisited
    )
}

private fun UserMarkerEntity.toUserMarker(): UserMarker {
    return UserMarker(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        markerType = MarkerType.valueOf(markerType),
        createdAt = createdAt,
        tags = Json.decodeFromString(tags),
        color = color
    )
}

private fun UserMarker.toEntity(): UserMarkerEntity {
    return UserMarkerEntity(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        markerType = markerType.name,
        createdAt = createdAt,
        tags = Json.encodeToString(tags),
        color = color
    )
}