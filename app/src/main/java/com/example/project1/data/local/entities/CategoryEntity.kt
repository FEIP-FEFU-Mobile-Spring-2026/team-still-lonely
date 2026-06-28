package com.example.project1.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.project1.data.Category   // ← ТВОЙ КЛАСС ИЗ data.kt

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String
) {
    fun toCategory() = Category(id, name)
}