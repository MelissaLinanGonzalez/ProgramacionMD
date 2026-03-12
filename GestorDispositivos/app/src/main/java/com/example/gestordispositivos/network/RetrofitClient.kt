package com.example.gestordispositivos.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que proporciona la instancia de Retrofit
 * configurada con el base URL de demostración.
 */
object RetrofitClient {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
