package dev.cprn007.yaof.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.cprn007.yaof.data.entity.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM tracked_apps ORDER BY addedAt DESC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntity)

    @Delete
    suspend fun deleteApp(app: AppEntity)

    @Query("SELECT * FROM tracked_apps WHERE ownerRepo = :id")
    suspend fun getApp(id: String): AppEntity?
}
