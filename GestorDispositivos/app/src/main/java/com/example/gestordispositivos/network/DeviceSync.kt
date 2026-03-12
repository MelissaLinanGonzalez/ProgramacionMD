package com.example.gestordispositivos.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Clase que simula la sincronización de dispositivos con un servidor remoto
 * utilizando Retrofit. Realiza una llamada HTTP real a JSONPlaceholder
 * para demostrar conectividad de red.
 */
class DeviceSync {

    private val tag = "DeviceSync"

    /**
     * Simula la sincronización: envía datos al servidor y recibe respuesta.
     * @param onSuccess callback cuando la sincronización es exitosa
     * @param onError callback cuando hay un error
     */
    fun syncWithServer(onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d(tag, "Iniciando sincronización con el servidor...")

        // Simular envío de datos de un dispositivo
        val deviceData = mapOf(
            "title" to "Sync dispositivos",
            "body" to "Datos de inventario sincronizados",
            "userId" to "1"
        )

        RetrofitClient.instance.syncDevice(deviceData).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    Log.d(tag, "Sincronización exitosa: ${response.body()}")
                    onSuccess()
                } else {
                    Log.e(tag, "Error del servidor: ${response.code()}")
                    onError("Error del servidor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e(tag, "Fallo de red: ${t.message}")
                onError("Sin conexión: ${t.message}")
            }
        })
    }
}
