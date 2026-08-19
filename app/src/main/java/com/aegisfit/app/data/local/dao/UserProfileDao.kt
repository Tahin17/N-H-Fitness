package com.aegisfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aegisfit.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity): Long

    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfileOnce(userId: String): UserProfileEntity?

    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteProfile(userId: String): Int
}
