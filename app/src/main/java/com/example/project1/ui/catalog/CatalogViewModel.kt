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

class CatalogViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository = ProductRepository(application.applicationContext)

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private var allProducts: List<Product> = emptyList()
    private var allCategories: List<Category> = emptyList()
    private var currentFilter: String = "Новинки"
    private var isDataLoaded = false

    init {
        viewModelScope.launch {
            repository.isNetworkAvailable.collect { connected ->
                _isNetworkAvailable.postValue(connected)
            }
        }
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            repository.getCatalog().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (!isDataLoaded) {
                            @Suppress("UNCHECKED_CAST")
                            _products.value = Resource.Loading as Resource<List<Product>>
                        }
                    }
                    is Resource.Success -> {
                        allProducts = resource.data
                        isDataLoaded = true
                        loadCategories()
                        applyFilter(currentFilter)
                    }
                    is Resource.Error -> {
                        if (!isDataLoaded) {
                            _products.value = Resource.Error(resource.message)
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadCategories() {
        allCategories = repository.getCategories()
        val translated = CategoryTranslator.getTranslatedCategories(allCategories)
        _categories.value = Resource.Success(translated)
    }

    fun filterByCategory(category: String) {
        currentFilter = category
        if (isDataLoaded) {
            applyFilter(category)
        }
    }

    private fun applyFilter(category: String) {
        val filtered =
            when (category) {
                "Новинки" -> allProducts.filter { it.isNew }
                "Все" -> allProducts
                else -> {
                    val categoryId = CategoryTranslator.getCategoryId(category, allCategories)
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
        isDataLoaded = false
        loadData()
    }

    fun getCurrentFilter(): String = currentFilter

    fun setFilter(category: String) {
        currentFilter = category
        if (isDataLoaded) {
            applyFilter(category)
        }
    }

    fun getProductById(id: String): Product? = allProducts.find { it.id == id }
}
