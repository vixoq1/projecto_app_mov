package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projecto_prueba_final.ui.UsuarioViewModel

@Composable
fun UsuarioDialog(
    vm: UsuarioViewModel,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val form by vm.form.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (form.id == null) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.apellido,
                    onValueChange = vm::onApellidoChange,
                    label = { Text("Apellido") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.email,
                    onValueChange = vm::onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.telefono,
                    onValueChange = vm::onTelefonoChange,
                    label = { Text("Teléfono") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { vm.guardar(onSaved) }) {
                Text(if (form.id == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
