package com.rajkashiv.task.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rajkashiv.task.data.remote.HoldingsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/"

    @Provides @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

        @Provides @Singleton
        fun provideOkHttp(): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build()

        @Provides @Singleton
        fun provideRetrofit(json: Json, okHttp: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .client(okHttp)
                .build()

        @Provides @Singleton
        fun provideApi(retrofit: Retrofit): HoldingsApi = retrofit.create(HoldingsApi::class.java)
    }