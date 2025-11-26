package com.example.projecto_prueba_final.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val imagenUrl: String?
)