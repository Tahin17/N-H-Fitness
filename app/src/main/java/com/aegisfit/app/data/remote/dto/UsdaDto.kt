package com.aegisfit.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsdaSearchResponseDto(
    @SerializedName("foods") val foods: List<UsdaFoodDto>
)

data class UsdaFoodDto(
    @SerializedName("fdcId") val fdcId: Int,
    @SerializedName("description") val description: String,
    @SerializedName("brandOwner") val brandOwner: String?,
    @SerializedName("foodNutrients") val foodNutrients: List<UsdaNutrientDto>,
    @SerializedName("gtinUpc") val gtinUpc: String?
)

data class UsdaNutrientDto(
    @SerializedName("nutrientName") val nutrientName: String,
    @SerializedName("value") val value: Double,
    @SerializedName("unitName") val unitName: String
)
