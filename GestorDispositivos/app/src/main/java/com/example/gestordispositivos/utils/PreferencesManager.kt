package com.example.gestordispositivos.utils

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Wrapper sobre SharedPreferences para gestionar las preferencias
 * del usuario de forma centralizada y segura.
 */
class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "gestor_prefs"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_USER_NAME = "user_name"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ──────── Última sincronización ────────

    fun saveLastSync() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        prefs.edit().putString(KEY_LAST_SYNC, dateFormat.format(Date())).apply()
    }

    fun getLastSync(): String {
        return prefs.getString(KEY_LAST_SYNC, "") ?: ""
    }

    // ──────── Modo oscuro ────────

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    // ──────── Notificaciones ────────

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    // ──────── Nombre de usuario ────────

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Usuario") ?: "Usuario"
    }
}
