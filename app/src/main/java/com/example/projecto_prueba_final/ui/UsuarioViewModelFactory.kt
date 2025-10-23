package com.example.projecto_prueba_final.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projecto_prueba_final.data.AppDatabase
import com.example.projecto_prueba_final.data.UsuarioRepository


class UsuarioViewModelFactory(app: Application) : ViewModelProvider.Factory {
    private val repo by lazy {
        val dao = AppDatabase.get(app).usuarioDao()
        UsuarioRepository(dao)
    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        UsuarioViewModel(repo) as T
}
