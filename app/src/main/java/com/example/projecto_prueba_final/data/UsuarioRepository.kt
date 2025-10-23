package com.example.projecto_prueba_final.data

import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {

    val usuarios: Flow<List<Usuario>> = dao.getAll()

    suspend fun agregar(nombre: String, apellido: String, email: String, telefono: String) {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        require(email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { "El email no es válido" }
        require(telefono.isNotBlank()) { "El teléfono no puede estar vacío" }
        dao.insert(Usuario(nombre = nombre.trim(), apellido = apellido.trim(), email = email.trim(), telefono = telefono.trim()))
    }

    suspend fun actualizar(id: Long, nombre: String, apellido: String, email: String, telefono: String) {
        require(id > 0) { "Id inválido" }
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        require(email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { "El email no es válido" }
        require(telefono.isNotBlank()) { "El teléfono no puede estar vacío" }
        dao.update(Usuario(id = id, nombre = nombre.trim(), apellido = apellido.trim(), email = email.trim(), telefono = telefono.trim()))
    }

    suspend fun eliminar(usuario: Usuario) = dao.delete(usuario)
    suspend fun obtener(id: Long) = dao.findById(id)
}
