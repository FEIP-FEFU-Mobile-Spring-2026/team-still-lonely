package com.example.project1.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.entities.CartEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CartManager {

    private lateinit var cartDao: com.example.project1.data.local.dao.CartDao
    private lateinit var database: AppDatabase
    private var isInitialized = false

    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> = _totalQuantity

    fun init(context: Context) {
        if (!isInitialized) {
            database = AppDatabase.getInstance(context)
            cartDao = database.cartDao()
            isInitialized = true
            refreshTotalQuantity()
        }
    }

    private fun refreshTotalQuantity() {
        CoroutineScope(Dispatchers.IO).launch {
            val count = cartDao.getTotalQuantity()
            _totalQuantity.postValue(count)
        }
    }

    suspend fun addToCart(productId: String, sizeId: String, quantity: Int = 1) {
        withContext(Dispatchers.IO) {
            val existing = cartDao.getByProductAndSize(productId, sizeId)
            if (existing != null) {
                cartDao.updateQuantity(productId, sizeId, existing.quantity + quantity)
            } else {
                cartDao.insert(CartEntity(productId = productId, sizeId = sizeId, quantity = quantity))
            }
            refreshTotalQuantity()
        }
    }

    suspend fun removeFromCart(productId: String, sizeId: String) {
        withContext(Dispatchers.IO) {
            cartDao.deleteByProductAndSize(productId, sizeId)
            refreshTotalQuantity()
        }
    }

    suspend fun updateQuantity(productId: String, sizeId: String, newQuantity: Int) {
        withContext(Dispatchers.IO) {
            if (newQuantity <= 0) {
                removeFromCart(productId, sizeId)
            } else {
                cartDao.updateQuantity(productId, sizeId, newQuantity)
                refreshTotalQuantity()
            }
        }
    }

    suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.clearAll()
            refreshTotalQuantity()
        }
    }

    fun getCartItems(): Flow<List<CartItem>> = flow {
        val entities = cartDao.getAll()
        val items = entities.mapNotNull { entity ->
            val product = getProductById(entity.productId)
            if (product != null) {
                CartItem(
                    product = product,
                    sizeId = entity.sizeId,
                    quantity = entity.quantity
                )
            } else {
                null
            }
        }
        emit(items)
    }

    private fun getProductById(productId: String): Product? {
        return ProductRepository.cachedProducts?.find { it.id == productId }
    }
}

data class CartItem(
    val product: Product,
    val sizeId: String,
    val quantity: Int
) {
    val sizeName: String
        get() = product.sizes.find { it.id == sizeId }?.name ?: sizeId

    val totalPrice: Double
        get() = product.price * quantity
}
