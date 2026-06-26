package com.example.project1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project1.data.local.dao.CartDao
import com.example.project1.data.local.dao.CategoryDao
import com.example.project1.data.local.dao.ProductDao
import com.example.project1.data.local.entities.CartEntity
import com.example.project1.data.local.entities.CategoryEntity
import com.example.project1.data.local.entities.ProductEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class, CartEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalog_database"
                )
                    .fallbackToDestructiveMigration() // Разрешаем деструктивную миграцию
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}