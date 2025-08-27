package com.rajkashiv.task.ui.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkashiv.task.domain.usecase.ComputePortfolioSummaryUseCase
import com.rajkashiv.task.domain.usecase.GetHoldingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HoldingUiModel(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val pnl: Double,
    val tag: String? = null
)

data class PortfolioSummaryUiModel(
    val currentValue: Double,
    val totalInvestment: Double,
    val todaysPnl: Double,
    val totalPnl: Double,
    val totalPnlPercentage: Double
)

data class HoldingsUiState(
    val holdings: List<HoldingUiModel> = emptyList(),
    val summary: PortfolioSummaryUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val expanded: Boolean = false
)

@HiltViewModel
class HoldingsViewModel @Inject constructor(
    private val getHoldingsUseCase: GetHoldingsUseCase,
    private val computePortfolioSummaryUseCase: ComputePortfolioSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HoldingsUiState())
    val state: StateFlow<HoldingsUiState> = _state.asStateFlow()

    init {
        loadHoldings(force = false)
    }

    fun loadHoldings(force: Boolean = false) {
        viewModelScope.launch {
            getHoldingsUseCase(forceRefresh = force)
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "An error occurred"
                        )
                    }
                }
                .collect { result ->
                    result.onSuccess { domainHoldings ->
                        val holdingsUiModels = domainHoldings.map { domainHolding ->
                            // Calculate P&L for individual HoldingUiModel
                            val currentValueForHolding = domainHolding.ltp * domainHolding.quantity
                            val investmentValueForHolding = domainHolding.avgPrice * domainHolding.quantity
                            val pnlForHolding = currentValueForHolding - investmentValueForHolding
                            HoldingUiModel(
                                symbol = domainHolding.symbol,
                                quantity = domainHolding.quantity,
                                ltp = domainHolding.ltp,
                                pnl = pnlForHolding,
                                tag = null
                            )
                        }
                        val summary = computePortfolioSummaryUseCase(domainHoldings)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                holdings = holdingsUiModels,
                                summary = summary,
                                error = null
                            )
                        }
                    }.onFailure { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = e.localizedMessage ?: "Failed to load holdings"
                            )
                        }
                    }
                }
        }
    }

    fun toggleExpand() {
        _state.update { it.copy(expanded = !it.expanded) }
    }
}

