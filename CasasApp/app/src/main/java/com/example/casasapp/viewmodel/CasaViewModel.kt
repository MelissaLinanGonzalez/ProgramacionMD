package com.example.casasapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casasapp.CasasApplication
import com.example.casasapp.data.Casa
import com.example.casasapp.data.RepositorioCasasMock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel principal para la galería de casas.
 * Maneja el estado de la lista, filtros y búsqueda.
 * Combina datos mock + casas guardadas en Room para mostrar ambas.
 */
class CasaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CasasApplication).repository

    // Estado del filtro seleccionado
    private val _filtroTipo = MutableStateFlow<String?>(null)
    val filtroTipo: StateFlow<String?> = _filtroTipo.asStateFlow()

    // Estado de búsqueda
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Lista de casas filtrada (mock + Room)
    private val _casas = MutableStateFlow<List<Casa>>(RepositorioCasasMock.casasMock)
    val casas: StateFlow<List<Casa>> = _casas.asStateFlow()

    // Casas almacenadas en Room (se actualiza reactivamente)
    private var casasRoom: List<Casa> = emptyList()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Observar los cambios en Room y combinar con mock
        viewModelScope.launch {
            repository.todasLasCasas.collectLatest { casasDb ->
                casasRoom = casasDb
                aplicarFiltros()
            }
        }
    }

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
        // Combinar casas mock + casas de Room
        val casasBase = RepositorioCasasMock.casasMock + casasRoom

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
        aplicarFiltros()
    }

    fun eliminarCasa(casa: Casa) {
        viewModelScope.launch {
            try {
                repository.eliminarCasa(casa)
            } catch (_: Exception) {
                // Si es una casa mock, solo la quitamos de la lista local
                _casas.value = _casas.value.filter { it.id != casa.id }
            }
        }
    }
}

