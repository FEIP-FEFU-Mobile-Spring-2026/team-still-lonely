package com.example.project1

import com.example.project1.data.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductBusinessLogicTest {
    private fun createProduct(
        priceInKopecks: Int,
        tags: List<String>,
    ) = Product(
        id = "id",
        name = "name",
        shortDescription = "short",
        longDescription = "long",
        priceInKopecks = priceInKopecks,
        imageUrl = "url",
        tags = tags,
        categoryId = "catId",
    )

    @Test
    fun `price property should convert kopecks to rubles`() {
        val product = createProduct(10050, emptyList())
        assertEquals(100.5, product.price, 0.001)
    }

    @Test
    fun `isNew should be true when tags contain New`() {
        val product = createProduct(1000, listOf("Tag1", "New"))
        assertTrue(product.isNew)
    }

    @Test
    fun `isNew should be false when tags do not contain New`() {
        val product = createProduct(1000, listOf("Tag1", "Old"))
        assertFalse(product.isNew)
    }
}
