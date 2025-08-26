package com.rajkashiv.task.data.remote.mapper

import com.rajkashiv.task.data.remote.dto.HoldingDto
import com.rajkashiv.task.domain.model.Holding

fun HoldingDto.toDomain() = Holding(
    symbol = symbol,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close,
    quantity = quantity
)
