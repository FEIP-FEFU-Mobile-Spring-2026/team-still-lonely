package com.example.project1.ui.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project1.data.Category
import com.example.project1.data.CategoryTranslator
import com.example.project1.data.Product
import com.example.project1.data.ProductRepository
import com.example.project1.data.Resource
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application.applicationContext)

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private var allProducts: List<Product> = emptyList()
    private var allCategories: List<Category> = emptyList()
    private var currentFilter: String = "Новинки"
    private var isDataLoaded = false

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _products.value = Resource.loading()
            _categories.value = Resource.loading()

            try {
                val categoriesResult = repository.getCategories()
                if (categoriesResult.isSuccess) {
                    allCategories = categoriesResult.getOrNull() ?: emptyList()
                    val translatedCategories = CategoryTranslator.getTranslatedCategories(allCategories)
                    _categories.value = Resource.Success(translatedCategories)
                } else {
                    _categories.value = Resource.Error(
                        categoriesResult.exceptionOrNull()?.message ?: "Ошибка загрузки категорий"
                    )
                }

                val productsResult = repository.getAllProducts()
                if (productsResult.isSuccess) {
                    allProducts = productsResult.getOrNull() ?: emptyList()
                    isDataLoaded = true
                    applyFilter(currentFilter)
                } else {
                    _products.value = Resource.Error(
                        productsResult.exceptionOrNull()?.message ?: "Ошибка загрузки товаров"
                    )
                }
            } catch (e: Exception) {
                _products.value = Resource.Error(e.message ?: "Ошибка загрузки данных")
                _categories.value = Resource.Error(e.message ?: "Ошибка загрузки данных")
            }
        }
    }

    fun filterByCategory(russianCategory: String) {
        currentFilter = russianCategory
        if (isDataLoaded) {
            applyFilter(russianCategory)
        }
    }

    private fun applyFilter(russianCategory: String) {
        val filtered = when (russianCategory) {
            "Новинки" -> allProducts.filter { it.isNew }
            "Все" -> allProducts
            else -> {
                val categoryId = CategoryTranslator.getCategoryId(russianCategory, allCategories)
                if (categoryId != null) {
                    allProducts.filter { it.categoryId == categoryId }
                } else {
                    emptyList()
                }
            }
        }
        _products.value = Resource.Success(filtered)
    }

    fun refresh() {
        repository.clearCache()
        loadData()
    }

    fun getCurrentFilter(): String = currentFilter

    fun setFilter(category: String) {
        currentFilter = category
        if (isDataLoaded) {
            applyFilter(category)
        }
    }

    fun getProductById(id: String): Product? {
        return allProducts.find { it.id == id }
    }
}