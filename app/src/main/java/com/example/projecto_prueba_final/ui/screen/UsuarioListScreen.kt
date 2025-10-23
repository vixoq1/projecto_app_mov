package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projecto_prueba_final.data.Usuario
import com.example.projecto_prueba_final.ui.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioListScreen(vm: UsuarioViewModel) {
    val usuarios by vm.usuarios.collectAsStateWithLifecycle()
    val formState by vm.form.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Usuario?>(null) }
    var toCall by remember { mutableStateOf<Usuario?>(null) }
    var showDialer by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    if (showDialer) {
        DialerScreen(onDismiss = { showDialer = false })
        return
    }

    if (toCall != null) {
        CallScreen(usuario = toCall!!, onHangUp = { toCall = null })
        return
    }

    if (formState.error != null) {
        AlertDialog(
            onDismissRequest = { vm.limpiarError() },
            confirmButton = { TextButton(onClick = { vm.limpiarError() }) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(formState.error ?: "") }
        )
    }

    if (showDialog) {
        UsuarioDialog(
            vm = vm,
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false }
        )
    }

    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { toDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    toDelete?.let(vm::eliminar)
                    toDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar a '${toDelete?.nombre}'?") }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Contactos") }) },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = {
                    vm.editar(null)
                    showDialog = true
                }) { Text("+") }
                Spacer(modifier = Modifier.height(8.dp))
                FloatingActionButton(onClick = { showDialer = true }) {
                    Icon(Icons.Default.Call, contentDescription = "Llamar")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            val filteredUsuarios = if (searchQuery.isEmpty()) {
                usuarios
            } else {
                usuarios.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                            it.apellido.contains(searchQuery, ignoreCase = true) ||
                            it.telefono.contains(searchQuery, ignoreCase = true) ||
                            it.email.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredUsuarios.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if(searchQuery.isEmpty()) "No hay usuarios. Presiona + para agregar." else "No se encontraron contactos.")
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredUsuarios, key = { it.id }) { u ->
                        UsuarioItem(
                            usuario = u,
                            onEdit = {
                                vm.editar(u)
                                showDialog = true
                            },
                            onDelete = { toDelete = u },
                            onCall = { toCall = u }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactAvatar(usuario: Usuario) {
    val avatarColors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
        Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
        Color(0xFF4FC3F7), Color(0xFF4DD0E1), Color(0xFF4DB6AC),
        Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFD54F),
        Color(0xFFFFB74D), Color(0xFFFF8A65), Color(0xFFA1887F),
        Color(0xFF90A4AE)
    )
    val name = usuario.nombre + usuario.apellido
    val color = avatarColors[kotlin.math.abs(name.hashCode()) % avatarColors.size]

    val initials = "${usuario.nombre.firstOrNull() ?: ""}${usuario.apellido.firstOrNull() ?: ""}".uppercase()
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UsuarioItem(
    usuario: Usuario,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                ContactAvatar(usuario = usuario)
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.clickable { onEdit() }) {
                    Text("${usuario.nombre} ${usuario.apellido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(usuario.email)
                    Text(usuario.telefono)
                    Text("Id: ${usuario.id}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCall) { Text("Llamar") }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                onEdit()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                onDelete()
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
