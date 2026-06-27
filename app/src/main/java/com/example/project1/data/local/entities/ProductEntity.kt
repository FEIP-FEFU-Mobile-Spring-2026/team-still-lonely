package com.example.project1.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.project1.data.Product // ← ТВОЙ КЛАСС ИЗ data.kt
import com.example.project1.data.Size // ← ТВОЙ КЛАСС ИЗ data.kt
import com.google.gson.Gson

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: String,
    val sizes: String,
    val categoryId: String,
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun toProduct(): Product {
        val gson = Gson()
        return Product(
            id = id,
            name = name,
            shortDescription = shortDescription,
            longDescription = longDescription,
            priceInKopecks = priceInKopecks,
            imageUrl = imageUrl,
            tags = gson.fromJson(tags, Array<String>::class.java).toList(),
            sizes = gson.fromJson(sizes, Array<Size>::class.java).toList(),
            categoryId = categoryId,
            material = material,
            weight = weight,
            season = season,
            countryOfOrigin = countryOfOrigin,
        )
    }

    companion object {
        fun fromProduct(product: Product): ProductEntity {
            val gson = Gson()
            return ProductEntity(
                id = product.id,
                name = product.name,
                shortDescription = product.shortDescription,
                longDescription = product.longDescription,
                priceInKopecks = product.priceInKopecks,
                imageUrl = product.imageUrl,
                tags = gson.toJson(product.tags),
                sizes = gson.toJson(product.sizes),
                categoryId = product.categoryId,
                material = product.material,
                weight = product.weight,
                season = product.season,
                countryOfOrigin = product.countryOfOrigin,
            )
        }
    }
}
