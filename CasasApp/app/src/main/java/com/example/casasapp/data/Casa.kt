package com.example.casasapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_casas")
data class Casa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val ubicacion: String,
    val tipo: String, // "Venta" o "Alquiler"
    val imagenes: List<String> = emptyList(), // Lista de URLs
    val comentarios: List<Comentario> = emptyList(), // Comentarios
    val propietarioId: String = ""
)