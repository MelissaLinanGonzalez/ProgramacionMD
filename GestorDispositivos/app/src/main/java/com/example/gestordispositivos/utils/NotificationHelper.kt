package com.example.gestordispositivos.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gestordispositivos.R

/**
 * Helper para crear y gestionar notificaciones locales.
 * Crea automáticamente el canal de notificación para Android 8+.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "gestor_dispositivos_channel"
        const val NOTIFICATION_SYNC_ID = 1001
        const val NOTIFICATION_REPORT_ID = 1002
    }

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificación (requerido para Android 8.0+).
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra una notificación de sincronización completada.
     */
    fun showSyncNotification() {
        showNotification(
            id = NOTIFICATION_SYNC_ID,
            title = context.getString(R.string.notification_sync_title),
            text = context.getString(R.string.notification_sync_text)
        )
    }

    /**
     * Muestra una notificación de reporte enviado.
     */
    fun showReportNotification() {
        showNotification(
            id = NOTIFICATION_REPORT_ID,
            title = context.getString(R.string.notification_report_title),
            text = context.getString(R.string.notification_report_text)
        )
    }

    /**
     * Construye y muestra una notificación genérica.
     */
    private fun showNotification(id: Int, title: String, text: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            // El usuario no ha concedido permiso POST_NOTIFICATIONS (Android 13+)
            e.printStackTrace()
        }
    }
}
