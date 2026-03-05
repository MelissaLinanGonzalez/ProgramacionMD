package com.example.casasapp.data

/**
 * SessionManager - Singleton para gestión de sesión de usuario.
 * Almacena información del usuario actual y su rol.
 */
object SessionManager {
    
    // Datos del usuario actual
    var nombreUsuario: String = ""
        private set
    
    var email: String = ""
        private set
    
    /**
     * Rol del usuario:
     * - "vendedor" = Quiere vender/alquilar propiedades
     * - "comprador" = Quiere comprar/buscar propiedades
     */
    var rol: String = ""
        private set
    
    /**
     * Verifica si hay un usuario con sesión activa
     */
    val isLoggedIn: Boolean
        get() = nombreUsuario.isNotBlank()
    
    /**
     * Inicia sesión con credenciales existentes (simulado)
     */
    fun login(usuario: String, password: String): Boolean {
        // Simulación: aceptar cualquier credencial no vacía
        return if (usuario.isNotBlank() && password.isNotBlank()) {
            nombreUsuario = usuario
            rol = "comprador" // Por defecto al hacer login
            true
        } else {
            false
        }
    }
    
    /**
     * Registra un nuevo usuario con su rol seleccionado
     */
    fun registrar(nombre: String, emailUsuario: String, password: String, rolSeleccionado: String): Boolean {
        return if (nombre.isNotBlank() && emailUsuario.isNotBlank() && password.length >= 4) {
            nombreUsuario = nombre
            email = emailUsuario
            rol = rolSeleccionado
            true
        } else {
            false
        }
    }
    
    /**
     * Cierra la sesión actual
     */
    fun logout() {
        nombreUsuario = ""
        email = ""
        rol = ""
    }
    
    /**
     * Verifica si el usuario puede publicar propiedades
     */
    val puedePublicar: Boolean
        get() = rol == "vendedor"
}
