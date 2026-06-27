package com.example.project1

import com.example.project1.data.CartItem
import com.example.project1.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class CartBusinessLogicTest {
    @Test
    fun `CartItem totalPrice should be product price multiplied by quantity`() {
        val product =
            Product(
                id = "id",
                name = "name",
                shortDescription = "short",
                longDescription = "long",
                priceInKopecks = 25000, // 250.00 rubles
                imageUrl = "url",
                categoryId = "catId",
            )

        val cartItem = CartItem(product, "XL", 3)

        assertEquals(750.0, cartItem.totalPrice, 0.001)
    }
}
