package com.example.gestordispositivos

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordispositivos.db.AppDatabase
import com.example.gestordispositivos.network.DeviceSync
import com.example.gestordispositivos.network.WirelessHelper
import com.example.gestordispositivos.utils.NotificationHelper
import com.example.gestordispositivos.utils.PreferencesManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var tvLastSync: TextView
    private lateinit var rvDeviceSummary: RecyclerView

    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper

    companion object {
        private const val REQUEST_ENABLE_BT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dependencias
        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)

        // Enlazar vistas
        pieChart = findViewById(R.id.pieChart)
        tvLastSync = findViewById(R.id.tvLastSync)
        rvDeviceSummary = findViewById(R.id.rvDeviceSummary)

        // Configurar toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Configurar UI
        setupPieChart()
        setupRecyclerView()
        updateLastSyncLabel()

        // Botón de sincronización
        findViewById<MaterialButton>(R.id.btnSync)
            .setOnClickListener { performSync() }

        // FAB → Reportar incidencia
        findViewById<FloatingActionButton>(R.id.fabReport).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        // Botón → Compartir Informe vía Bluetooth / Mensajes
        findViewById<MaterialButton>(R.id.btnCompartirInforme).setOnClickListener {
            compartirInformeInalambrico()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refrescar datos al volver de ReportActivity
        setupPieChart()
        setupRecyclerView()
        updateLastSyncLabel()
    }

    // ──────── PieChart ────────

    private fun setupPieChart() {
        // Envolvemos la consulta en una corrutina en segundo plano (IO)
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = database.deviceDao()
            val active = dao.countByStatus("Activo")
            val inactive = dao.countByStatus("Inactivo")
            val repair = dao.countByStatus("En reparación")
            val total = active + inactive + repair

            if (total == 0) return@launch

            // Volvemos al hilo principal para dibujar la interfaz
            withContext(Dispatchers.Main) {
                // Crear entradas
                val entries = mutableListOf<PieEntry>()
                if (active > 0)   entries.add(PieEntry(active.toFloat(),   getString(R.string.status_active)))
                if (inactive > 0)  entries.add(PieEntry(inactive.toFloat(),  getString(R.string.status_inactive)))
                if (repair > 0)   entries.add(PieEntry(repair.toFloat(),   getString(R.string.status_repair)))

                // Colores
                val colors = mutableListOf<Int>()
                if (active > 0)   colors.add(ContextCompat.getColor(this@MainActivity, R.color.chart_active))
                if (inactive > 0)  colors.add(ContextCompat.getColor(this@MainActivity, R.color.chart_inactive))
                if (repair > 0)   colors.add(ContextCompat.getColor(this@MainActivity, R.color.chart_repair))

                val dataSet = PieDataSet(entries, "").apply {
                    this.colors = colors
                    valueTextSize = 14f
                    valueTextColor = Color.WHITE
                    valueFormatter = PercentFormatter(pieChart)
                    sliceSpace = 3f
                }

                pieChart.apply {
                    data = PieData(dataSet)
                    description.isEnabled = false
                    isDrawHoleEnabled = true
                    holeRadius = 45f
                    transparentCircleRadius = 50f
                    setUsePercentValues(true)
                    setEntryLabelTextSize(13f)
                    setEntryLabelColor(Color.WHITE)
                    legend.textSize = 13f
                    legend.textColor = ContextCompat.getColor(this@MainActivity, R.color.md_on_surface)
                    animateY(1000, Easing.EaseInOutQuad)
                    invalidate()
                }
            }
        }
    }

    // ──────── RecyclerView ────────

    private fun setupRecyclerView() {
        // Envolvemos la consulta en una corrutina
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = database.deviceDao()

            val types = listOf(
                DeviceTypeSummary(getString(R.string.type_laptop),  dao.countByType("Portátil"),  android.R.drawable.ic_menu_agenda),
                DeviceTypeSummary(getString(R.string.type_mobile),  dao.countByType("Móvil"),     android.R.drawable.ic_menu_call),
                DeviceTypeSummary(getString(R.string.type_tablet),  dao.countByType("Tablet"),    android.R.drawable.ic_menu_gallery),
                DeviceTypeSummary(getString(R.string.type_server),  dao.countByType("Servidor"),  android.R.drawable.ic_menu_manage)
            )

            // Volvemos al hilo principal para actualizar la lista visual
            withContext(Dispatchers.Main) {
                rvDeviceSummary.layoutManager = LinearLayoutManager(this@MainActivity)
                rvDeviceSummary.adapter = DeviceSummaryAdapter(types)
            }
        }
    }

    // ──────── Sincronización ────────

    private fun performSync() {
        Toast.makeText(this, getString(R.string.sync_in_progress), Toast.LENGTH_SHORT).show()

        // 1. Simular conexión inalámbrica
        val wireless = WirelessHelper(this)
        val wifiResult = wireless.simulateWifiConnection()
        android.util.Log.d("MainActivity", "Resultado Wi-Fi: $wifiResult")

        // 2. Sincronizar con servidor vía Retrofit
        val sync = DeviceSync()
        sync.syncWithServer(
            onSuccess = {
                runOnUiThread {
                    // Guardar timestamp de sincronización
                    preferencesManager.saveLastSync()
                    updateLastSyncLabel()
                    Toast.makeText(this, getString(R.string.sync_complete), Toast.LENGTH_SHORT).show()
                    notificationHelper.showSyncNotification()
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "${getString(R.string.sync_error)}: $error", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    // ──────── Bluetooth & Compartir Informe ────────

    /**
     * Criterio c) Uso de clases para conexión con dispositivos inalámbricos (Bluetooth).
     * Criterio d) Uso de clases para intercambio de mensajes de texto/multimedia (ACTION_SEND).
     *
     * Comprueba el estado del Bluetooth, genera un informe de inventario desde Room
     * y lanza un Intent chooser para enviar el informe vía Bluetooth o SMS/MMS.
     */
    @Suppress("DEPRECATION")
    private fun compartirInformeInalambrico() {
        // a) Instanciar BluetoothAdapter
        val bluetoothAdapter: BluetoothAdapter? =
            (getSystemService(BluetoothManager::class.java))?.adapter
                ?: BluetoothAdapter.getDefaultAdapter()

        // b) Comprobar si el Bluetooth está soportado
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show()
            return
        }

        // b) Comprobar si el Bluetooth está activado
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth desactivado. Solicitando activación…", Toast.LENGTH_SHORT).show()
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        // c) Bluetooth activado → leer dispositivos de Room en corrutina
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = database.deviceDao()

            // Contar por tipo y estado
            val totalPortatiles = dao.countByType("Portátil")
            val totalMoviles    = dao.countByType("Móvil")
            val totalTablets    = dao.countByType("Tablet")
            val totalServidores = dao.countByType("Servidor")

            val activos      = dao.countByStatus("Activo")
            val inactivos    = dao.countByStatus("Inactivo")
            val enReparacion = dao.countByStatus("En reparación")
            val totalGeneral = dao.getTotal()

            // d) Generar String formateado con el informe
            val informe = buildString {
                appendLine("📋 Informe de Inventario de Dispositivos")
                appendLine("=========================================")
                appendLine()
                appendLine("📊 Total de dispositivos: $totalGeneral")
                appendLine()
                appendLine("── Por Tipo ──")
                appendLine("  • Portátiles: $totalPortatiles")
                appendLine("  • Móviles: $totalMoviles")
                appendLine("  • Tablets: $totalTablets")
                appendLine("  • Servidores: $totalServidores")
                appendLine()
                appendLine("── Por Estado ──")
                appendLine("  • Activos: $activos")
                appendLine("  • Inactivos: $inactivos")
                appendLine("  • En reparación: $enReparacion")
                appendLine()
                appendLine("Generado desde GestorDispositivos.")
            }

            // e) Crear Intent ACTION_SEND y lanzar el chooser en el hilo principal
            withContext(Dispatchers.Main) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Informe de Inventario - GestorDispositivos")
                    putExtra(Intent.EXTRA_TEXT, informe)
                }
                val chooser = Intent.createChooser(sendIntent, "Enviar informe vía Bluetooth o SMS")
                startActivity(chooser)
            }
        }
    }

    // ──────── Helpers ────────

    private fun updateLastSyncLabel() {
        val lastSync = preferencesManager.getLastSync()
        tvLastSync.text = if (lastSync.isNotEmpty()) {
            getString(R.string.last_sync, lastSync)
        } else {
            getString(R.string.no_sync_yet)
        }
    }
}