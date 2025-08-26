package com.rajkashiv.task.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rajkashiv.task.data.local.entity.HoldingEntity

@Database(entities = [HoldingEntity::class], version = 1)
abstract class HoldingsDatabase : RoomDatabase() {
    abstract fun holdingsDao(): HoldingsDao
}
