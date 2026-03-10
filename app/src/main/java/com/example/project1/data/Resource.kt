package com.example.project1.data

// Класс для представления состояния загрузки
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    object Loading : Resource<Nothing>()
}