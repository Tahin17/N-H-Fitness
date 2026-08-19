package com.aegisfit.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.DataSyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataSyncRepository: DataSyncRepository,
    private val authRepository: AuthRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Being signed out is a valid idle state, not a failed background job.
        val userId = authRepository.currentUserId ?: return Result.success()
        
        return try {
            val result = dataSyncRepository.syncDirtyData(userId)
            if (result.isSuccess) {
                Result.success()
            } else if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRY_ATTEMPTS) Result.retry() else Result.failure()
        }
    }

    private companion object {
        const val MAX_RETRY_ATTEMPTS = 3
    }
}
