package com.example.casasapp

import android.app.Application
import com.example.casasapp.data.CasaDatabase
import com.example.casasapp.data.RepositorioCasas

/**
 * Application class para inicializar componentes globales.
 * Proporciona acceso lazy al database y repository.
 */
class CasasApplication : Application() {
    
    // Lazy initialization del database
    val database: CasaDatabase by lazy {
        CasaDatabase.getDatabase(this)
    }
    
    // Lazy initialization del repository
    val repository: RepositorioCasas by lazy {
        RepositorioCasas(database.casaDao())
    }
}
