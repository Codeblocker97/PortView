package com.rajkashiv.task.domain.usecase

import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.ui.holdings.PortfolioSummaryUiModel

class ComputePortfolioSummaryUseCase {
    operator fun invoke(holdings: List<Holding>): PortfolioSummaryUiModel {
        if (holdings.isEmpty()) {
            return PortfolioSummaryUiModel(0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val overallCurrentValue = holdings.sumOf { it.ltp * it.quantity }
        val overallTotalInvestment = holdings.sumOf { it.avgPrice * it.quantity }
        val overallTotalPnl = overallCurrentValue - overallTotalInvestment

        val overallTodaysPnl = holdings.sumOf { (it.ltp - it.close) * it.quantity }

        val overallTotalPnlPercentage = if (overallTotalInvestment != 0.0) {
            (overallTotalPnl / overallTotalInvestment) * 100
        } else {
            0.0
        }

        return PortfolioSummaryUiModel(
            currentValue = overallCurrentValue,
            totalInvestment = overallTotalInvestment,
            todaysPnl = overallTodaysPnl,
            totalPnl = overallTotalPnl,
            totalPnlPercentage = overallTotalPnlPercentage
        )
    }
}
