package com.rajkashiv.task.data.repository

import com.rajkashiv.task.data.local.HoldingsDao
import com.rajkashiv.task.data.local.mapper.toDomain as entityToDomain
import com.rajkashiv.task.data.local.mapper.toEntity
import com.rajkashiv.task.data.remote.HoldingsApi
import com.rajkashiv.task.data.remote.mapper.toDomain as dtoToDomain
import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
            val remote = api.getHoldings().data.map { it.dtoToDomain() }
            dao.clear()
            dao.upsertAll(remote.map { it.toEntity() })
            emit(Result.success(remote))
        } catch (e: Exception) {
            if (cache.isNotEmpty()) emit(Result.success(cache)) else emit(Result.failure(e))
        }
    }
}
