package com.example.casasapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que abstrae el acceso a datos.
 * Implementa el patrón Repository para separar lógica de datos de ViewModels.
 */
class RepositorioCasas(private val casaDao: CasaDao) {
    
    val todasLasCasas: Flow<List<Casa>> = casaDao.getAllCasas()
    
    fun getCasasByTipo(tipo: String): Flow<List<Casa>> {
        return casaDao.getCasasByTipo(tipo)
    }
    
    fun buscarCasas(query: String): Flow<List<Casa>> {
        return casaDao.buscarCasas(query)
    }
    
    suspend fun getCasa(id: Int): Casa? {
        return casaDao.getCasaById(id)
    }
    
    suspend fun agregarCasa(casa: Casa): Long {
        return casaDao.insertCasa(casa)
    }
    
    suspend fun actualizarCasa(casa: Casa) {
        casaDao.updateCasa(casa)
    }
    
    suspend fun eliminarCasa(casa: Casa) {
        casaDao.deleteCasa(casa)
    }
    
    suspend fun eliminarCasaPorId(id: Int) {
        casaDao.deleteCasaById(id)
    }
}