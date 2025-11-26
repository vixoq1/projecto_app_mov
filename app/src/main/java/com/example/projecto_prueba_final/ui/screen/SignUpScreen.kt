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
fun SignUpScreen(vm: AuthViewModel, onSignUpSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    val formState by vm.signUpFormState.collectAsStateWithLifecycle()

    if (formState.signUpSuccess) {
        LaunchedEffect(Unit) { onSignUpSuccess() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.email,
            onValueChange = { vm.onSignUpFormChange(it, formState.password, formState.confirmPassword) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = formState.password,
            onValueChange = { vm.onSignUpFormChange(formState.email, it, formState.confirmPassword) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = formState.confirmPassword,
            onValueChange = { vm.onSignUpFormChange(formState.email, formState.password, it) },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        formState.signUpError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { vm.signUp() }) {
            Text("Registrarse")
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}