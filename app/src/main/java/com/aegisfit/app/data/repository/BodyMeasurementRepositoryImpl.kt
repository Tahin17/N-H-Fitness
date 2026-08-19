package com.aegisfit.app.data.repository

import com.aegisfit.app.data.local.dao.BodyMeasurementDao
import com.aegisfit.app.data.mapper.BodyMeasurementMapper.toDomain
import com.aegisfit.app.data.mapper.BodyMeasurementMapper.toEntity
import com.aegisfit.app.domain.model.BodyMeasurement
import com.aegisfit.app.domain.repository.BodyMeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyMeasurementRepositoryImpl @Inject constructor(
    private val dao: BodyMeasurementDao
) : BodyMeasurementRepository {

    override fun getAllMeasurements(userId: String): Flow<List<BodyMeasurement>> {
        return dao.getAll(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getLatestMeasurement(userId: String): Flow<BodyMeasurement?> {
        return dao.getLatest(userId).map { it?.toDomain() }
    }

    override suspend fun getMeasurementByDate(userId: String, date: Long): BodyMeasurement? {
        return dao.getByDate(userId, date)?.toDomain()
    }

    override suspend fun saveMeasurement(measurement: BodyMeasurement) {
        require(measurement.userId.isNotBlank()) { "A signed-in user is required." }
        val values = listOfNotNull(
            measurement.chestCm, measurement.waistCm, measurement.hipsCm, measurement.neckCm,
            measurement.leftBicepCm, measurement.rightBicepCm,
            measurement.leftForearmCm, measurement.rightForearmCm,
            measurement.leftQuadCm, measurement.rightQuadCm,
            measurement.leftCalfCm, measurement.rightCalfCm
        )
        require(values.isNotEmpty() && values.all { it.isFinite() && it in 5.0..300.0 }) {
            "Enter at least one measurement between 5 and 300 cm."
        }
        dao.insert(measurement.toEntity())
    }

    override suspend fun deleteMeasurement(userId: String, id: Long) {
        if (userId.isNotBlank() && id > 0) dao.deleteById(userId, id)
    }
}
