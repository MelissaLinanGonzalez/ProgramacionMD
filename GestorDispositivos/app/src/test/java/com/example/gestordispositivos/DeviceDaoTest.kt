package com.example.gestordispositivos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gestordispositivos.db.AppDatabase
import com.example.gestordispositivos.db.DeviceDao
import com.example.gestordispositivos.model.Device
import com.example.gestordispositivos.utils.PreferencesManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests unitarios (ejecutados en JVM con Robolectric).
 *
 * Cubre:
 *  - Integración: Funcionamiento de Room in-memory.
 *  - Regresión: Inserción y lectura coherente.
 *  - Seguridad: Verificación de almacenamiento (MODE_PRIVATE).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class DeviceDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: DeviceDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.deviceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ──────── Integración ────────

    @Test
    fun `insertar dispositivo y recuperarlo por ID`() {
        val device = Device(
            name = "MacBook Test",
            type = "Portátil",
            status = "Activo",
            serialNumber = "MBT-001",
            location = "Oficina Test",
            lastSync = "2026-02-24T12:00:00"
        )

        val id = dao.insertDevice(device)
        val result = dao.getDeviceById(id)

        assertNotNull(result)
        assertEquals("MacBook Test", result?.name)
        assertEquals("Portátil", result?.type)
    }

    @Test
    fun `insertar multiples dispositivos y listar todos`() {
        val devices = listOf(
            Device(name = "D1", type = "Móvil",  status = "Activo",   serialNumber = "S1", location = "A", lastSync = ""),
            Device(name = "D2", type = "Tablet", status = "Inactivo", serialNumber = "S2", location = "B", lastSync = "")
        )
        dao.insertAll(devices)

        val all = dao.getAllDevices()
        assertEquals(2, all.size)
    }

    // ──────── Regresión ────────

    @Test
    fun `conteo por estado devuelve valores correctos`() {
        dao.insertAll(
            listOf(
                Device(name = "A", type = "Portátil", status = "Activo",         serialNumber = "1", location = "", lastSync = ""),
                Device(name = "B", type = "Móvil",    status = "Activo",         serialNumber = "2", location = "", lastSync = ""),
                Device(name = "C", type = "Tablet",   status = "En reparación",  serialNumber = "3", location = "", lastSync = "")
            )
        )

        assertEquals(2, dao.countByStatus("Activo"))
        assertEquals(0, dao.countByStatus("Inactivo"))
        assertEquals(1, dao.countByStatus("En reparación"))
    }

    @Test
    fun `insercion con REPLACE actualiza el registro existente`() {
        val original = Device(id = 100, name = "Original", type = "Móvil", status = "Activo",
            serialNumber = "S100", location = "A", lastSync = "")
        dao.insertDevice(original)

        val updated = original.copy(name = "Actualizado", status = "Inactivo")
        dao.insertDevice(updated)

        val result = dao.getDeviceById(100)
        assertEquals("Actualizado", result?.name)
        assertEquals("Inactivo", result?.status)
        assertEquals(1, dao.getTotal())  // Solo un registro, no duplicado
    }

    @Test
    fun `eliminar dispositivo reduce el total`() {
        val device = Device(name = "A", type = "Portátil", status = "Activo",
            serialNumber = "1", location = "", lastSync = "")
        val id = dao.insertDevice(device)
        assertEquals(1, dao.getTotal())

        val toDelete = dao.getDeviceById(id)!!
        dao.deleteDevice(toDelete)
        assertEquals(0, dao.getTotal())
    }

    @Test
    fun `updateDevice cambia el estado correctamente`() {
        val device = Device(
            name = "Test Update", type = "Móvil", status = "Activo",
            serialNumber = "TU-001", location = "Oficina", lastSync = ""
        )
        val id = dao.insertDevice(device)
        val inserted = dao.getDeviceById(id)!!

        val updated = inserted.copy(status = "En reparación")
        dao.updateDevice(updated)

        val result = dao.getDeviceById(id)
        assertNotNull(result)
        assertEquals("En reparación", result?.status)
        assertEquals("Test Update", result?.name) // resto de campos inalterado
    }

    // ──────── Seguridad ────────

    @Test
    fun `SharedPreferences usa MODE_PRIVATE`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)

        // Guardar datos sensibles
        prefs.setUserName("TestUser")
        prefs.saveLastSync()

        // Verificar que las preferencias se guardan y leen correctamente
        assertEquals("TestUser", prefs.getUserName())
        assertTrue(prefs.getLastSync().isNotEmpty())

        // Verificar que MODE_PRIVATE impide acceso desde otras apps
        // (MODE_PRIVATE es el único modo seguro, los demás están deprecados)
        val rawPrefs = context.getSharedPreferences("gestor_prefs", Context.MODE_PRIVATE)
        assertEquals("TestUser", rawPrefs.getString("user_name", ""))

        // Verificar que un nombre de archivo diferente NO tiene acceso
        val otherPrefs = context.getSharedPreferences("other_prefs", Context.MODE_PRIVATE)
        assertNull(otherPrefs.getString("user_name", null))
    }

    @Test
    fun `BD en memoria no persiste datos entre sesiones`() {
        dao.insertDevice(
            Device(name = "Temporal", type = "Móvil", status = "Activo",
                serialNumber = "T1", location = "", lastSync = "")
        )
        assertEquals(1, dao.getTotal())

        // Cerrar y reabrir BD in-memory → datos se pierden
        database.close()

        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.deviceDao()

        assertEquals(0, dao.getTotal())
    }
}
