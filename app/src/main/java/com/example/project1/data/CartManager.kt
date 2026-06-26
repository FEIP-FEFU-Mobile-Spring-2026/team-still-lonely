package com.example.project1.data

import android.util.Log

object CartManager {
    private val cartItems = mutableMapOf<String, CartItem>()

    data class CartItem(
        val product: Product,
        val size: String,
        var quantity: Int = 1,
        var isSelected: Boolean = true
    ) {
        val key: String
            get() = "${product.id}_$size"
    }

    fun addToCart(product: Product, size: String) {
        val key = "${product.id}_$size"
        val existingItem = cartItems[key]

        if (existingItem == null) {
            cartItems[key] = CartItem(product, size, 1, true)
            Log.d("CartManager", "Товар добавлен: ${product.name} ($size)")
        } else {
            existingItem.quantity++
            Log.d("CartManager", "Количество увеличено: ${product.name} ($size) = ${existingItem.quantity}")
        }
    }

    fun updateQuantity(productId: String, size: String, newQuantity: Int) {
        val key = "${productId}_$size"
        if (newQuantity <= 0) {
            cartItems.remove(key)
        } else {
            cartItems[key]?.quantity = newQuantity
        }
    }

    fun toggleSelection(productId: String, size: String): Boolean {
        val key = "${productId}_$size"
        val item = cartItems[key]
        return if (item != null) {
            item.isSelected = !item.isSelected
            item.isSelected
        } else {
            false
        }
    }

    fun selectAll(select: Boolean) {
        cartItems.values.forEach { it.isSelected = select }
    }

    fun isAllSelected(): Boolean {
        return cartItems.values.all { it.isSelected }
    }

    fun getSelectedCount(): Int {
        return cartItems.values.count { it.isSelected }
    }

    fun getSelectedItems(): List<CartItem> {
        return cartItems.values.filter { it.isSelected }
    }

    fun removeFromCart(productId: String, size: String) {
        val key = "${productId}_$size"
        cartItems.remove(key)
    }

    fun getCartItems(): List<CartItem> {
        return cartItems.values.toList()
    }

    fun getItemQuantity(productId: String, size: String): Int {
        val key = "${productId}_$size"
        return cartItems[key]?.quantity ?: 0
    }

    fun isInCart(productId: String, size: String): Boolean {
        val key = "${productId}_$size"
        return cartItems.containsKey(key)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return cartItems.values
            .filter { it.isSelected }
            .sumOf { it.product.price * it.quantity }
    }

    fun getTotalItemCount(): Int {
        return cartItems.values
            .filter { it.isSelected }
            .sumOf { it.quantity }
    }
}