package com.example.project1.data

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    object Loading : Resource<Nothing>()

    companion object {
        // Добавляем вспомогательные функции для создания экземпляров с правильным типом
        @Suppress("UNCHECKED_CAST")
        fun <T> loading(): Resource<T> = Loading as Resource<T>
    }
}