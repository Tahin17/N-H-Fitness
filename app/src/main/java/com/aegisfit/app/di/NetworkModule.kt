package com.aegisfit.app.di

import com.aegisfit.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "NHTFitness/${BuildConfig.VERSION_NAME} (Android; com.aegisfit.app)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("openFoodFacts")
    fun provideOpenFoodFactsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("usda")
    fun provideUsdaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(@Named("openFoodFacts") retrofit: Retrofit): com.aegisfit.app.data.remote.api.OpenFoodFactsApi {
        return retrofit.create(com.aegisfit.app.data.remote.api.OpenFoodFactsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUsdaApi(@Named("usda") retrofit: Retrofit): com.aegisfit.app.data.remote.api.UsdaApi {
        return retrofit.create(com.aegisfit.app.data.remote.api.UsdaApi::class.java)
    }
}
