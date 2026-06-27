package com.example.project1.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("catalog")
    suspend fun getCatalog(
        @Header("Authorization") authorization: String,
    ): Response<CatalogResponse>
}
