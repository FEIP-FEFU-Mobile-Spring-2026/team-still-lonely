package com.example.project1.data

import com.google.gson.annotations.SerializedName

// Этот класс больше не используется, но оставим для совместимости
data class ApiProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating? = null
) {
    data class Rating(
        val rate: Double,
        val count: Int
    )
}