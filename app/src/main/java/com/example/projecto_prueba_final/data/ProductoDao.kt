package com.example.projecto_prueba_final.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun getAll(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(producto: Producto)

    @Delete
    suspend fun delete(producto: Producto)
}