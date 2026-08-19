package com.aegisfit.app.di

import com.aegisfit.app.data.repository.AuthRepositoryImpl
import com.aegisfit.app.data.repository.BodyMeasurementRepositoryImpl
import com.aegisfit.app.data.repository.DataSyncRepositoryImpl
import com.aegisfit.app.data.repository.HydrationRepositoryImpl
import com.aegisfit.app.data.repository.NutritionRepositoryImpl
import com.aegisfit.app.data.repository.SkincareRepositoryImpl
import com.aegisfit.app.data.repository.UserRepositoryImpl
import com.aegisfit.app.data.repository.WorkoutRepositoryImpl
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.BodyMeasurementRepository
import com.aegisfit.app.domain.repository.DataSyncRepository
import com.aegisfit.app.domain.repository.HydrationRepository
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.repository.SkincareRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindBodyMeasurementRepository(impl: BodyMeasurementRepositoryImpl): BodyMeasurementRepository

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(impl: NutritionRepositoryImpl): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindHydrationRepository(impl: HydrationRepositoryImpl): HydrationRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindSkincareRepository(impl: SkincareRepositoryImpl): SkincareRepository

    @Binds
    @Singleton
    abstract fun bindDataSyncRepository(impl: DataSyncRepositoryImpl): DataSyncRepository
}
