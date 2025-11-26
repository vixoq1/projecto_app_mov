package com.example.projecto_prueba_final.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Producto::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productos.db"
                )
                .fallbackToDestructiveMigration() // Añadido para manejar cambios de versión
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}