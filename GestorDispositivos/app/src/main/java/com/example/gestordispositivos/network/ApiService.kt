package com.example.gestordispositivos.network

import com.example.gestordispositivos.model.Device
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface Retrofit que define los endpoints del API simulado.
 * En un escenario real, apuntaría a un backend propio.
 * Aquí se usa JSONPlaceholder como demostración de conectividad HTTP.
 */
interface ApiService {

    /**
     * Simula la obtención de dispositivos desde el servidor.
     * Usa un endpoint genérico de JSONPlaceholder (/posts) como placeholder.
     */
    @GET("posts")
    fun getRemoteDevices(): Call<List<Map<String, Any>>>

    /**
     * Simula el envío de datos de dispositivo al servidor.
     */
    @POST("posts")
    fun syncDevice(@Body device: Map<String, String>): Call<Map<String, Any>>
}
