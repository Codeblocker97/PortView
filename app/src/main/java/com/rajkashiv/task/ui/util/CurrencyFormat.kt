package com.rajkashiv.task.ui.util

import java.text.NumberFormat
import java.util.Locale

private val inLocale = Locale("en", "IN")
private val nf = NumberFormat.getCurrencyInstance(inLocale).apply {
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}

fun inr(value: Double): String = nf.format(value)
