package com.example.casasapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casasapp.CasasApplication
import com.example.casasapp.data.Casa
import com.example.casasapp.data.Comentario
import com.example.casasapp.data.RepositorioCasasMock
import com.example.casasapp.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de detalle.
 */
data class DetalleUiState(
    val casa: Casa? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val imagenSeleccionada: Int = 0,
    val nuevoComentario: String = ""
)

/**
 * ViewModel para la pantalla de detalle de una casa.
 * Maneja la carga de datos y comentarios.
 * Usa datos mock cuando la base de datos está vacía.
 */
class DetalleViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = (application as CasasApplication).repository
    
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()
    
    fun cargarCasa(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Intenta cargar desde Room, si no existe busca en Mock
            var casa = repository.getCasa(id)
            if (casa == null) {
                casa = RepositorioCasasMock.getCasaById(id)
            }
            
            _uiState.value = _uiState.value.copy(
                casa = casa,
                isLoading = false,
                error = if (casa == null) "Casa no encontrada" else null
            )
        }
    }
    
    fun seleccionarImagen(index: Int) {
        _uiState.value = _uiState.value.copy(imagenSeleccionada = index)
    }
    
    fun updateNuevoComentario(texto: String) {
        _uiState.value = _uiState.value.copy(nuevoComentario = texto)
    }
    
    fun agregarComentario() {
        val state = _uiState.value
        val casa = state.casa ?: return
        val textoComentario = state.nuevoComentario.trim()
        
        if (textoComentario.isBlank()) return
        
        // Usa el nombre del usuario de la sesión
        val usuario = SessionManager.nombreUsuario.ifBlank { "Usuario" }
        
        viewModelScope.launch {
            val nuevoComentario = Comentario(
                usuario = usuario,
                texto = textoComentario
            )
            
            val casaActualizada = casa.copy(
                comentarios = casa.comentarios + nuevoComentario
            )
            
            // Intenta actualizar en Room (si es una casa de la DB)
            try {
                repository.actualizarCasa(casaActualizada)
            } catch (_: Exception) {
                // Si falla (por ejemplo, es una casa mock), solo actualizamos el estado local
            }
            
            _uiState.value = _uiState.value.copy(
                casa = casaActualizada,
                nuevoComentario = ""
            )
        }
    }
    
    fun eliminarCasa(onComplete: () -> Unit) {
        val casa = _uiState.value.casa ?: return
        
        viewModelScope.launch {
            try {
                repository.eliminarCasa(casa)
            } catch (_: Exception) {
                // Si es una casa mock, no se puede eliminar de la DB
            }
            onComplete()
        }
    }
}
