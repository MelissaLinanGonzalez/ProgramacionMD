package com.example.casasapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromComentarioList(value: List<Comentario>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toComentarioList(value: String): List<Comentario> {
        val listType = object : TypeToken<List<Comentario>>() {}.type
        return Gson().fromJson(value, listType)
    }
}