package com.rajkashiv.task.domain.usecase

import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.domain.model.PortfolioSummary

class ComputePortfolioSummaryUseCase {
    operator fun invoke(list: List<Holding>): PortfolioSummary {
        val current = list.sumOf { it.ltp * it.quantity }
        val invested = list.sumOf { it.avgPrice * it.quantity }
        val total = current - invested
        val today = list.sumOf { (it.close - it.ltp) * it.quantity }
        val percent = if (invested > 0) total / invested else 0.0
        return PortfolioSummary(current, invested, total, today, percent)
    }
}
