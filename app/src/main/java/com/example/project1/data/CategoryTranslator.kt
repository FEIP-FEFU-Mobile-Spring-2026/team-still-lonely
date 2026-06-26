package com.example.project1.data

object CategoryTranslator {

    fun getTranslatedCategories(categories: List<Category>): List<String> {
        val categoryNames = categories.map { it.name }
        return listOf("Новинки", "Все") + categoryNames
    }

    fun getCategoryId(categoryName: String, categories: List<Category>): String? {
        return categories.find { it.name == categoryName }?.id
    }

    fun getCategoryName(categoryId: String, categories: List<Category>): String {
        return categories.find { it.id == categoryId }?.name ?: categoryId
    }
}
