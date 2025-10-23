package com.example.projecto_prueba_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.projecto_prueba_final.ui.UsuarioViewModel
import com.example.projecto_prueba_final.ui.UsuarioViewModelFactory
import com.example.projecto_prueba_final.ui.screen.UsuarioListScreen


class MainActivity : ComponentActivity() {
    private val vm: UsuarioViewModel by viewModels { UsuarioViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UsuarioListScreen(vm) }
    }
}
