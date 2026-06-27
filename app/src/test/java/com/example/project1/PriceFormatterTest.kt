package com.example.project1

import com.example.project1.utils.PriceFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceFormatterTest {
    @Test
    fun `formatRublesFromKopecks should correctly divide and format with rounding`() {
        val kopecks = 123456
        val expected = "1 235 ₽"
        val actual = PriceFormatter.formatRublesFromKopecks(kopecks)
        assertEquals(expected.replace(" ", "\u00A0"), actual.replace(" ", "\u00A0"))
    }

    @Test
    fun `formatRubles should correctly format double value with rounding`() {
        val price = 1000.0
        val expected = "1 000 ₽"
        val actual = PriceFormatter.formatRubles(price)
        assertEquals(expected.replace(" ", "\u00A0"), actual.replace(" ", "\u00A0"))
    }

    @Test
    fun `formatRubles with decimals should correctly format with rounding`() {
        val price = 99.99
        val expected = "100 ₽"
        val actual = PriceFormatter.formatRubles(price)
        assertEquals(expected.replace(" ", "\u00A0"), actual.replace(" ", "\u00A0"))
    }
}
