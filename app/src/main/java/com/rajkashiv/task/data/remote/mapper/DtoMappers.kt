package com.rajkashiv.task.data.remote.mapper

import com.rajkashiv.task.data.remote.dto.UserHoldingDto
import com.rajkashiv.task.domain.model.Holding

fun UserHoldingDto.toDomain(): Holding = Holding(
    symbol = symbol,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close,
    quantity = quantity
)
