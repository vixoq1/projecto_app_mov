package com.example.projecto_prueba_final.data

import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val dao: ProductoDao) {
    val productos: Flow<List<Producto>> = dao.getAll()

    suspend fun getById(id: Int): Producto? {
        return dao.getById(id)
    }

    suspend fun save(producto: Producto) {
        dao.save(producto)
    }

    suspend fun delete(producto: Producto) {
        dao.delete(producto)
    }
}