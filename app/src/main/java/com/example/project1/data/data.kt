package com.example.project1.data

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isNew: Boolean = false,  // Добавьте это поле
    val category: String = "",
    val availableSizes: List<String> = listOf("XS", "S", "M", "L", "XL")
)