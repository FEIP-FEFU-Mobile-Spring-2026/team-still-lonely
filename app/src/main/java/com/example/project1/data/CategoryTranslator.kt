package com.example.project1.data

object CategoryTranslator {

    // Словарь для перевода категорий
    private val categoryMap = mapOf(
        "men's clothing" to "Мужская одежда",
        "women's clothing" to "Женская одежда",
        "jewelery" to "Драгоценности",
        "electronics" to "Электроника"
    )

    // Перевести категорию с английского на русский
    fun translate(category: String): String {
        return categoryMap[category] ?: category
    }

    // Получить список всех категорий для фильтра (с переводом)
    fun getTranslatedCategories(originalCategories: List<String>): List<String> {
        return listOf("Все", "Новинки") + originalCategories.map { translate(it) }
    }

    // Обратное преобразование (для фильтрации)
    fun reverseTranslate(russianCategory: String): String {
        return categoryMap.entries.find { it.value == russianCategory }?.key ?: russianCategory
    }
}