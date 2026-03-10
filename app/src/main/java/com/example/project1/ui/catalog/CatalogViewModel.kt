package com.example.project1.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.CategoryTranslator
import com.example.project1.data.Product
import com.example.project1.data.ProductRepository
import com.example.project1.data.Resource
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private var allProducts: List<Product> = emptyList()
    private var currentFilter: String = "Все"

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading as Resource<List<Product>>
            try {
                val result = repository.getAllProducts()
                if (result.isSuccess) {
                    allProducts = result.getOrNull() ?: emptyList()
                    applyFilter(currentFilter)
                } else {
                    _products.value = Resource.Error(result.exceptionOrNull()?.message ?: "Ошибка")
                }
            } catch (e: Exception) {
                _products.value = Resource.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading as Resource<List<String>>
            try {
                val result = repository.getCategories()
                if (result.isSuccess) {
                    val originalCategories = result.getOrNull() ?: emptyList()
                    // Переводим категории на русский
                    val translatedCategories = CategoryTranslator.getTranslatedCategories(originalCategories)
                    _categories.value = Resource.Success(translatedCategories)
                } else {
                    _categories.value = Resource.Error(result.exceptionOrNull()?.message ?: "Ошибка")
                }
            } catch (e: Exception) {
                _categories.value = Resource.Error(e.message ?: "Ошибка")
            }
        }
    }

    fun filterByCategory(russianCategory: String) {
        currentFilter = russianCategory

        // Для фильтрации используем оригинальные названия
        val filterForApi = when (russianCategory) {
            "Все" -> "Все"
            "Новинки" -> "Новинки"
            else -> CategoryTranslator.reverseTranslate(russianCategory)
        }

        applyFilter(filterForApi, isOriginal = russianCategory !in listOf("Все", "Новинки"))
    }

    private fun applyFilter(category: String, isOriginal: Boolean = false) {
        val filtered = when {
            category == "Все" -> allProducts
            category == "Новинки" -> allProducts.take(3)
            else -> {
                if (isOriginal) {
                    allProducts.filter { it.category == category }
                } else {
                    allProducts.filter { it.category == category }
                }
            }
        }
        _products.value = Resource.Success(filtered)
    }

    fun refresh() {
        loadProducts()
        loadCategories()
    }
}