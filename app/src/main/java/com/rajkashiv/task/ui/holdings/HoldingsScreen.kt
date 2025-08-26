package com.rajkashiv.task.ui.holdings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rajkashiv.task.ui.holdings.components.HoldingRow
import com.rajkashiv.task.ui.holdings.components.PortfolioSummaryCard

@Composable
fun HoldingsRoute(vm: HoldingsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    HoldingsScreen(
        state = state,
        onRetry = { vm.load(force = true) },
        onToggleExpand = vm::toggleExpand
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoldingsScreen(
    state: HoldingsUiState,
    onRetry: () -> Unit,
    onToggleExpand: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Portfolio") }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            PortfolioSummaryCard(
                summary = state.summary,
                expanded = state.expanded,
                onToggle = onToggleExpand,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (state.isLoading && state.holdings.isEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn { // The content lambda here has LazyListScope as its receiver
                items( // Call items directly
                    items = state.holdings,
                    key = { holding -> holding.symbol } // It's good practice to explicitly name the lambda parameter
                ) { holding -> // 'holding' is the iterated item from state.holdings
                    HoldingRow(holding)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = onRetry, label = { Text("Retry: $it") })
            }
        }
    }
}
