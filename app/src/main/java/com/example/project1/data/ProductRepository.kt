package com.example.project1.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProductRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getAllProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = apiService.getProduct(id)
            if (response.isSuccessful) {
                val product = response.body()?.toProduct()
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Продукт не найден"))
                }
            } else {
                Result.failure(Exception("Ошибка загрузки: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val response = apiService.getProductsByCategory(category)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toProduct() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Ошибка загрузки: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<String>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки категорий"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllProductsFlow(): Flow<Resource<List<Product>>> = flow {
        // Используем аннотацию @Suppress для подавления ошибки
        @Suppress("UNCHECKED_CAST")
        emit(Resource.Loading as Resource<List<Product>>)
        try {
            val result = getAllProducts()
            if (result.isSuccess) {
                emit(Resource.Success(result.getOrThrow()))
            } else {
                emit(Resource.Error(result.exceptionOrNull()?.message ?: "Ошибка"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Ошибка"))
        }
    }.flowOn(Dispatchers.IO)
}