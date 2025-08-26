package com.rajkashiv.task.domain.model

data class Holding(
    val symbol: String,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double,
    val quantity: Int
)
