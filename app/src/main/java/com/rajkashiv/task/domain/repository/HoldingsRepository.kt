package com.rajkashiv.task.domain.repository

import com.rajkashiv.task.domain.model.Holding
import kotlinx.coroutines.flow.Flow

interface HoldingsRepository {
    fun getHoldings(forceRefresh: Boolean): Flow<Result<List<Holding>>>
}
