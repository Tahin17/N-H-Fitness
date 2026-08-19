package com.aegisfit.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val authState: Flow<String?>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut()
}
