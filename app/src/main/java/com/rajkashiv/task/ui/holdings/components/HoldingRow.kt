package com.rajkashiv.task.ui.holdings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rajkashiv.task.ui.util.inr
import com.rajkashiv.task.domain.model.Holding

@Composable
fun HoldingRow(h: Holding, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(h.symbol, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium))
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("LTP: ${inr(h.ltp)}")
            Text("NET QTY: ${h.quantity}")
            val pnl = h.ltp * h.quantity - h.avgPrice * h.quantity
            val color = when {
                pnl > 0 -> MaterialTheme.colorScheme.tertiary
                pnl < 0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
            Text("P&L: ${inr(pnl)}", color = color)
        }
    }
}
