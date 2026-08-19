package com.aegisfit.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponseDto(
    @SerializedName("products") val products: List<OffProductDto>
)

data class OffProductDto(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("nutriments") val nutriments: OffNutrimentsDto?,
    @SerializedName("code") val code: String?,
    @SerializedName("categories") val categories: String?,
    @SerializedName("image_url") val imageUrl: String?
)

data class OffNutrimentsDto(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Double?,
    @SerializedName("proteins_100g") val proteins100g: Double?,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Double?,
    @SerializedName("fat_100g") val fat100g: Double?,
    @SerializedName("fiber_100g") val fiber100g: Double?
)
