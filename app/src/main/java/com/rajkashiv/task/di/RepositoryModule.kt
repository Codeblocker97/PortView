package com.rajkashiv.task.di

import com.rajkashiv.task.data.local.HoldingsDao
import com.rajkashiv.task.data.remote.HoldingsApi
import com.rajkashiv.task.data.repository.HoldingsRepositoryImpl
import com.rajkashiv.task.domain.repository.HoldingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideHoldingsRepository(api: HoldingsApi, dao: HoldingsDao): HoldingsRepository =
        HoldingsRepositoryImpl(api, dao)
}
