package com.aegisfit.app.domain.repository

import com.aegisfit.app.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepository {
    fun getAllMeasurements(userId: String): Flow<List<BodyMeasurement>>
    fun getLatestMeasurement(userId: String): Flow<BodyMeasurement?>
    suspend fun getMeasurementByDate(userId: String, date: Long): BodyMeasurement?
    suspend fun saveMeasurement(measurement: BodyMeasurement)
    suspend fun deleteMeasurement(userId: String, id: Long)
}
