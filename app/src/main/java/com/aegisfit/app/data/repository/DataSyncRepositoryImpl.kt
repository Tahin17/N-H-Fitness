package com.aegisfit.app.data.repository

import android.util.Log
import com.aegisfit.app.data.local.dao.CardioLogDao
import com.aegisfit.app.data.local.dao.FoodItemDao
import com.aegisfit.app.data.local.dao.FoodLogDao
import com.aegisfit.app.data.local.dao.CreatineDao
import com.aegisfit.app.data.local.dao.UserProfileDao
import com.aegisfit.app.data.local.dao.WeightLogDao
import com.aegisfit.app.data.local.dao.WorkoutLogDao
import com.aegisfit.app.data.local.entity.CardioLogEntity
import com.aegisfit.app.data.local.entity.FoodItemEntity
import com.aegisfit.app.data.local.entity.FoodLogEntity
import com.aegisfit.app.data.local.entity.CreatineLogEntity
import com.aegisfit.app.data.local.entity.UserProfileEntity
import com.aegisfit.app.data.local.entity.WeightLogEntity
import com.aegisfit.app.data.local.entity.WorkoutLogEntity
import com.aegisfit.app.domain.repository.DataSyncRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class DataSyncRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileDao: UserProfileDao,
    private val workoutLogDao: WorkoutLogDao,
    private val cardioLogDao: CardioLogDao,
    private val foodItemDao: FoodItemDao,
    private val foodLogDao: FoodLogDao,
    private val creatineDao: CreatineDao,
    private val weightLogDao: WeightLogDao
) : DataSyncRepository {

    override suspend fun pushAllLocalData(userId: String): Result<Unit> = runCatching {
        require(userId.isNotBlank()) { "A signed-in user is required for cloud sync." }
        val thirtyDaysAgo = System.currentTimeMillis() - THIRTY_DAYS_MS

        userProfileDao.getProfileOnce(userId)?.let { profile ->
            firestore.collection(USERS).document(userId)
                .set(profile.toCloudMap(), SetOptions.merge())
                .await()
        }

        workoutLogDao.getLogsSince(userId, thirtyDaysAgo).forEach { log ->
            uploadLog(userId, WORKOUT, log.toCloudMap(), log.date, log.id.toString()).getOrThrow()
        }
        cardioLogDao.getLogsSince(userId, thirtyDaysAgo).forEach { log ->
            uploadLog(userId, CARDIO, log.toCloudMap(), log.date, log.id.toString()).getOrThrow()
        }
        foodLogDao.getLogsSince(userId, thirtyDaysAgo).forEach { log ->
            val food = foodItemDao.getById(log.foodItemId)
            uploadLog(
                userId,
                FOOD,
                log.toCloudMap() + (food?.toCloudSnapshot().orEmpty()),
                log.date,
                log.id.toString()
            ).getOrThrow()
        }
        weightLogDao.getLogsSince(userId, thirtyDaysAgo).forEach { log ->
            uploadLog(userId, WEIGHT, log.toCloudMap(), log.date, log.id.toString()).getOrThrow()
        }
        creatineDao.getLogsSince(userId, thirtyDaysAgo).forEach { log ->
            uploadLog(userId, CREATINE, log.toCloudMap(), log.date, log.id.toString()).getOrThrow()
        }
    }

    override suspend fun pullAllCloudData(userId: String): Result<Unit> = runCatching {
        require(userId.isNotBlank()) { "A signed-in user is required for cloud sync." }
        pullProfile(userId)

        val snapshot = firestore.collection(USERS).document(userId)
            .collection(LOGS)
            .get()
            .await()

        snapshot.documents.forEach { document ->
            val data = document.data
            if (data == null) {
                Log.w(TAG, "Skipped empty cloud log ${document.id}")
                return@forEach
            }

            val restoredData = if ("localId" in data) {
                data
            } else {
                document.id.substringAfterLast('_').toLongOrNull()
                    ?.let { data + ("localId" to it) }
                    ?: data
            }
            runCatching {
                when (document.id.substringBefore('_')) {
                    WORKOUT -> restoreWorkout(userId, restoredData)
                    CARDIO -> restoreCardio(userId, restoredData)
                    FOOD -> restoreFood(userId, document.id, restoredData)
                    WEIGHT -> restoreWeight(userId, restoredData)
                    CREATINE -> restoreCreatine(userId, restoredData)
                    else -> Log.w(TAG, "Skipped unknown cloud log type for ${document.id}")
                }
            }.onFailure { error ->
                // One old or malformed document must not block sign-in or recovery of valid data.
                Log.w(TAG, "Skipped malformed cloud log ${document.id}", error)
            }
        }
    }

    override suspend fun syncDirtyData(userId: String): Result<Unit> = pushAllLocalData(userId)

    override suspend fun uploadLog(
        userId: String,
        logType: String,
        logData: Map<String, Any>,
        date: Long,
        id: String
    ): Result<Unit> = runCatching {
        require(userId.isNotBlank()) { "A signed-in user is required for cloud sync." }
        require(logType in SUPPORTED_LOG_TYPES) { "Unsupported log type." }
        val safeId = id.filter { it.isLetterOrDigit() || it == '-' }.take(80)
        require(safeId.isNotBlank()) { "A valid log ID is required." }
        val documentId = "${logType}_${date}_$safeId"
        firestore.collection(USERS).document(userId)
            .collection(LOGS).document(documentId)
            .set(logData, SetOptions.merge())
            .await()
    }

    private suspend fun pullProfile(userId: String) {
        val document = firestore.collection(USERS).document(userId).get().await()
        val data = document.data ?: return
        val name = data.string("name")?.trim().orEmpty()
        if (name.length !in 2..80) {
            Log.w(TAG, "Skipped incomplete cloud profile for signed-in user")
            return
        }
        val now = System.currentTimeMillis()
        userProfileDao.insertOrUpdate(
            UserProfileEntity(
                userId = userId,
                name = name,
                age = data.int("age")?.coerceIn(13, 100) ?: 25,
                gender = data.string("gender") ?: "Other",
                weightKg = data.double("weightKg")?.coerceIn(30.0, 350.0) ?: 70.0,
                goalWeightKg = data.double("goalWeightKg")?.coerceIn(30.0, 350.0) ?: 70.0,
                heightCm = data.double("heightCm")?.coerceIn(100.0, 250.0) ?: 170.0,
                bodyFatPercent = data.double("bodyFatPercent")?.takeIf { it in 2.0..75.0 },
                activityLevel = data.string("activityLevel") ?: "Moderate",
                dailyCalorieTarget = data.int("dailyCalorieTarget")?.coerceIn(1_000, 6_000) ?: 2_000,
                unitSystem = data.string("unitSystem") ?: "Metric",
                useStealthMode = false,
                xp = data.long("xp")?.coerceAtLeast(0) ?: 0,
                createdAt = data.long("createdAt") ?: now,
                updatedAt = data.long("updatedAt") ?: now
            )
        )
    }

    private suspend fun restoreWorkout(userId: String, data: Map<String, Any>) {
        val exerciseId = data.longRequired("exerciseId")
        val date = data.longRequired("date")
        val setNumber = data.intRequired("setNumber").coerceIn(1, 100)
        val cloudId = data.long("localId")?.takeIf { it > 0 }
        val id = when {
            cloudId != null && workoutLogDao.getById(cloudId)?.userId.let { it == null || it == userId } -> cloudId
            else -> workoutLogDao.findId(userId, exerciseId, date, setNumber) ?: 0
        }
        workoutLogDao.insert(
            WorkoutLogEntity(
                id = id,
                userId = userId,
                exerciseId = exerciseId,
                date = date,
                setNumber = setNumber,
                reps = data.intRequired("reps").coerceIn(0, 1_000),
                weightKg = data.doubleRequired("weightKg").coerceIn(0.0, 1_000.0),
                completed = data.boolean("completed") ?: false,
                notes = data.string("notes")?.take(500)
            )
        )
    }

    private suspend fun restoreCardio(userId: String, data: Map<String, Any>) {
        val date = data.longRequired("date")
        val type = data.stringRequired("type").take(60)
        val duration = data.intRequired("durationMin").coerceIn(1, 1_440)
        val cloudId = data.long("localId")?.takeIf { it > 0 }
        val id = when {
            cloudId != null && cardioLogDao.getById(cloudId)?.userId.let { it == null || it == userId } -> cloudId
            else -> cardioLogDao.findId(userId, date, type, duration) ?: 0
        }
        cardioLogDao.insert(
            CardioLogEntity(
                id = id,
                userId = userId,
                date = date,
                type = type,
                durationMin = duration,
                distanceKm = data.double("distanceKm")?.takeIf { it > 0 }?.coerceAtMost(1_000.0),
                caloriesBurned = data.int("caloriesBurned")?.coerceIn(0, 20_000) ?: 0,
                completed = data.boolean("completed") ?: false
            )
        )
    }

    private suspend fun restoreFood(
        userId: String,
        cloudDocumentId: String,
        data: Map<String, Any>
    ) {
        val oldFoodItemId = data.long("foodItemId")
        val foodItemId = if (!data.string("foodName").isNullOrBlank()) {
            foodItemDao.upsertOne(
                FoodItemEntity(
                    name = data.stringRequired("foodName").take(160),
                    brand = data.string("brand")?.takeIf(String::isNotBlank)?.take(120),
                    caloriesPer100g = data.double("caloriesPer100g")?.coerceIn(0.0, 1_000.0) ?: 0.0,
                    proteinPer100g = data.double("proteinPer100g")?.coerceIn(0.0, 100.0) ?: 0.0,
                    carbsPer100g = data.double("carbsPer100g")?.coerceIn(0.0, 100.0) ?: 0.0,
                    fatPer100g = data.double("fatPer100g")?.coerceIn(0.0, 100.0) ?: 0.0,
                    defaultServingSizeG = data.double("defaultServingSizeG")
                        ?.coerceIn(1.0, 5_000.0) ?: 100.0,
                    servingDescription = data.string("servingDescription")?.take(80),
                    barcode = data.string("barcode")?.takeIf(String::isNotBlank)?.take(80),
                    externalId = data.string("externalId")?.takeIf(String::isNotBlank)?.take(160)
                        ?: "cloud:$cloudDocumentId",
                    source = data.string("source")?.take(40) ?: "cloud",
                    lastUpdatedEpochMs = System.currentTimeMillis()
                )
            )
        } else {
            oldFoodItemId?.let { foodItemDao.getById(it)?.id }
                ?: error("The legacy food item is not available on this device.")
        }

        val date = data.longRequired("date")
        val timestamp = data.long("timestamp") ?: date
        val mealType = data.string("mealType")?.takeIf { it in MEAL_TYPES } ?: "Snack"
        val cloudId = data.long("localId")?.takeIf { it > 0 }
        val logId = when {
            cloudId != null && foodLogDao.getById(cloudId)?.userId.let { it == null || it == userId } -> cloudId
            else -> foodLogDao.findId(userId, date, timestamp, mealType) ?: 0
        }

        foodLogDao.insert(
            FoodLogEntity(
                id = logId,
                userId = userId,
                date = date,
                foodItemId = foodItemId,
                servingSizeG = data.doubleRequired("servingSizeG").coerceIn(1.0, 100_000.0),
                mealType = mealType,
                calories = data.double("calories")?.coerceIn(0.0, 100_000.0) ?: 0.0,
                protein = data.double("protein")?.coerceIn(0.0, 10_000.0) ?: 0.0,
                carbs = data.double("carbs")?.coerceIn(0.0, 10_000.0) ?: 0.0,
                fat = data.double("fat")?.coerceIn(0.0, 10_000.0) ?: 0.0,
                timestamp = timestamp
            )
        )
    }

    private suspend fun restoreWeight(userId: String, data: Map<String, Any>) {
        val date = data.longRequired("date")
        val cloudId = data.long("localId")?.takeIf { it > 0 }
        val id = when {
            cloudId != null && weightLogDao.getById(cloudId)?.userId.let { it == null || it == userId } -> cloudId
            else -> weightLogDao.findId(userId, date) ?: 0
        }
        weightLogDao.insert(
            WeightLogEntity(
                id = id,
                userId = userId,
                date = date,
                weightKg = data.doubleRequired("weightKg").coerceIn(30.0, 350.0),
                timestamp = data.long("timestamp") ?: System.currentTimeMillis()
            )
        )
    }

    private suspend fun restoreCreatine(userId: String, data: Map<String, Any>) {
        val date = data.longRequired("date")
        val cloudId = data.long("localId")?.takeIf { it > 0 }
        val id = when {
            cloudId != null && creatineDao.getById(cloudId)?.userId.let { it == null || it == userId } -> cloudId
            else -> creatineDao.findId(userId, date) ?: 0
        }
        creatineDao.insertOrUpdate(
            CreatineLogEntity(
                id = id,
                userId = userId,
                date = date,
                taken = data.boolean("taken") ?: false,
                waterAmountMl = data.int("waterAmountMl")?.coerceIn(0, 5_000) ?: 0
            )
        )
    }

    private fun UserProfileEntity.toCloudMap(): Map<String, Any> = mapOf(
        "name" to name,
        "age" to age,
        "gender" to gender,
        "weightKg" to weightKg,
        "goalWeightKg" to goalWeightKg,
        "heightCm" to heightCm,
        "bodyFatPercent" to (bodyFatPercent ?: 0.0),
        "activityLevel" to activityLevel,
        "dailyCalorieTarget" to dailyCalorieTarget,
        "unitSystem" to unitSystem,
        "xp" to xp,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    private fun WorkoutLogEntity.toCloudMap(): Map<String, Any> = mapOf(
        "localId" to id,
        "userId" to userId,
        "exerciseId" to exerciseId,
        "date" to date,
        "setNumber" to setNumber,
        "reps" to reps,
        "weightKg" to weightKg,
        "completed" to completed,
        "notes" to notes.orEmpty()
    )

    private fun CardioLogEntity.toCloudMap(): Map<String, Any> = mapOf(
        "localId" to id,
        "userId" to userId,
        "date" to date,
        "type" to type,
        "durationMin" to durationMin,
        "distanceKm" to (distanceKm ?: 0.0),
        "caloriesBurned" to caloriesBurned,
        "completed" to completed
    )

    private fun FoodLogEntity.toCloudMap(): Map<String, Any> = mapOf(
        "localId" to id,
        "userId" to userId,
        "date" to date,
        "foodItemId" to foodItemId,
        "servingSizeG" to servingSizeG,
        "mealType" to mealType,
        "calories" to calories,
        "protein" to protein,
        "carbs" to carbs,
        "fat" to fat,
        "timestamp" to timestamp
    )

    private fun FoodItemEntity.toCloudSnapshot(): Map<String, Any> = mapOf(
        "foodName" to name,
        "brand" to brand.orEmpty(),
        "caloriesPer100g" to caloriesPer100g,
        "proteinPer100g" to proteinPer100g,
        "carbsPer100g" to carbsPer100g,
        "fatPer100g" to fatPer100g,
        "defaultServingSizeG" to defaultServingSizeG,
        "servingDescription" to servingDescription.orEmpty(),
        "barcode" to barcode.orEmpty(),
        "externalId" to externalId.orEmpty(),
        "source" to source
    )

    private fun WeightLogEntity.toCloudMap(): Map<String, Any> = mapOf(
        "localId" to id,
        "userId" to userId,
        "date" to date,
        "weightKg" to weightKg,
        "timestamp" to timestamp
    )

    private fun CreatineLogEntity.toCloudMap(): Map<String, Any> = mapOf(
        "localId" to id,
        "userId" to userId,
        "date" to date,
        "taken" to taken,
        "waterAmountMl" to waterAmountMl
    )

    private fun Map<String, Any>.string(key: String): String? = this[key] as? String
    private fun Map<String, Any>.stringRequired(key: String): String =
        string(key)?.takeIf(String::isNotBlank) ?: error("Missing $key")
    private fun Map<String, Any>.long(key: String): Long? = (this[key] as? Number)?.toLong()
    private fun Map<String, Any>.longRequired(key: String): Long = long(key) ?: error("Missing $key")
    private fun Map<String, Any>.int(key: String): Int? = (this[key] as? Number)?.toInt()
    private fun Map<String, Any>.intRequired(key: String): Int = int(key) ?: error("Missing $key")
    private fun Map<String, Any>.double(key: String): Double? =
        (this[key] as? Number)?.toDouble()?.takeIf(Double::isFinite)
    private fun Map<String, Any>.doubleRequired(key: String): Double =
        double(key) ?: error("Missing $key")
    private fun Map<String, Any>.boolean(key: String): Boolean? = this[key] as? Boolean

    private companion object {
        const val TAG = "NHTCloudSync"
        const val USERS = "users"
        const val LOGS = "logs"
        const val WORKOUT = "workout"
        const val CARDIO = "cardio"
        const val FOOD = "food"
        const val WEIGHT = "weight"
        const val CREATINE = "creatine"
        const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1_000
        val SUPPORTED_LOG_TYPES = setOf(WORKOUT, CARDIO, FOOD, WEIGHT, CREATINE)
        val MEAL_TYPES = setOf("Breakfast", "Lunch", "Dinner", "Snack")
    }
}
