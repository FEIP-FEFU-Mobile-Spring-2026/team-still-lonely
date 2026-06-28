package com.example.project1.data

import android.content.Context
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.entities.CategoryEntity
import com.example.project1.data.local.entities.ProductEntity
import com.example.project1.data.remote.RetrofitClient
import com.example.project1.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()

    private val networkMonitor = NetworkMonitor(context)

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable = _isNetworkAvailable.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            networkMonitor.isConnected.collect { connected ->
                _isNetworkAvailable.value = connected
            }
        }
    }

    fun getCatalog(): Flow<Resource<List<Product>>> = flow {
        val cachedProducts = getCachedProducts()
        val cachedCategories = getCachedCategories()

        if (cachedProducts.isNotEmpty() && cachedCategories.isNotEmpty()) {
            emit(Resource.Success(cachedProducts))
        } else {
            @Suppress("UNCHECKED_CAST")
            emit(Resource.Loading as Resource<List<Product>>)
        }

        if (_isNetworkAvailable.value) {
            try {
                val response = RetrofitClient.apiService.getCatalog("Bearer ${RetrofitClient.TOKEN}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val products = body.items.map { dto ->
                            Product(
                                id = dto.id,
                                name = dto.name,
                                shortDescription = dto.shortDescription,
                                longDescription = dto.longDescription,
                                priceInKopecks = dto.priceInKopecks,
                                imageUrl = dto.imageUrl,
                                tags = dto.tags,
                                sizes = dto.sizes.map { Size(it.id, it.name) },
                                categoryId = dto.categoryId,
                                material = dto.material ?: "",
                                weight = dto.weight ?: "",
                                season = dto.season ?: "",
                                countryOfOrigin = dto.countryOfOrigin ?: ""
                            )
                        }
                        val categories = body.categories.map { Category(it.id, it.name) }

                        saveToCache(products, categories)
                        emit(Resource.Success(products))
                    } else if (cachedProducts.isEmpty()) {
                        emit(Resource.Error("Пустой ответ"))
                    }
                } else if (cachedProducts.isEmpty()) {
                    emit(Resource.Error("Ошибка сервера: ${response.code()}"))
                }
            } catch (e: Exception) {
                if (cachedProducts.isEmpty()) {
                    emit(Resource.Error("Нет интернета"))
                }
            }
        } else if (cachedProducts.isEmpty()) {
            emit(Resource.Error("Нет подключения к интернету и нет кэша"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAll().map { it.toCategory() }
        }
    }

    private suspend fun getCachedProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            try {
                productDao.getAll().map { it.toProduct() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun getCachedCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                categoryDao.getAll().map { it.toCategory() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun saveToCache(products: List<Product>, categories: List<Category>) {
        withContext(Dispatchers.IO) {
            productDao.clearAll()
            productDao.insertAll(products.map { ProductEntity.fromProduct(it) })
            categoryDao.clearAll()
            categoryDao.insertAll(categories.map { CategoryEntity(it.id, it.name) })
        }
    }

    fun isNetworkAvailable(): Boolean = _isNetworkAvailable.value
}
