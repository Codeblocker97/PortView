package com.rajkashiv.task.ui.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkashiv.task.domain.model.PortfolioSummary
import com.rajkashiv.task.domain.model.Holding
import com.rajkashiv.task.domain.usecase.ComputePortfolioSummaryUseCase
import com.rajkashiv.task.domain.usecase.GetHoldingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.div
import kotlin.times

data class HoldingUiModel(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val pnl: Double,
    val tag: String? = null // For things like "T1 Holding"
)

data class PortfolioSummaryUiModel(
    val currentValue: Double,         // New: As per the image
    val totalInvestment: Double,    // New: As per the image
    val todaysPnl: Double,          // New: As per the image
    val totalPnl: Double,             // Previously: totalPnl
    val totalPnlPercentage: Double
)

data class HoldingsUiState(
    val holdings: List<HoldingUiModel> = emptyList(),
    val summary: PortfolioSummaryUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val expanded: Boolean = false // For the summary card
)

@HiltViewModel
class HoldingsViewModel @Inject constructor(
//    private val getHoldings: GetHoldingsUseCase,
//    private val compute: ComputePortfolioSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HoldingsUiState())
    val state: StateFlow<HoldingsUiState> = _state.asStateFlow()

    init {
        load(force = false) // Load data on init
    }

    fun load(force: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
//            getHoldings(force).collect { res ->
//                res.onSuccess { list ->
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            holdings = list,
//                            summary = compute(list),
//                            error = null
//                        )
//                    }
//                }.onFailure { e ->
//                    _state.update { it.copy(isLoading = false, error = e.message) }
//                }
//            }
            try {
                // --- Simulate data fetching ---
                // Replace with actual data fetching logic from your repository
                kotlinx.coroutines.delay(1000) // Simulate network delay

                val fetchedHoldings = listOf(
                    HoldingUiModel("ASHOKLEY", 3, 119.10, 12.90, null),
                    HoldingUiModel("HDFC", 7, 2497.20, -1517.46, null),
                    HoldingUiModel("ICICIBANK", 1, 624.70, 135.60, null),
                    HoldingUiModel("IDEA", 3, 9.95, 2.79, "T1 Holding"),
                    HoldingUiModel("AIRTEL", 71, 9.95, 66.03, null),
                    HoldingUiModel("INDHOTEL", 10, 142.75, -697.06, null)
                )
                val calculatedCurrentValue = fetchedHoldings.sumOf { it.ltp * it.quantity }
                val calculatedTotalPnl = fetchedHoldings.sumOf { it.pnl }
                val calculatedTotalInvestment = calculatedCurrentValue - calculatedTotalPnl // Investment = Current Value - PNL
                val calculatedTodaysPnl = -235.65 // Mock value for Today's P&L as per image

                val calculatedTotalPnlPercentage = if (calculatedTotalInvestment != 0.0) {
                    (calculatedTotalPnl / calculatedTotalInvestment) * 100
                } else {
                    0.0
                }

                val summaryData = PortfolioSummaryUiModel(
                    currentValue = calculatedCurrentValue,
                    totalInvestment = calculatedTotalInvestment,
                    todaysPnl = calculatedTodaysPnl,
                    totalPnl = calculatedTotalPnl,
                    totalPnlPercentage = calculatedTotalPnlPercentage
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        holdings = fetchedHoldings,
                        summary = summaryData,
                        error = null
//                        summary = PortfolioSummaryUiModel(calculatedTotalPnl, calculatedPercentage)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.localizedMessage ?: "An unknown error occurred")
                }
            }
        }
    }

    fun toggleExpand() {
        _state.update { it.copy(expanded = !it.expanded) }
    }
}
