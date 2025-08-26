package com.rajkashiv.task.domain.model

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnL: Double,
    val todaysPnL: Double,
    val pnlPercent: Double
)
