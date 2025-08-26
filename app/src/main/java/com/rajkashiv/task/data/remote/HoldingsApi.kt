package com.rajkashiv.task.data.remote

import com.rajkashiv.task.data.remote.dto.HoldingsResponseDto
import retrofit2.http.GET

interface HoldingsApi {
    @GET(".")
    suspend fun getHoldings(): HoldingsResponseDto
}
