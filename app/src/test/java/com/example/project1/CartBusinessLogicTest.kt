package com.example.project1

import com.example.project1.data.CartItem
import com.example.project1.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class CartBusinessLogicTest {

    private fun createProduct(priceInKopecks: Int) = Product(
        id = "id",
        name = "name",
        shortDescription = "short",
        longDescription = "long",
        priceInKopecks = priceInKopecks,
        imageUrl = "url",
        categoryId = "catId"
    )

    @Test
    fun `CartItem totalPrice should be product price multiplied by quantity`() {
        val product = createProduct(25000) // 250.00 rubles
        val cartItem = CartItem(product, "XL", 3)
        assertEquals(750.0, cartItem.totalPrice, 0.001)
    }

    @Test
    fun `CartItem totalPrice should be zero when quantity is zero`() {
        val product = createProduct(10000)
        val cartItem = CartItem(product, "M", 0)
        assertEquals(0.0, cartItem.totalPrice, 0.001)
    }

    @Test
    fun `CartItem totalPrice with large values should be correct`() {
        val product = createProduct(1000000) // 10,000.00 rubles
        val cartItem = CartItem(product, "L", 10)
        assertEquals(100000.0, cartItem.totalPrice, 0.001)
    }

    @Test
    fun `Cart list total calculation should work correctly`() {
        val p1 = createProduct(10000) // 100.0
        val p2 = createProduct(5000)  // 50.0
        
        val items = listOf(
            CartItem(p1, "S", 2), // 200.0
            CartItem(p2, "M", 1)  // 50.0
        )
        
        val total = items.sumOf { it.totalPrice }
        assertEquals(250.0, total, 0.001)
    }
}
