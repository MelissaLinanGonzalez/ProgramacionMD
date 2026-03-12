package com.example.gestordispositivos.network

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log

/**
 * Helper que simula la conexión con dispositivos físicos
 * mediante Bluetooth y Wi-Fi. En un entorno real, aquí se
 * implementaría el escaneo y enlace con dispositivos BLE/Wi-Fi Direct.
 */
class WirelessHelper(private val context: Context) {

    private val tag = "WirelessHelper"

    /**
     * Simula una conexión Bluetooth.
     * Comprueba si el adaptador Bluetooth está disponible y habilitado.
     * @return resultado descriptivo de la simulación
     */
    fun simulateBluetoothConnection(): String {
        return try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Log.w(tag, "Bluetooth no disponible en este dispositivo")
                "Bluetooth no disponible en este dispositivo"
            } else if (!bluetoothAdapter.isEnabled) {
                Log.w(tag, "Bluetooth está desactivado")
                "Bluetooth desactivado. Actívalo para conectar dispositivos."
            } else {
                Log.d(tag, "Bluetooth habilitado — simulando conexión con dispositivo...")
                // Simulación: en producción aquí iría BluetoothDevice.connectGatt()
                Thread.sleep(500) // Simular latencia de conexión
                Log.d(tag, "Conexión Bluetooth simulada correctamente")
                context.getString(com.example.gestordispositivos.R.string.wireless_bt_connected)
            }
        } catch (e: SecurityException) {
            Log.e(tag, "Permiso Bluetooth denegado: ${e.message}")
            "Permiso Bluetooth denegado"
        }
    }

    /**
     * Simula una conexión Wi-Fi.
     * Comprueba el estado actual del adaptador Wi-Fi.
     * @return resultado descriptivo de la simulación
     */
    fun simulateWifiConnection(): String {
        return try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (!wifiManager.isWifiEnabled) {
                Log.w(tag, "Wi-Fi está desactivado")
                "Wi-Fi desactivado. Actívalo para sincronizar."
            } else {
                Log.d(tag, "Wi-Fi habilitado — simulando conexión...")
                Thread.sleep(300)
                val info = wifiManager.connectionInfo
                Log.d(tag, "Conectado a red: ${info.ssid}")
                context.getString(com.example.gestordispositivos.R.string.wireless_wifi_connected)
            }
        } catch (e: SecurityException) {
            Log.e(tag, "Permiso Wi-Fi denegado: ${e.message}")
            "Permiso Wi-Fi denegado"
        }
    }
}
