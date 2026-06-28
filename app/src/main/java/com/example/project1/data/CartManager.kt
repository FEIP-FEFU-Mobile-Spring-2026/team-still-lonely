package com.example.project1.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val PREFS_NAME = "cart_prefs"
    private const val KEY_CART_ITEMS = "cart_items"

    private val gson = Gson()
    private var prefs: Context? = null
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

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext
        loadFromStorage()
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
        persist()
    }

    fun updateQuantity(productId: String, size: String, newQuantity: Int) {
        val key = "${productId}_$size"
        if (newQuantity <= 0) {
            cartItems.remove(key)
        } else {
            cartItems[key]?.quantity = newQuantity
        }
        persist()
    }

    fun toggleSelection(productId: String, size: String): Boolean {
        val key = "${productId}_$size"
        val item = cartItems[key]
        return if (item != null) {
            item.isSelected = !item.isSelected
            persist()
            item.isSelected
        } else {
            false
        }
    }

    fun selectAll(select: Boolean) {
        cartItems.values.forEach { it.isSelected = select }
        persist()
    }

    fun isAllSelected(): Boolean = cartItems.values.all { it.isSelected }

    fun getSelectedCount(): Int = cartItems.values.count { it.isSelected }

    fun getSelectedItems(): List<CartItem> = cartItems.values.filter { it.isSelected }

    fun removeFromCart(productId: String, size: String) {
        cartItems.remove("${productId}_$size")
        persist()
    }

    fun getCartItems(): List<CartItem> = cartItems.values.toList()

    fun getItemQuantity(productId: String, size: String): Int {
        return cartItems["${productId}_$size"]?.quantity ?: 0
    }

    fun isInCart(productId: String, size: String): Boolean {
        return cartItems.containsKey("${productId}_$size")
    }

    fun clearCart() {
        cartItems.clear()
        persist()
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

    private fun persist() {
        val context = prefs ?: return
        val json = gson.toJson(cartItems.values.toList())
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CART_ITEMS, json)
            .apply()
    }

    private fun loadFromStorage() {
        val context = prefs ?: return
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CART_ITEMS, null) ?: return

        val type = object : TypeToken<List<CartItem>>() {}.type
        val stored: List<CartItem> = gson.fromJson(json, type) ?: return
        cartItems.clear()
        stored.forEach { item -> cartItems[item.key] = item }
    }
}
