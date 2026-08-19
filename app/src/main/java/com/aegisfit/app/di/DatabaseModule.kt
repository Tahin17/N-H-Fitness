package com.aegisfit.app.di

import android.content.Context
import androidx.room.Room
import com.aegisfit.app.data.local.AegisFitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AegisFitDatabase {
        return Room.databaseBuilder(
            context,
            AegisFitDatabase::class.java,
            "aegisfit_database"
        )
        .addCallback(AegisFitDatabase.prepopulateCallback)
        .addMigrations(AegisFitDatabase.MIGRATION_6_7)
        .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5)
        .build()
    }

    @Provides fun provideUserProfileDao(db: AegisFitDatabase) = db.userProfileDao()
    @Provides fun provideBodyMeasurementDao(db: AegisFitDatabase) = db.bodyMeasurementDao()
    @Provides fun provideWorkoutDao(db: AegisFitDatabase) = db.workoutDao()
    @Provides fun provideWorkoutLogDao(db: AegisFitDatabase) = db.workoutLogDao()
    @Provides fun provideCardioLogDao(db: AegisFitDatabase) = db.cardioLogDao()
    @Provides fun provideFoodItemDao(db: AegisFitDatabase) = db.foodItemDao()
    @Provides fun provideFoodLogDao(db: AegisFitDatabase) = db.foodLogDao()
    @Provides fun provideFoodSearchCacheDao(db: AegisFitDatabase) = db.foodSearchCacheDao()
    @Provides fun provideHydrationDao(db: AegisFitDatabase) = db.hydrationDao()
    @Provides fun provideCreatineDao(db: AegisFitDatabase) = db.creatineDao()
    @Provides fun provideSkincareDao(db: AegisFitDatabase) = db.skincareDao()
    @Provides fun provideSkinPhotoDao(db: AegisFitDatabase) = db.skinPhotoDao()
    @Provides fun provideMicroActivityDao(db: AegisFitDatabase) = db.microActivityDao()
    @Provides fun provideWeightLogDao(db: AegisFitDatabase) = db.weightLogDao()
}
