package com.yourname.mapscenes.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.yourname.mapscenes.database.converters.ListConverter
import com.yourname.mapscenes.database.dao.SceneDao
import com.yourname.mapscenes.database.dao.UserMarkerDao
import com.yourname.mapscenes.model.SceneEntity
import com.yourname.mapscenes.model.UserMarkerEntity

@Database(
    entities = [SceneEntity::class, UserMarkerEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sceneDao(): SceneDao
    abstract fun userMarkerDao(): UserMarkerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mapscenes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}