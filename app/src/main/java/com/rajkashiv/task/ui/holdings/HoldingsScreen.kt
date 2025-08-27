package com.rajkashiv.task.ui.holdings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HoldingsRoute(vm: HoldingsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    HoldingsScreen(
        state = state,
        onRetry = { vm.loadHoldings(force = true) },
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
        topBar = {
            TopAppBar(
                title = { Text("Portfolio") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PullToRefreshBox( // Assuming you want to keep pull-to-refresh
                isRefreshing = state.isLoading && state.holdings.isNotEmpty(), // Show refresh only if data is already there
                onRefresh = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isLoading && state.holdings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
                    }
                } else if (state.error != null && state.holdings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Failed to load holdings. ${state.error}\nPull down to retry.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp) // Removed horizontal here, handled in HoldingRow
                    ) {
                        items(
                            items = state.holdings,
                            // Key needs to be unique. If symbols can repeat, you need a better unique ID.
                            // For this example, assuming symbol + quantity or a dedicated ID from API.
                            // If IDEA can appear twice with different quantities, symbol alone isn't unique.
                            key = { holding -> holding.symbol + holding.quantity } // Example of a more unique key
                        ) { holding ->
                            HoldingRow(holding = holding)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }

            PortfolioSummaryCard(
                summary = state.summary,
                expanded = state.expanded,
                onToggle = onToggleExpand,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Optional: Display error as a chip if not handled within the list
            if (state.error != null && state.holdings.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = onRetry,
                    label = { Text("Retry: ${state.error}") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HoldingRow(holding: HoldingUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = holding.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NET QTY: ${holding.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
                // Optional: Display "T1 Holding" tag or similar
                holding.tag?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    ChipLikeTag(text = it)
                }
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "LTP: ₹${String.format("%.2f", holding.ltp)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "P&L: ₹${String.format("%.2f", holding.pnl)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (holding.pnl >= 0) Color(0xFF008000) else Color.Red
            )
        }
    }
}

@Composable
fun ChipLikeTag(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(start = 4.dp) // Adjusted padding
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummaryUiModel?,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    summary?.let { s -> // Renamed for clarity within the scope
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .animateContentSize(), // Smoothly animates size changes
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Collapsed View / Header part (Always Visible)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profit & Loss*",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${String.format("%.2f", s.totalPnl)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (s.totalPnl >= 0) Color(0xFF008000) else Color.Red,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "(${String.format("%.2f", s.totalPnlPercentage)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (s.totalPnl >= 0) Color(0xFF008000) else Color.Red,
                             modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }

                // Expanded Details (Conditionally Visible)
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        SummaryDetailRow(label = "Current value*", value = s.currentValue)
                        Spacer(modifier = Modifier.height(8.dp))
                        SummaryDetailRow(label = "Total investment*", value = s.totalInvestment)
                        Spacer(modifier = Modifier.height(8.dp))
                        SummaryDetailRow(label = "Today's Profit & Loss*", value = s.todaysPnl, isPnl = true)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryDetailRow(label: String, value: Double, isPnl: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "₹${String.format("%.2f", value)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isPnl) (if (value >= 0) Color(0xFF008000) else Color.Red) else LocalContentColor.current
        )
    }
}


// --- Preview ---
@Preview(showBackground = true, name = "Holdings Screen Collapsed")
@Composable
fun HoldingsScreenCollapsedPreview() {
    val sampleHoldings = listOf(
        HoldingUiModel("ASHOKLEY", 3, 119.10, 12.90, null),
        HoldingUiModel("HDFC", 7, 2497.20, -1517.46, null),
        HoldingUiModel("ICICIBANK", 1, 624.70, 135.60, null),
        HoldingUiModel("IDEA", 3, 9.95, 2.79, "T1 Holding"),
        HoldingUiModel("AIRTEL", 71, 9.95, 66.03, null),
        HoldingUiModel("INDHOTEL", 10, 142.75, -697.06, null)
    )
    val sampleSummary = PortfolioSummaryUiModel(27893.65, 28590.71, -235.65, -697.06, -2.44)

    HoldingsScreen(
        state = HoldingsUiState(
            holdings = sampleHoldings,
            summary = sampleSummary,
            isLoading = false,
            error = null,
            expanded = false // Collapsed state for preview
        ),
        onRetry = {},
        onToggleExpand = {}
    )
}

@Preview(showBackground = true, name = "Holdings Screen Expanded")
@Composable
fun HoldingsScreenExpandedPreview() {
    val sampleHoldings = listOf(
        HoldingUiModel("ASHOKLEY", 3, 119.10, 12.90, null),
        HoldingUiModel("HDFC", 7, 2497.20, -1517.46, null),
        HoldingUiModel("IDEA", 3, 9.95, 2.79, "T1 Holding"),
    )
    val sampleSummary = PortfolioSummaryUiModel(27893.65, 28590.71, -235.65, -697.06, -2.44)

    HoldingsScreen(
        state = HoldingsUiState(
            holdings = sampleHoldings,
            summary = sampleSummary,
            isLoading = false,
            error = null,
            expanded = true // Expanded state for preview
        ),
        onRetry = {},
        onToggleExpand = {}
    )
}
