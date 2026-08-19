package com.aegisfit.app.data.repository

import com.aegisfit.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val authState: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val normalizedEmail = email.trim()
            require(normalizedEmail.length in 3..254 && password.isNotEmpty() && password.length <= 128) {
                "Invalid credentials."
            }
            firebaseAuth.signInWithEmailAndPassword(normalizedEmail, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val normalizedEmail = email.trim()
            require(normalizedEmail.length in 3..254 && password.length in 8..128 &&
                password.any(Char::isLetter) && password.any(Char::isDigit)
            ) { "Invalid account details." }
            firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            val normalizedEmail = email.trim()
            require(normalizedEmail.length in 3..254) { "Invalid email address." }
            firebaseAuth.sendPasswordResetEmail(normalizedEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
