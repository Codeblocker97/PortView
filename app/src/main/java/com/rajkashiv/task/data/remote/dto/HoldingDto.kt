package com.rajkashiv.task.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HoldingDto(
    @SerialName("symbol") val symbol: String,
    @SerialName("ltp") val ltp: Double,
    @SerialName("avg_price") val avgPrice: Double,
    @SerialName("close") val close: Double,
    @SerialName("quantity") val quantity: Int
)

@Serializable
data class HoldingsResponseDto(
    @SerialName("data") val data: List<HoldingDto> = emptyList()
)
