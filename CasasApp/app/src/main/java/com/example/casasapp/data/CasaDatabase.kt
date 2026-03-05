package com.example.casasapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Base de datos Room para la aplicación CasasApp.
 * Singleton pattern para asegurar una única instancia.
 */
@Database(entities = [Casa::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CasaDatabase : RoomDatabase() {
    
    abstract fun casaDao(): CasaDao
    
    companion object {
        @Volatile
        private var INSTANCE: CasaDatabase? = null
        
        fun getDatabase(context: Context): CasaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CasaDatabase::class.java,
                    "casas_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
