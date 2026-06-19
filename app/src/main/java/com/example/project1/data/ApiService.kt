package com.example.project1.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("products")
    suspend fun getAllProducts(): Response<List<ApiProduct>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiProduct>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<ApiProduct>>

    @GET("products?limit={limit}")
    suspend fun getProductsLimited(@Path("limit") limit: Int): Response<List<ApiProduct>>
}