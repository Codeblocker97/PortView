package com.rajkashiv.task.data.local.mapper

import com.rajkashiv.task.data.local.entity.HoldingEntity
import com.rajkashiv.task.domain.model.Holding

fun HoldingEntity.toDomain() = Holding(symbol, quantity, ltp, avgPrice, close)
fun Holding.toEntity() = HoldingEntity(symbol, ltp, avgPrice, close, quantity)
