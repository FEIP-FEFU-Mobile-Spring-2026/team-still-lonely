package com.example.project1.data

import java.io.Serializable

data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: List<String> = emptyList(),
    val sizes: List<Size> = emptyList(),
    val categoryId: String,
    val material: String = "",
    val weight: String = "",
    val season: String = "",
    val countryOfOrigin: String = ""
) : Serializable {
    val description: String get() = shortDescription
    val price: Double get() = priceInKopecks / 100.0
    val category: String get() = categoryId
    val isNew: Boolean get() = tags.contains("New")
}

data class Size(
    val id: String,
    val name: String
) : Serializable

data class ProductsResponse(
    val categories: List<Category>,
    val items: List<Product>
) : Serializable

data class Category(
    val id: String,
    val name: String
) : Serializable
