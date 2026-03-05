package com.example.casasapp.data

/**
 * Data class para representar un comentario de usuario sobre una casa.
 */
data class Comentario(
    val usuario: String,
    val texto: String,
    val fecha: Long = System.currentTimeMillis()
)
