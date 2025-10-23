package com.example.projecto_prueba_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecto_prueba_final.data.Usuario
import com.example.projecto_prueba_final.data.UsuarioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repo: UsuarioRepository): ViewModel() {

    val usuarios: StateFlow<List<Usuario>> =
        repo.usuarios.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(UsuarioFormState())
    val form: StateFlow<UsuarioFormState> = _form.asStateFlow()

    fun editar(usuario: Usuario?) {
        _form.value = if (usuario == null) {
            UsuarioFormState()
        } else {
            UsuarioFormState(
                id = usuario.id,
                nombre = usuario.nombre,
                apellido = usuario.apellido,
                email = usuario.email,
                telefono = usuario.telefono
            )
        }
    }

    fun onNombreChange(v: String) { _form.update { it.copy(nombre = v) } }
    fun onApellidoChange(v: String) { _form.update { it.copy(apellido = v) } }
    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onTelefonoChange(v: String) { _form.update { it.copy(telefono = v) } }
    fun limpiarError() { _form.update { it.copy(error = null) } }

    fun guardar(oAlFinal: () -> Unit = {}) = viewModelScope.launch {
        try {
            val f = _form.value
            if (f.id == null) {
                repo.agregar(f.nombre, f.apellido, f.email, f.telefono)
            } else {
                repo.actualizar(f.id, f.nombre, f.apellido, f.email, f.telefono)
            }
            editar(null)
            oAlFinal()
        } catch (e: Exception) {
            _form.update { it.copy(error = e.message ?: "Error desconocido") }
        }
    }

    fun eliminar(usuario: Usuario) = viewModelScope.launch {
        repo.eliminar(usuario)
    }
}
