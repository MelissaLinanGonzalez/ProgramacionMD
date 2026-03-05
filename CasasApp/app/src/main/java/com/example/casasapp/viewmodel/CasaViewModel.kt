package com.example.casasapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.casasapp.data.Casa
import com.example.casasapp.data.RepositorioCasasMock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel principal para la galería de casas.
 * Maneja el estado de la lista, filtros y búsqueda.
 * Usa datos mock para evitar crashes en pantalla vacía.
 */
class CasaViewModel : ViewModel() {
    
    // Estado del filtro seleccionado
    private val _filtroTipo = MutableStateFlow<String?>(null)
    val filtroTipo: StateFlow<String?> = _filtroTipo.asStateFlow()
    
    // Estado de búsqueda
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()
    
    // Lista de casas filtrada
    private val _casas = MutableStateFlow<List<Casa>>(RepositorioCasasMock.casasMock)
    val casas: StateFlow<List<Casa>> = _casas.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun setFiltroTipo(tipo: String?) {
        _filtroTipo.value = tipo
        aplicarFiltros()
    }
    
    fun setBusqueda(query: String) {
        _busqueda.value = query
        aplicarFiltros()
    }
    
    private fun aplicarFiltros() {
        val tipo = _filtroTipo.value
        val query = _busqueda.value
        val casasBase = RepositorioCasasMock.casasMock
        
        _casas.value = casasBase.filter { casa ->
            val matchesTipo = tipo == null || casa.tipo == tipo
            val matchesBusqueda = query.isBlank() ||
                casa.nombre.contains(query, ignoreCase = true) ||
                casa.ubicacion.contains(query, ignoreCase = true) ||
                casa.descripcion.contains(query, ignoreCase = true)
            matchesTipo && matchesBusqueda
        }
    }
    
    fun refrescar() {
        _casas.value = RepositorioCasasMock.casasMock
        aplicarFiltros()
    }
    
    fun eliminarCasa(casa: Casa) {
        // No hay eliminación en mock, solo filtramos la lista
        _casas.value = _casas.value.filter { it.id != casa.id }
    }
}
