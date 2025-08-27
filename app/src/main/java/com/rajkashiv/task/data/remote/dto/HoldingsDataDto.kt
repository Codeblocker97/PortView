package com.rajkashiv.task.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HoldingsDataDto(
    @SerialName("userHolding") val userHolding: List<UserHoldingDto>
)
