package com.example.casasapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CasaDao {

    @Query("SELECT * FROM tabla_casas ORDER BY id DESC")
    fun getAllCasas(): Flow<List<Casa>>

    @Query("SELECT * FROM tabla_casas WHERE id = :id")
    suspend fun getCasaById(id: Int): Casa?

    @Query("SELECT * FROM tabla_casas WHERE tipo = :tipo ORDER BY id DESC")
    fun getCasasByTipo(tipo: String): Flow<List<Casa>>

    @Query("SELECT * FROM tabla_casas WHERE nombre LIKE '%' || :query || '%' OR ubicacion LIKE '%' || :query || '%' ORDER BY id DESC")
    fun buscarCasas(query: String): Flow<List<Casa>>

    // Usamos Long para obtener el ID generado si lo necesitamos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCasa(casa: Casa): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCasa(casa: Casa)

    @Delete
    suspend fun deleteCasa(casa: Casa)

    @Query("DELETE FROM tabla_casas WHERE id = :id")
    suspend fun deleteCasaById(id: Int)
}