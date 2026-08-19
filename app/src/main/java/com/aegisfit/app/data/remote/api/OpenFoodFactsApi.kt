package com.aegisfit.app.data.remote.api

import com.aegisfit.app.data.remote.dto.OpenFoodFactsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("cgi/search.pl")
    suspend fun searchFood(
        @Query("search_terms") query: String,
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = "code,product_name,brands,nutriments,categories,image_url"
    ): OpenFoodFactsResponseDto
}
