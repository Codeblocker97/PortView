package com.rajkashiv.task.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rajkashiv.task.data.local.entity.HoldingEntity

@Dao
interface HoldingsDao {
    @Query("SELECT * FROM holdings")
    suspend fun getAll(): List<HoldingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<HoldingEntity>)

    @Query("DELETE FROM holdings")
    suspend fun clear()
}
