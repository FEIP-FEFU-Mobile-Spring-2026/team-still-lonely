package com.example.project1

import com.example.project1.data.local.entities.CategoryEntity
import com.example.project1.data.local.entities.ProductEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelMappingTest {
    @Test
    fun `ProductEntity toProduct should map all fields correctly`() {
        val entity =
            ProductEntity(
                id = "1",
                name = "Name",
                shortDescription = "Short",
                longDescription = "Long",
                priceInKopecks = 1000,
                imageUrl = "url",
                categoryId = "cat",
                tags = "[\"tag1\",\"tag2\"]",
                sizes = "[{\"id\":\"s1\",\"name\":\"S\"}]",
                material = "mat",
                weight = "weight",
                season = "season",
                countryOfOrigin = "country",
            )

        val product = entity.toProduct()

        assertEquals(entity.id, product.id)
        assertEquals(entity.name, product.name)
        assertEquals(listOf("tag1", "tag2"), product.tags)
        assertEquals(1, product.sizes.size)
        assertEquals("S", product.sizes[0].name)
        assertEquals(10.0, product.price, 0.001)
    }

    @Test
    fun `CategoryEntity toCategory should map all fields correctly`() {
        val entity = CategoryEntity("id", "name")
        val category = entity.toCategory()

        assertEquals(entity.id, category.id)
        assertEquals(entity.name, category.name)
    }
}
