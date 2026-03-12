package com.example.gestordispositivos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gestordispositivos.db.AppDatabase
import com.example.gestordispositivos.db.DeviceDao
import com.example.gestordispositivos.model.Device
import com.example.gestordispositivos.utils.NotificationHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity para que el usuario pueda reportar una incidencia
 * sobre un dispositivo del inventario.
 *
 * - El Spinner de dispositivos se carga desde Room.
 * - Al enviar, se actualiza el estado del dispositivo seleccionado.
 */
class ReportActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var spinnerDevice: Spinner
    private lateinit var spinnerNewStatus: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var notificationHelper: NotificationHelper

    private lateinit var dao: DeviceDao
    private var deviceList: List<Device> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        notificationHelper = NotificationHelper(this)
        dao = AppDatabase.getDatabase(this).deviceDao()

        // Toolbar con botón de volver
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarReport)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Enlazar vistas
        etTitle = findViewById(R.id.etReportTitle)
        etDescription = findViewById(R.id.etReportDescription)
        spinnerDevice = findViewById(R.id.spinnerDevice)
        spinnerNewStatus = findViewById(R.id.spinnerNewStatus)
        spinnerPriority = findViewById(R.id.spinnerPriority)

        // Cargar dispositivos desde Room en hilo de fondo
        loadDevicesIntoSpinner()

        // Botón enviar
        findViewById<MaterialButton>(R.id.btnSendReport).setOnClickListener {
            sendReport()
        }
    }

    /**
     * Lee los dispositivos de la BD en hilo IO y llena el Spinner
     * con sus nombres en el hilo principal.
     */
    private fun loadDevicesIntoSpinner() {
        lifecycleScope.launch(Dispatchers.IO) {
            val devices = dao.getAllDevices()

            withContext(Dispatchers.Main) {
                deviceList = devices

                if (devices.isEmpty()) {
                    Toast.makeText(
                        this@ReportActivity,
                        getString(R.string.report_no_devices),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@withContext
                }

                val names = devices.map { it.name }
                val adapter = ArrayAdapter(
                    this@ReportActivity,
                    android.R.layout.simple_spinner_item,
                    names
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDevice.adapter = adapter
            }
        }
    }

    /**
     * Valida el formulario, actualiza el estado del dispositivo
     * seleccionado en Room y cierra la Activity.
     */
    private fun sendReport() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priority = spinnerPriority.selectedItem.toString()

        // Validación
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.report_error_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (deviceList.isEmpty()) {
            Toast.makeText(this, getString(R.string.report_no_devices), Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener dispositivo y nuevo estado seleccionados
        val selectedDevice = deviceList[spinnerDevice.selectedItemPosition]
        val newStatus = spinnerNewStatus.selectedItem.toString()

        // Crear copia con el nuevo estado
        val updatedDevice = selectedDevice.copy(status = newStatus)

        // Log del reporte
        android.util.Log.d(
            "ReportActivity",
            "Reporte enviado → Título: $title | Dispositivo: ${selectedDevice.name} " +
                    "| Nuevo estado: $newStatus | Prioridad: $priority | Descripción: $description"
        )

        // Actualizar en BD y cerrar en hilo de fondo
        lifecycleScope.launch(Dispatchers.IO) {
            dao.updateDevice(updatedDevice)

            withContext(Dispatchers.Main) {
                // Notificación de confirmación
                notificationHelper.showReportNotification()

                // Feedback al usuario
                Toast.makeText(
                    this@ReportActivity,
                    getString(R.string.report_sent_ok),
                    Toast.LENGTH_SHORT
                ).show()

                // Cerrar activity volviendo a MainActivity
                finish()
            }
        }
    }
}
