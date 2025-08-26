package com.rajkashiv.task.ui.holdings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rajkashiv.task.ui.util.inr
import com.rajkashiv.task.domain.model.PortfolioSummary
import kotlin.math.abs


@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummary?,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier.clickable { onToggle() }) {
        Column(Modifier.padding(16.dp)) {
            val totalPnL = summary?.totalPnL ?: 0.0
            val percent = summary?.pnlPercent ?: 0.0
            val sign = if (totalPnL > 0) "+" else if (totalPnL < 0) "-" else ""
            val color = when {
                totalPnL > 0 -> MaterialTheme.colorScheme.tertiary
                totalPnL < 0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }

            Text(
                text = "P&L: $sign${inr(abs(totalPnL))} (${String.format("%.2f", percent * 100)}%)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )

            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text("Current Value: ${inr(summary?.currentValue ?: 0.0)}")
                    Text("Total Investment: ${inr(summary?.totalInvestment ?: 0.0)}")
                    Text("Today’s P&L: ${inr(summary?.todaysPnL ?: 0.0)}")
                }
            }
        }
    }
}
