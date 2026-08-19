package com.aegisfit.app.domain.repository

interface DataSyncRepository {
    suspend fun pushAllLocalData(userId: String): Result<Unit>
    suspend fun pullAllCloudData(userId: String): Result<Unit>
    suspend fun syncDirtyData(userId: String): Result<Unit>
    suspend fun uploadLog(userId: String, logType: String, logData: Map<String, Any>, date: Long, id: String): Result<Unit>
}
