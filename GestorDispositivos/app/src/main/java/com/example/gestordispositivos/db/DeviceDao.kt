package com.example.gestordispositivos.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestordispositivos.model.Device

/**
 * DAO para la entidad Device. Contiene todas las consultas
 * necesarias para CRUD y para alimentar los gráficos.
 */
@Dao
interface DeviceDao {

    // ──────── CRUD ────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: Device): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(devices: List<Device>)

    @Update
    fun updateDevice(device: Device)

    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevices(): List<Device>

    @Query("SELECT * FROM devices WHERE id = :id")
    fun getDeviceById(id: Long): Device?

    @Delete
    fun deleteDevice(device: Device)

    @Query("DELETE FROM devices")
    fun deleteAll()

    // ──────── Consultas para gráficos ────────

    /** Cuenta dispositivos agrupados por estado (para PieChart). */
    @Query("SELECT COUNT(*) FROM devices WHERE status = :status")
    fun countByStatus(status: String): Int

    /** Cuenta dispositivos agrupados por tipo (para RecyclerView resumen). */
    @Query("SELECT COUNT(*) FROM devices WHERE type = :type")
    fun countByType(type: String): Int

    /** Número total de dispositivos. */
    @Query("SELECT COUNT(*) FROM devices")
    fun getTotal(): Int
}
