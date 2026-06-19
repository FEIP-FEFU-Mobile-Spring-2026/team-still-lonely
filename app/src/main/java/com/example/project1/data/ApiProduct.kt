package com.example.project1.data

import com.google.gson.annotations.SerializedName

data class ApiProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,

    // Добавляем поля для рейтинга (опционально)
    val rating: Rating? = null
) {
    data class Rating(
        val rate: Double,
        val count: Int
    )

    // Конвертируем в наш Product
    fun toProduct(): Product {
        return Product(
            id = id,
            name = title,
            description = description,
            price = price,
            imageUrl = image,
            category = category,
            isNew = false, // API не дает информацию о новинках
            availableSizes = listOf("XS", "S", "M", "L", "XL") // Стандартные размеры
        )
    }
}