package com.rajkashiv.task.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey val symbol: String,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double,
    val quantity: Int
)
