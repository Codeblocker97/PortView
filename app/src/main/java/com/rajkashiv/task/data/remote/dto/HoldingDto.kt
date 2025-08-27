package com.rajkashiv.task.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserHoldingDto(
    @SerialName("symbol") val symbol: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("ltp") val ltp: Double,
    @SerialName("avgPrice") val avgPrice: Double,
    @SerialName("close") val close: Double
)

@Serializable
data class HoldingsResponseDto(
    @SerialName("data") val data: HoldingsDataDto
)
