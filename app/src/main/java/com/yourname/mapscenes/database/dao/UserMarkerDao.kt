package com.yourname.mapscenes.database.dao

import androidx.room.*
import com.yourname.mapscenes.model.UserMarkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMarkerDao {
    @Query("SELECT * FROM user_markers")
    fun getAllMarkers(): Flow<List<UserMarkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: UserMarkerEntity)

    @Query("DELETE FROM user_markers WHERE id = :markerId")
    suspend fun deleteMarker(markerId: String)

    @Update
    suspend fun updateMarker(marker: UserMarkerEntity)

    @Query("SELECT * FROM user_markers WHERE markerType = :type")
    fun getMarkersByType(type: String): Flow<List<UserMarkerEntity>>
}