package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface BodyMeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: BodyMeasurementEntity): Long

    @Query("SELECT * FROM body_measurements WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements WHERE user_id = :userId AND date = :date LIMIT 1")
    suspend fun getByDate(userId: String, date: Long): BodyMeasurementEntity?

    @Query("SELECT * FROM body_measurements WHERE user_id = :userId ORDER BY date DESC LIMIT 1")
    fun getLatest(userId: String): Flow<BodyMeasurementEntity?>

    @Query("DELETE FROM body_measurements WHERE user_id = :userId AND id = :id")
    suspend fun deleteById(userId: String, id: Long): Int
}
