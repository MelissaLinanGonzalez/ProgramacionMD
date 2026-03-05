package com.example.casasapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casasapp.CasasApplication
import com.example.casasapp.data.Casa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado del formulario de nueva casa.
 */
data class FormularioUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val ubicacion: String = "",
    val tipo: String = "Venta",
    val imagenes: List<String> = emptyList(),
    val propietarioId: String = "usuario_demo",
    
    // Estados de error
    val errorNombre: Boolean = false,
    val errorPrecio: Boolean = false,
    val errorUbicacion: Boolean = false,
    val errorImagenes: Boolean = false,
    
    // Estado general
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false
)

/**
 * ViewModel para el formulario de creación/edición de casas.
 * Maneja validación y guardado.
 */
class FormularioViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = (application as CasasApplication).repository
    
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()
    
    fun updateNombre(valor: String) {
        _uiState.value = _uiState.value.copy(
            nombre = valor,
            errorNombre = false
        )
    }
    
    fun updateDescripcion(valor: String) {
        _uiState.value = _uiState.value.copy(descripcion = valor)
    }
    
    fun updatePrecio(valor: String) {
        _uiState.value = _uiState.value.copy(
            precio = valor,
            errorPrecio = false
        )
    }
    
    fun updateUbicacion(valor: String) {
        _uiState.value = _uiState.value.copy(
            ubicacion = valor,
            errorUbicacion = false
        )
    }
    
    fun updateTipo(valor: String) {
        _uiState.value = _uiState.value.copy(tipo = valor)
    }
    
    fun agregarImagen(uri: String) {
        val nuevasImagenes = _uiState.value.imagenes + uri
        _uiState.value = _uiState.value.copy(
            imagenes = nuevasImagenes,
            errorImagenes = false
        )
    }
    
    fun eliminarImagen(index: Int) {
        val nuevasImagenes = _uiState.value.imagenes.toMutableList()
        if (index in nuevasImagenes.indices) {
            nuevasImagenes.removeAt(index)
            _uiState.value = _uiState.value.copy(imagenes = nuevasImagenes)
        }
    }
    
    fun validarYGuardar(): Boolean {
        val state = _uiState.value
        var esValido = true
        
        val errorNombre = state.nombre.isBlank()
        val errorPrecio = state.precio.isBlank() || state.precio.toDoubleOrNull() == null
        val errorUbicacion = state.ubicacion.isBlank()
        val errorImagenes = state.imagenes.isEmpty()
        
        if (errorNombre || errorPrecio || errorUbicacion || errorImagenes) {
            _uiState.value = state.copy(
                errorNombre = errorNombre,
                errorPrecio = errorPrecio,
                errorUbicacion = errorUbicacion,
                errorImagenes = errorImagenes
            )
            esValido = false
        }
        
        if (esValido) {
            guardarCasa()
        }
        
        return esValido
    }
    
    private fun guardarCasa() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val state = _uiState.value
            val nuevaCasa = Casa(
                nombre = state.nombre.trim(),
                descripcion = state.descripcion.trim(),
                precio = state.precio.toDouble(),
                ubicacion = state.ubicacion.trim(),
                tipo = state.tipo,
                imagenes = state.imagenes,
                comentarios = emptyList(),
                propietarioId = state.propietarioId
            )
            
            repository.agregarCasa(nuevaCasa)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                guardadoExitoso = true
            )
        }
    }
    
    fun resetFormulario() {
        _uiState.value = FormularioUiState()
    }
}
