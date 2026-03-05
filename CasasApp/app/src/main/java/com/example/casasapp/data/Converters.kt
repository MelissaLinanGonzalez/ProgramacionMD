package com.example.casasapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type Converters para Room.
 * Permite almacenar List<String> y List<Comentario> como JSON en SQLite.
 */
class Converters {
    private val gson = Gson()

    // Conversores para List<String> (imágenes)
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    // Conversores para List<Comentario>
    @TypeConverter
    fun fromComentarioList(value: List<Comentario>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toComentarioList(value: String): List<Comentario> {
        val listType = object : TypeToken<List<Comentario>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
