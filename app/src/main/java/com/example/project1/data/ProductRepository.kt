package com.example.project1.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.project1.R
import java.io.IOException

class ProductRepository(private val context: Context) {

    private var cachedProducts: List<Product>? = null
    private var cachedCategories: List<Category>? = null

    suspend fun getAllProducts(): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                if (cachedProducts == null) {
                    loadProductsFromJson()
                }
                Result.success(cachedProducts ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        return withContext(Dispatchers.IO) {
            try {
                if (cachedCategories == null) {
                    loadProductsFromJson()
                }
                Result.success(cachedCategories ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                if (cachedProducts == null) {
                    loadProductsFromJson()
                }
                val filtered = cachedProducts?.filter { it.categoryId == categoryId } ?: emptyList()
                Result.success(filtered)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun loadProductsFromJson() {
        withContext(Dispatchers.IO) {
            try {
                val jsonString = context.resources.openRawResource(R.raw.products)
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val response = gson.fromJson(jsonString, ProductsResponse::class.java)

                cachedCategories = response.categories
                cachedProducts = response.items
            } catch (e: Exception) {
                throw Exception("Ошибка загрузки данных: ${e.message}")
            }
        }
    }
    fun clearCache() {
        cachedProducts = null
        cachedCategories = null
    }
}