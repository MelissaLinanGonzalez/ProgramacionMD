package com.example.gestordispositivos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa un dispositivo del inventario.
 */
@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,        // Portátil, Móvil, Tablet, Servidor
    val status: String,      // Activo, Inactivo, En reparación
    val serialNumber: String,
    val location: String,
    val lastSync: String     // Fecha de última sincronización (ISO 8601)
)
