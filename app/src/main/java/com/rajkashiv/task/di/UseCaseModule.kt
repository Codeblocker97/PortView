package com.rajkashiv.task.di

import com.rajkashiv.task.domain.repository.HoldingsRepository
import com.rajkashiv.task.domain.usecase.ComputePortfolioSummaryUseCase
import com.rajkashiv.task.domain.usecase.GetHoldingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideGetHoldings(repo: HoldingsRepository) = GetHoldingsUseCase(repo)
    @Provides fun provideComputeSummary() = ComputePortfolioSummaryUseCase()
}
