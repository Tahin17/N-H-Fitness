package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.SkinPhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface SkinPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: SkinPhotoEntity): Long

    @Query("SELECT * FROM skin_photos WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): Flow<List<SkinPhotoEntity>>

    @Query("SELECT * FROM skin_photos WHERE user_id = :userId AND date = :date")
    fun getByDate(userId: String, date: Long): Flow<List<SkinPhotoEntity>>

    @Query("SELECT * FROM skin_photos WHERE user_id = :userId AND angle_type = :angleType ORDER BY date DESC")
    fun getByAngle(userId: String, angleType: String): Flow<List<SkinPhotoEntity>>

    @Query("DELETE FROM skin_photos WHERE user_id = :userId AND id = :id")
    suspend fun delete(userId: String, id: Long): Int
}
