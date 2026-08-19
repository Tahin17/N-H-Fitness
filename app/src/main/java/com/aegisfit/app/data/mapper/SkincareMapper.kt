package com.aegisfit.app.data.mapper

import com.aegisfit.app.data.local.entity.SkinPhotoEntity
import com.aegisfit.app.data.local.entity.SkincareLogEntity
import com.aegisfit.app.data.local.entity.SkincareRoutineEntity
import com.aegisfit.app.domain.model.SkinPhoto
import com.aegisfit.app.domain.model.SkincareLog
import com.aegisfit.app.domain.model.SkincareRoutine

fun SkincareRoutineEntity.toDomainModel(): SkincareRoutine {
    return SkincareRoutine(
        id = this.id,
        stepOrder = this.stepOrder,
        routineType = this.routineType,
        productName = this.productName,
        productCategory = this.productCategory,
        activeIngredient = this.activeIngredient,
        instructions = this.instructions,
        dosage = this.dosage,
        warning = this.warning,
        alternateGroup = this.alternateGroup
    )
}

fun SkincareRoutine.toEntity(): SkincareRoutineEntity {
    return SkincareRoutineEntity(
        id = this.id,
        stepOrder = this.stepOrder,
        routineType = this.routineType,
        productName = this.productName,
        productCategory = this.productCategory,
        activeIngredient = this.activeIngredient,
        instructions = this.instructions,
        dosage = this.dosage,
        warning = this.warning,
        alternateGroup = this.alternateGroup
    )
}

fun SkincareLogEntity.toDomainModel(): SkincareLog {
    return SkincareLog(
        id = this.id,
        userId = this.userId,
        date = this.date,
        routineType = this.routineType,
        stepId = this.routineStepId,
        completed = this.completed
    )
}

fun SkincareLog.toEntity(): SkincareLogEntity {
    return SkincareLogEntity(
        id = this.id,
        userId = this.userId,
        date = this.date,
        routineType = this.routineType,
        routineStepId = this.stepId,
        completed = this.completed
    )
}

fun SkinPhotoEntity.toDomainModel(): SkinPhoto {
    return SkinPhoto(
        id = this.id,
        userId = this.userId,
        date = this.date,
        photoPath = this.photoPath,
        angleType = this.angleType,
        notes = this.notes
    )
}

fun SkinPhoto.toEntity(): SkinPhotoEntity {
    return SkinPhotoEntity(
        id = this.id,
        userId = this.userId,
        date = this.date,
        photoPath = this.photoPath,
        angleType = this.angleType,
        notes = this.notes
    )
}
