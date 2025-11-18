package com.yourname.mapscenes.database.dao

import androidx.room.*
import com.yourname.mapscenes.model.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes")
    fun getAllScenes(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getSceneById(id: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllScenes(scenes: List<SceneEntity>)

    @Query("SELECT * FROM scenes WHERE type = :type")
    fun getScenesByType(type: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun searchScenes(query: String): List<SceneEntity>

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Query("UPDATE scenes SET isFavorite = :isFavorite WHERE id = :sceneId")
    suspend fun updateFavoriteStatus(sceneId: String, isFavorite: Boolean)

    @Query("SELECT * FROM scenes WHERE isFavorite = 1")
    fun getFavoriteScenes(): Flow<List<SceneEntity>>
}