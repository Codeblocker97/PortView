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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HoldingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val expanded: Boolean = false,
    val holdings: List<Holding> = emptyList(),
    val summary: PortfolioSummary? = null
)

@HiltViewModel
class HoldingsViewModel @Inject constructor(
    private val getHoldings: GetHoldingsUseCase,
    private val compute: ComputePortfolioSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HoldingsUiState())
    val state: StateFlow<HoldingsUiState> = _state

    init { load(force = true) }

    fun load(force: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getHoldings(force).collect { res ->
                res.onSuccess { list ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            holdings = list,
                            summary = compute(list),
                            error = null
                        )
                    }
                }.onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun toggleExpand() = _state.update { it.copy(expanded = !it.expanded) }
}
