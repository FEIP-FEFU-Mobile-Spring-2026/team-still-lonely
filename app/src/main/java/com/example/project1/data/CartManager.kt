package com.example.project1.data

import android.util.Log

object CartManager {
    // Ключ = "productId_size" (например: "1_XL")
    private val cartItems = mutableMapOf<String, CartItem>()

    data class CartItem(
        val product: Product,
        val size: String,
        var quantity: Int = 1,
        var isSelected: Boolean = true  // По умолчанию выбран
    ) {
        // Уникальный ключ для этого товара с размером
        val key: String
            get() = "${product.id}_$size"
    }

    // Добавить товар в корзину с конкретным размером
    fun addToCart(product: Product, size: String) {
        val key = "${product.id}_$size"
        val existingItem = cartItems[key]

        if (existingItem == null) {
            cartItems[key] = CartItem(product, size, 1, true) // Новый товар выбран по умолчанию
            Log.d("CartManager", "Товар добавлен: ${product.name} ($size)")
        } else {
            existingItem.quantity++
            Log.d("CartManager", "Количество увеличено: ${product.name} ($size) = ${existingItem.quantity}")
        }
    }

    // Изменить количество товара конкретного размера
    fun updateQuantity(productId: Int, size: String, newQuantity: Int) {
        val key = "${productId}_$size"
        if (newQuantity <= 0) {
            cartItems.remove(key)
        } else {
            cartItems[key]?.quantity = newQuantity
        }
    }

    // Переключить выбор товара
    fun toggleSelection(productId: Int, size: String): Boolean {
        val key = "${productId}_$size"
        val item = cartItems[key]
        return if (item != null) {
            item.isSelected = !item.isSelected
            item.isSelected
        } else {
            false
        }
    }

    // Установить выбор для всех товаров
    fun selectAll(select: Boolean) {
        cartItems.values.forEach { it.isSelected = select }
    }

    // Проверить, все ли товары выбраны
    fun isAllSelected(): Boolean {
        return cartItems.values.all { it.isSelected }
    }

    // Получить количество выбранных товаров
    fun getSelectedCount(): Int {
        return cartItems.values.count { it.isSelected }
    }

    // Получить выбранные товары
    fun getSelectedItems(): List<CartItem> {
        return cartItems.values.filter { it.isSelected }
    }

    // Удалить товар конкретного размера
    fun removeFromCart(productId: Int, size: String) {
        val key = "${productId}_$size"
        cartItems.remove(key)
    }

    // Получить все товары в корзине
    fun getCartItems(): List<CartItem> {
        return cartItems.values.toList()
    }

    // Получить количество конкретного товара конкретного размера
    fun getItemQuantity(productId: Int, size: String): Int {
        val key = "${productId}_$size"
        return cartItems[key]?.quantity ?: 0
    }

    // Проверить, есть ли товар конкретного размера в корзине
    fun isInCart(productId: Int, size: String): Boolean {
        val key = "${productId}_$size"
        return cartItems.containsKey(key)
    }

    // Очистить корзину
    fun clearCart() {
        cartItems.clear()
    }

    // Получить общую стоимость ТОЛЬКО выбранных товаров
    fun getTotalPrice(): Double {
        return cartItems.values
            .filter { it.isSelected }
            .sumOf { it.product.price * it.quantity }
    }

    // Получить общее количество выбранных товаров
    fun getTotalItemCount(): Int {
        return cartItems.values
            .filter { it.isSelected }
            .sumOf { it.quantity }
    }
}