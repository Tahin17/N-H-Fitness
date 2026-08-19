package com.aegisfit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aegisfit.app.data.local.converter.Converters
import com.aegisfit.app.data.local.dao.*
import com.aegisfit.app.data.local.entity.*
import com.aegisfit.app.data.seed.SeedData

@Database(
    entities = [
        UserProfileEntity::class,
        BodyMeasurementEntity::class,
        WorkoutDayEntity::class,
        ExerciseEntity::class,
        WorkoutLogEntity::class,
        CardioLogEntity::class,
        FoodItemEntity::class,
        FoodLogEntity::class,
        HydrationLogEntity::class,
        CreatineLogEntity::class,
        SkincareRoutineEntity::class,
        SkincareLogEntity::class,
        SkinPhotoEntity::class,
        MicroActivityLogEntity::class,
        WeightLogEntity::class,
        FoodSearchCacheEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AegisFitDatabase : RoomDatabase() {
    
    abstract fun userProfileDao(): UserProfileDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun cardioLogDao(): CardioLogDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun foodSearchCacheDao(): FoodSearchCacheDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun creatineDao(): CreatineDao
    abstract fun skincareDao(): SkincareDao
    abstract fun skinPhotoDao(): SkinPhotoDao
    abstract fun microActivityDao(): MicroActivityDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE food_items ADD COLUMN external_id TEXT")
                db.execSQL("ALTER TABLE food_items ADD COLUMN source TEXT NOT NULL DEFAULT 'local'")
                db.execSQL("ALTER TABLE food_items ADD COLUMN last_updated_epoch_ms INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_food_items_external_id ON food_items(external_id)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS food_search_cache (
                        query TEXT NOT NULL PRIMARY KEY,
                        fetched_at_epoch_ms INTEGER NOT NULL,
                        result_count INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val prepopulateCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                
                // Always seed foods on open to ensure new foods are added without conflicts
                db.beginTransaction()
                try {
                    SeedData.seedFoodItems(db)
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }

                val cursor = db.query("SELECT count(*) FROM workout_days")
                var count = 0
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0)
                }
                cursor.close()

                if (count == 0) {
                    db.beginTransaction()
                    try {
                        seedAllData(db)
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
            }

            private fun seedAllData(db: SupportSQLiteDatabase) {
                SeedData.seedWorkoutDays(db)
                // Food items are seeded unconditionally above
                SeedData.seedSkincareRoutines(db)
            }
        }
    }
}
