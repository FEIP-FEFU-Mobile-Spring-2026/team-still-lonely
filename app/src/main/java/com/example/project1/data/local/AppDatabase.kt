package com.example.project1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project1.data.local.dao.CategoryDao
import com.example.project1.data.local.dao.ProductDao
import com.example.project1.data.local.entities.CategoryEntity
import com.example.project1.data.local.entities.ProductEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}