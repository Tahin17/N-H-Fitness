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
        FoodSearchCacheEntity::class,
        DailyCareItemEntity::class,
        DailyCareLogEntity::class
    ],
    version = 8,
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
    abstract fun dailyCareDao(): DailyCareDao

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

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_care_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        time_slot TEXT NOT NULL,
                        category TEXT NOT NULL,
                        notes TEXT,
                        time_hint TEXT,
                        created_at_ms INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_care_items_user_id ON daily_care_items(user_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_care_items_time_slot ON daily_care_items(time_slot)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_care_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        care_item_id INTEGER NOT NULL,
                        completed INTEGER NOT NULL,
                        completed_at_ms INTEGER,
                        FOREIGN KEY (care_item_id) REFERENCES daily_care_items (id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_care_logs_care_item_id ON daily_care_logs(care_item_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_care_logs_user_id_date ON daily_care_logs(user_id, date)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_daily_care_logs_user_id_date_care_item_id ON daily_care_logs(user_id, date, care_item_id)")
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
                // Note: Care items start clean and empty as requested
            }
        }
    }
}
