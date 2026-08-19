package com.aegisfit.app.data.repository

import com.aegisfit.app.BuildConfig
import com.aegisfit.app.data.local.dao.FoodItemDao
import com.aegisfit.app.data.local.dao.FoodLogDao
import com.aegisfit.app.data.local.dao.FoodSearchCacheDao
import com.aegisfit.app.data.local.entity.FoodSearchCacheEntity
import com.aegisfit.app.data.mapper.toDomain
import com.aegisfit.app.data.mapper.toEntity
import com.aegisfit.app.data.remote.api.OpenFoodFactsApi
import com.aegisfit.app.data.remote.api.UsdaApi
import com.aegisfit.app.domain.model.FoodItem
import com.aegisfit.app.domain.model.FoodLog
import com.aegisfit.app.domain.model.FoodSearchRefreshResult
import com.aegisfit.app.domain.repository.DataSyncRepository
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.usecase.nutrition.FoodSearchPolicy
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NutritionRepositoryImpl @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val foodLogDao: FoodLogDao,
    private val foodSearchCacheDao: FoodSearchCacheDao,
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val usdaApi: UsdaApi,
    private val dataSyncRepository: DataSyncRepository,
    private val userRepository: UserRepository
) : NutritionRepository {

    override fun searchFoodLocally(query: String): Flow<List<FoodItem>> =
        foodItemDao.observeSearchCandidates().map { entities ->
            FoodSearchPolicy.rank(entities.map { it.toDomain() }, query)
        }

    override fun getSuggestedFoods(): Flow<List<FoodItem>> =
        foodItemDao.getSuggestedFoods().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshFoodSearch(
        query: String,
        forceRefresh: Boolean
    ): FoodSearchRefreshResult = coroutineScope {
        val normalizedQuery = FoodSearchPolicy.normalize(query)
        require(normalizedQuery.length >= FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH) {
            "Enter at least ${FoodSearchPolicy.MIN_REMOTE_QUERY_LENGTH} characters to search online."
        }

        val now = System.currentTimeMillis()
        val cached = foodSearchCacheDao.get(normalizedQuery)
        if (!forceRefresh && cached != null && FoodSearchPolicy.isFresh(
                cached.fetchedAtEpochMs,
                cached.resultCount,
                now
            )
        ) {
            return@coroutineScope FoodSearchRefreshResult(
                itemsAddedOrUpdated = 0,
                usedFreshCache = true
            )
        }

        val offResult = async {
            runCatching { openFoodFactsApi.searchFood(normalizedQuery).products.map { it.toDomain() } }
        }
        val usdaResult = if (BuildConfig.USDA_API_KEY.isNotBlank()) {
            async {
                runCatching {
                    usdaApi.searchFood(normalizedQuery, BuildConfig.USDA_API_KEY)
                        .foods
                        .map { it.toDomain() }
                }
            }
        } else {
            null
        }

        val off = offResult.await()
        val usda = usdaResult?.await()
        if (off.isFailure && (usda == null || usda.isFailure)) {
            throw IOException(
                "Food services are unavailable. Your saved foods still work offline.",
                off.exceptionOrNull() ?: usda?.exceptionOrNull()
            )
        }

        val remoteItems = buildList {
            off.getOrNull()?.let(::addAll)
            usda?.getOrNull()?.let(::addAll)
        }.asSequence()
            .filter(::isUsefulRemoteFood)
            .distinctBy { it.externalId ?: it.barcode }
            .take(40)
            .map { item -> item.copy(lastUpdatedEpochMs = now) }
            .toList()

        val changed = foodItemDao.upsertRemote(remoteItems.map { it.toEntity() })
        foodSearchCacheDao.upsert(
            FoodSearchCacheEntity(
                query = normalizedQuery,
                fetchedAtEpochMs = now,
                resultCount = remoteItems.size
            )
        )
        foodSearchCacheDao.deleteOlderThan(now - CACHE_RETENTION_MS)
        FoodSearchRefreshResult(changed, usedFreshCache = false)
    }

    override fun getFoodLogsByDate(userId: String, date: Long): Flow<List<FoodLog>> =
        foodLogDao.getForDate(userId, date).map { logs ->
            logs.mapNotNull { log ->
                foodItemDao.getById(log.foodItemId)?.toDomain()?.let { food -> log.toDomain(food) }
            }
        }

    override suspend fun saveFoodLog(log: FoodLog): Long {
        require(log.userId.isNotBlank()) { "Sign in before logging food." }
        require(log.servings.isFinite() && log.servings in 0.1..20.0) {
            "Servings must be between 0.1 and 20."
        }

        val entity = log.toEntity()
        val savedId = foodLogDao.insert(entity)
        val food = log.foodItem
        runCatching {
            dataSyncRepository.uploadLog(
                userId = log.userId,
                logType = "food",
                logData = mapOf(
                "localId" to savedId,
                "userId" to log.userId,
                "date" to entity.date,
                "foodItemId" to entity.foodItemId,
                "foodName" to food.name,
                "brand" to food.brand.orEmpty(),
                "caloriesPer100g" to food.caloriesPer100g,
                "proteinPer100g" to food.proteinPer100g,
                "carbsPer100g" to food.carbsPer100g,
                "fatPer100g" to food.fatPer100g,
                "defaultServingSizeG" to food.defaultServingSizeG,
                "servingDescription" to food.servingDescription.orEmpty(),
                "barcode" to food.barcode.orEmpty(),
                "externalId" to food.externalId.orEmpty(),
                "source" to food.source,
                "servingSizeG" to entity.servingSizeG,
                "mealType" to entity.mealType,
                "calories" to entity.calories,
                "protein" to entity.protein,
                "carbs" to entity.carbs,
                "fat" to entity.fat,
                "timestamp" to entity.timestamp
                ),
                date = entity.date,
                id = savedId.toString()
            ).getOrThrow()
        }
        runCatching { userRepository.addXp(log.userId, FOOD_LOG_POINTS) }
        return savedId
    }

    override suspend fun saveFoodItem(item: FoodItem): Long =
        foodItemDao.upsertOne(item.toEntity())

    override fun getTotalCaloriesForDate(userId: String, date: Long): Flow<Double?> =
        foodLogDao.getTotalCaloriesForDate(userId, date)

    override fun getTotalProteinForDate(userId: String, date: Long): Flow<Double?> =
        foodLogDao.getTotalProteinForDate(userId, date)

    override fun getTotalCarbsForDate(userId: String, date: Long): Flow<Double?> =
        foodLogDao.getTotalCarbsForDate(userId, date)

    override fun getTotalFatForDate(userId: String, date: Long): Flow<Double?> =
        foodLogDao.getTotalFatForDate(userId, date)

    override fun getTotalCaloriesInRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> = foodLogDao.getTotalCaloriesInRange(userId, startDate, endDate)

    private fun isUsefulRemoteFood(item: FoodItem): Boolean {
        val nutrients = listOf(
            item.caloriesPer100g,
            item.proteinPer100g,
            item.carbsPer100g,
            item.fatPer100g
        )
        return item.name.isNotBlank() &&
            item.name != "Unknown" &&
            nutrients.all { it.isFinite() && it >= 0.0 } &&
            nutrients.any { it > 0.0 } &&
            (item.externalId != null || !item.barcode.isNullOrBlank())
    }

    private companion object {
        const val FOOD_LOG_POINTS = 5L
        const val CACHE_RETENTION_MS = 30L * 24 * 60 * 60 * 1_000
    }
}
