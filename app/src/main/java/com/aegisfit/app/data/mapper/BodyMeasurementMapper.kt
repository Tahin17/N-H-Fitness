package com.aegisfit.app.data.mapper

import com.aegisfit.app.data.local.entity.BodyMeasurementEntity
import com.aegisfit.app.domain.model.BodyMeasurement

object BodyMeasurementMapper {
    fun BodyMeasurementEntity.toDomain(): BodyMeasurement {
        return BodyMeasurement(
            id = this.id,
            userId = this.userId,
            date = this.date,
            chestCm = this.chestCm,
            waistCm = this.waistCm,
            hipsCm = this.hipsCm,
            neckCm = this.neckCm,
            leftBicepCm = this.leftBicepCm,
            rightBicepCm = this.rightBicepCm,
            leftForearmCm = this.leftForearmCm,
            rightForearmCm = this.rightForearmCm,
            leftQuadCm = this.leftQuadCm,
            rightQuadCm = this.rightQuadCm,
            leftCalfCm = this.leftCalfCm,
            rightCalfCm = this.rightCalfCm
        )
    }

    fun BodyMeasurement.toEntity(): BodyMeasurementEntity {
        return BodyMeasurementEntity(
            id = this.id,
            userId = this.userId,
            date = this.date,
            chestCm = this.chestCm,
            waistCm = this.waistCm,
            hipsCm = this.hipsCm,
            neckCm = this.neckCm,
            leftBicepCm = this.leftBicepCm,
            rightBicepCm = this.rightBicepCm,
            leftForearmCm = this.leftForearmCm,
            rightForearmCm = this.rightForearmCm,
            leftQuadCm = this.leftQuadCm,
            rightQuadCm = this.rightQuadCm,
            leftCalfCm = this.leftCalfCm,
            rightCalfCm = this.rightCalfCm
        )
    }
}
