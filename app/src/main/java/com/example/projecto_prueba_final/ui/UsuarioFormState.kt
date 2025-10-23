package com.example.projecto_prueba_final.ui

data class UsuarioFormState(
    val id: Long? = null,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val error: String? = null
)
