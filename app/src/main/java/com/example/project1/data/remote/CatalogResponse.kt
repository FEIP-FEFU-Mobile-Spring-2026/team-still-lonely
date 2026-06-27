package com.example.project1.data.remote

data class CatalogResponse(
    val categories: List<CategoryDto>,
    val items: List<ProductDto>,
)

data class CategoryDto(
    val id: String,
    val name: String,
)

data class ProductDto(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: List<String>,
    val sizes: List<SizeDto>,
    val categoryId: String,
    val material: String?,
    val weight: String?,
    val season: String?,
    val countryOfOrigin: String?,
)

data class SizeDto(
    val id: String,
    val name: String,
)
