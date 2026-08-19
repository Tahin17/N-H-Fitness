package com.aegisfit.app.data.remote.api

import com.aegisfit.app.data.remote.dto.UsdaSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UsdaApi {
    @GET("foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 20
    ): UsdaSearchResponseDto
}
