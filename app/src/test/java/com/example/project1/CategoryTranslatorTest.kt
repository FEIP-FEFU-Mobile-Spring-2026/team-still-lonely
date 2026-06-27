package com.example.project1

import com.example.project1.data.Category
import com.example.project1.data.CategoryTranslator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryTranslatorTest {
    private val categories =
        listOf(
            Category("1", "Одежда"),
            Category("2", "Обувь"),
        )

    @Test
    fun `getTranslatedCategories should prepend Novinki and Vse`() {
        val expected = listOf("Новинки", "Все", "Одежда", "Обувь")
        val actual = CategoryTranslator.getTranslatedCategories(categories)
        assertEquals(expected, actual)
    }

    @Test
    fun `getCategoryId should return correct id for valid name`() {
        val actual = CategoryTranslator.getCategoryId("Обувь", categories)
        assertEquals("2", actual)
    }

    @Test
    fun `getCategoryId should return null for unknown name`() {
        val actual = CategoryTranslator.getCategoryId("Электроника", categories)
        assertNull(actual)
    }

    @Test
    fun `getCategoryName should return correct name for valid id`() {
        val actual = CategoryTranslator.getCategoryName("1", categories)
        assertEquals("Одежда", actual)
    }

    @Test
    fun `getCategoryName should return id if not found`() {
        val actual = CategoryTranslator.getCategoryName("999", categories)
        assertEquals("999", actual)
    }
}
