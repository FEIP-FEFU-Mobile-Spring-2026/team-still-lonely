package com.example.project1.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PriceFormatter {
    private val format = DecimalFormat("#,###", DecimalFormatSymbols(Locale("ru")))

    fun formatRublesFromKopecks(priceInKopecks: Int): String {
        val rubles = priceInKopecks / 100.0
        return "${format.format(rubles)} ₽"
    }

    fun formatRubles(price: Double): String {
        return "${format.format(price)} ₽"
    }
}