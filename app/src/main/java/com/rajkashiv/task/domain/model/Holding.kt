package com.rajkashiv.task.domain.model

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double, // Added
    val close: Double
)
