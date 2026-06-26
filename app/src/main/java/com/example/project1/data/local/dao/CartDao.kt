package com.example.project1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project1.data.local.entities.CartEntity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartEntity>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND sizeName = :sizeName")
    suspend fun getByProductAndSize(productId: String, sizeName: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId AND sizeName = :sizeName")
    suspend fun updateQuantity(productId: String, sizeName: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND sizeName = :sizeName")
    suspend fun deleteByProductAndSize(productId: String, sizeName: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    suspend fun getTotalQuantity(): Int
}