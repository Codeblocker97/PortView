package com.rajkashiv.task.domain.usecase

import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow

class GetHoldingsUseCase(private val repository: HoldingsRepository) {
    operator fun invoke(forceRefresh: Boolean): Flow<Result<List<Holding>>> =
        repository.getHoldings(forceRefresh)
}
