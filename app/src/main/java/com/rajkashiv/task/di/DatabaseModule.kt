package com.rajkashiv.task.di

import android.content.Context
import androidx.room.Room
import com.rajkashiv.task.data.local.HoldingsDao
import com.rajkashiv.task.data.local.HoldingsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): HoldingsDatabase =
        Room.databaseBuilder(ctx, HoldingsDatabase::class.java, "holdings.db").build()

    @Provides
    fun provideDao(db: HoldingsDatabase): HoldingsDao = db.holdingsDao()
}
