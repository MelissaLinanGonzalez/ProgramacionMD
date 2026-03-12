package com.example.gestordispositivos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gestordispositivos.db.AppDatabase
import com.example.gestordispositivos.db.DeviceDao
import com.example.gestordispositivos.model.Device
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentados de la base de datos Room.
 * Se ejecutan en un dispositivo/emulador Android real.
 *
 * Cubre:
 * - Integración: creación de BD in-memory, inserción y lectura.
 * - Regresión: conteos por estado y por tipo para los gráficos.
 */
@RunWith(AndroidJUnit4::class)
class DeviceDatabaseInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: DeviceDao

    @Before
    fun setup() {
        // Creamos una base de datos temporal en la memoria RAM que se borrará al terminar
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Permitido solo para testing
            .build()
        dao = database.deviceDao()
    }

    @After
    fun tearDown() {
        // Cerramos la base de datos tras cada prueba
        database.close()
    }

    // ──────── Tests de Integración (Prueba PI-01 del Documento) ────────

    @Test
    fun insertAndRetrieveDevice() {
        val device = Device(
            name = "Test Laptop",
            type = "Portátil",
            status = "Activo",
            serialNumber = "TEST-001",
            location = "Lab",
            lastSync = "2026-02-24T10:00:00"
        )

        val id = dao.insertDevice(device)
        val retrieved = dao.getDeviceById(id)

        assertNotNull(retrieved)
        assertEquals("Test Laptop", retrieved!!.name)
        assertEquals("Portátil", retrieved.type)
        assertEquals("Activo", retrieved.status)
    }

    @Test
    fun getAllDevicesReturnsInsertedItems() {
        val devices = listOf(
            Device(name = "D1", type = "Móvil",    status = "Activo",   serialNumber = "S1", location = "A", lastSync = ""),
            Device(name = "D2", type = "Tablet",   status = "Inactivo", serialNumber = "S2", location = "B", lastSync = ""),
            Device(name = "D3", type = "Servidor", status = "Activo",   serialNumber = "S3", location = "C", lastSync = "")
        )
        dao.insertAll(devices)

        val result = dao.getAllDevices()
        assertEquals(3, result.size)
    }

    // ──────── Tests de Regresión (Prueba PR-01 del Documento) ────────

    @Test
    fun countByStatusReturnsCorrectCounts() {
        dao.insertAll(
            listOf(
                Device(name = "A", type = "Portátil", status = "Activo",         serialNumber = "1", location = "", lastSync = ""),
                Device(name = "B", type = "Móvil",    status = "Activo",         serialNumber = "2", location = "", lastSync = ""),
                Device(name = "C", type = "Tablet",   status = "Inactivo",       serialNumber = "3", location = "", lastSync = ""),
                Device(name = "D", type = "Servidor", status = "En reparación",  serialNumber = "4", location = "", lastSync = "")
            )
        )

        // Verificamos que el DAO suma bien los porcentajes para el Gráfico de tarta
        assertEquals(2, dao.countByStatus("Activo"))
        assertEquals(1, dao.countByStatus("Inactivo"))
        assertEquals(1, dao.countByStatus("En reparación"))
        assertEquals(0, dao.countByStatus("Desconocido"))
    }

    @Test
    fun countByTypeReturnsCorrectCounts() {
        dao.insertAll(
            listOf(
                Device(name = "A", type = "Portátil", status = "Activo", serialNumber = "1", location = "", lastSync = ""),
                Device(name = "B", type = "Portátil", status = "Activo", serialNumber = "2", location = "", lastSync = ""),
                Device(name = "C", type = "Móvil",    status = "Activo", serialNumber = "3", location = "", lastSync = "")
            )
        )

        assertEquals(2, dao.countByType("Portátil"))
        assertEquals(1, dao.countByType("Móvil"))
        assertEquals(0, dao.countByType("Tablet"))
    }

    @Test
    fun deleteAllClearsDatabase() {
        dao.insertAll(
            listOf(
                Device(name = "A", type = "Portátil", status = "Activo", serialNumber = "1", location = "", lastSync = ""),
                Device(name = "B", type = "Móvil",    status = "Activo", serialNumber = "2", location = "", lastSync = "")
            )
        )

        assertEquals(2, dao.getTotal())
        dao.deleteAll()
        assertEquals(0, dao.getTotal()) // Tras borrar, debe haber 0
    }
}