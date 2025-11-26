package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projecto_prueba_final.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: AuthViewModel, onLoginSuccess: () -> Unit, onNavigateToSignUp: () -> Unit) {
    val formState by vm.loginFormState.collectAsStateWithLifecycle()

    if (formState.loginSuccess) {
        LaunchedEffect(Unit) { onLoginSuccess() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.email,
            onValueChange = { vm.onLoginFormChange(it, formState.password) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = formState.password,
            onValueChange = { vm.onLoginFormChange(formState.email, it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        formState.loginError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { vm.login() }) {
            Text("Iniciar Sesión")
        }

        TextButton(onClick = onNavigateToSignUp) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}