package com.example.project1.data

object CategoryTranslator {

    // Теперь работает с категориями из JSON
    fun getTranslatedCategories(categories: List<Category>): List<String> {
        // "Новинки" всегда первым, затем "Все", потом остальные категории по имени
        val categoryNames = categories.map { it.name }
        return listOf("Новинки", "Все") + categoryNames
    }

    // Для обратного преобразования (по имени категории ищем id)
    fun getCategoryId(categoryName: String, categories: List<Category>): String? {
        return categories.find { it.name == categoryName }?.id
    }

    fun getCategoryName(categoryId: String, categories: List<Category>): String {
        return categories.find { it.id == categoryId }?.name ?: categoryId
    }
}