package com.rajkashiv.task.data.repository

import com.rajkashiv.task.data.local.HoldingsDao
import com.rajkashiv.task.data.local.mapper.toEntity
import com.rajkashiv.task.data.remote.HoldingsApi
import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import com.rajkashiv.task.data.local.mapper.toDomain as entityToDomain
import com.rajkashiv.task.data.remote.mapper.toDomain as dtoToDomain

class HoldingsRepositoryImpl(
    private val api: HoldingsApi,
    private val dao: HoldingsDao
) : HoldingsRepository {

    override fun getHoldings(forceRefresh: Boolean): Flow<Result<List<Holding>>> = flow {
        val cache = dao.getAll().map { it.entityToDomain() }
        if (!forceRefresh && cache.isNotEmpty()) {
            emit(Result.success(cache))
        }
        try {
            // Fetch from remote
            val response = api.getHoldings()
            val remoteHoldings = response.data.userHolding.map { it.dtoToDomain() }

            // Using caching:
             dao.clear()
             dao.upsertAll(remoteHoldings.map { it.toEntity() })

            emit(Result.success(remoteHoldings))
        } catch (e: IOException) { // Specific exception for network issues
            // If using caching and network fails, emit cache if available
             if (cache.isNotEmpty()) emit(Result.success(cache))
             else emit(Result.failure(e))
            emit(Result.failure(e))
        } catch (e: Exception) {
             if (cache.isNotEmpty()) emit(Result.success(cache))
             else emit(Result.failure(e))
            emit(Result.failure(e)) // Simplification
        }
    }
}
