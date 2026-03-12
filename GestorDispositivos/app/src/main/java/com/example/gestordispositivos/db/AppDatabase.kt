package com.example.gestordispositivos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestordispositivos.model.Device
import java.util.concurrent.Executors

/**
 * Base de datos Room (singleton) con datos pre-poblados de ejemplo.
 */
@Database(entities = [Device::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestor_dispositivos_db"
                )
                    .addCallback(prepopulateCallback)
                    .allowMainThreadQueries() // Solo para esta demo educativa
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback que inserta datos de ejemplo la primera vez
         * que se crea la base de datos.
         */
        private val prepopulateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    INSTANCE?.deviceDao()?.insertAll(sampleDevices())
                }
            }
        }

        private fun sampleDevices(): List<Device> = listOf(
            Device(name = "MacBook Pro 14\"",  type = "Portátil",  status = "Activo",         serialNumber = "MBP-2024-001", location = "Oficina A",  lastSync = "2026-02-24T10:00:00"),
            Device(name = "Dell Latitude 5540", type = "Portátil",  status = "Activo",         serialNumber = "DLL-2024-002", location = "Oficina B",  lastSync = "2026-02-24T09:30:00"),
            Device(name = "iPhone 15 Pro",      type = "Móvil",     status = "Activo",         serialNumber = "IPH-2024-003", location = "Campo",      lastSync = "2026-02-24T08:15:00"),
            Device(name = "Samsung Galaxy S24",  type = "Móvil",     status = "Inactivo",       serialNumber = "SAM-2024-004", location = "Almacén",    lastSync = "2026-02-20T14:00:00"),
            Device(name = "iPad Air M2",        type = "Tablet",    status = "Activo",         serialNumber = "IPA-2024-005", location = "Sala reunión", lastSync = "2026-02-23T16:45:00"),
            Device(name = "Lenovo Tab P12",     type = "Tablet",    status = "En reparación",  serialNumber = "LEN-2024-006", location = "Taller",     lastSync = "2026-02-18T11:20:00"),
            Device(name = "HP ProLiant DL380",  type = "Servidor",  status = "Activo",         serialNumber = "HPE-2024-007", location = "CPD",        lastSync = "2026-02-24T07:00:00"),
            Device(name = "Dell PowerEdge R750", type = "Servidor", status = "Inactivo",       serialNumber = "DPE-2024-008", location = "CPD",        lastSync = "2026-02-15T22:00:00"),
            Device(name = "Pixel 8",            type = "Móvil",     status = "En reparación",  serialNumber = "PIX-2024-009", location = "Taller",     lastSync = "2026-02-19T13:30:00"),
            Device(name = "ThinkPad X1 Carbon",  type = "Portátil",  status = "En reparación",  serialNumber = "TPX-2024-010", location = "Taller",     lastSync = "2026-02-17T10:00:00")
        )
    }
}
