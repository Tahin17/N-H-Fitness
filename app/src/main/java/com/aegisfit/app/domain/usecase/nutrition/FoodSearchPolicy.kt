package com.aegisfit.app.domain.usecase.nutrition

import com.aegisfit.app.domain.model.FoodItem
import java.util.Locale

object FoodSearchPolicy {
    private val MULTIPLE_WHITESPACE = Regex("\\s+")
    private const val POSITIVE_CACHE_TTL_MS = 7L * 24 * 60 * 60 * 1_000
    private const val EMPTY_CACHE_TTL_MS = 24L * 60 * 60 * 1_000
    const val MIN_REMOTE_QUERY_LENGTH = 3

    fun normalize(query: String): String = query
        .trim()
        .lowercase(Locale.ROOT)
        .replace(MULTIPLE_WHITESPACE, " ")

    fun isFresh(
        fetchedAtEpochMs: Long,
        resultCount: Int,
        nowEpochMs: Long
    ): Boolean {
        val ttl = if (resultCount > 0) POSITIVE_CACHE_TTL_MS else EMPTY_CACHE_TTL_MS
        return fetchedAtEpochMs > 0 && nowEpochMs >= fetchedAtEpochMs &&
            nowEpochMs - fetchedAtEpochMs < ttl
    }

    fun rank(items: List<FoodItem>, query: String, limit: Int = 60): List<FoodItem> {
        val normalized = normalize(query)
        if (normalized.isBlank()) return items.take(limit)
        val tokens = normalized.split(' ').filter(String::isNotBlank)

        return items.asSequence()
            .mapNotNull { item ->
                val name = normalize(item.name)
                val brand = normalize(item.brand.orEmpty())
                val category = normalize(item.category.orEmpty())
                val searchable = "$name $brand $category"
                if (!tokens.all(searchable::contains)) return@mapNotNull null

                var score = 0
                if (name == normalized) score += 1_000
                if (name.startsWith(normalized)) score += 600
                score += tokens.count { token -> name.split(' ').any { it.startsWith(token) } } * 120
                if (brand.contains(normalized)) score += 70
                if (category.contains(normalized)) score += 50
                if (item.isLocalBd) score += 35
                if (item.source == "local") score += 25
                if (item.category in setOf("Fitness", "Protein", "Fruit")) score += 10
                item to score
            }
            .sortedWith(compareByDescending<Pair<FoodItem, Int>> { it.second }.thenBy { it.first.name })
            .map { it.first }
            .take(limit)
            .toList()
    }
}
