package com.example.projecto_prueba_final.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projecto_prueba_final.data.Faq
import com.example.projecto_prueba_final.ui.FaqViewModel
import com.example.projecto_prueba_final.ui.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(vm: FaqViewModel, userRole: UserRole, onNavigateBack: () -> Unit) {
    val faqs by vm.faqs.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        FaqDialog(vm = vm, onDismiss = { showDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dudas Frecuentes") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN || userRole == UserRole.SUPPORT) {
                FloatingActionButton(onClick = { 
                    vm.editar(null)
                    showDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir FAQ")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqs, key = { it.id }) { faq ->
                FAQEntry(
                    faq = faq, 
                    userRole = userRole, 
                    onEdit = {
                        vm.editar(faq)
                        showDialog = true
                    }, 
                    onDelete = { vm.eliminar(faq) }
                )
            }
        }
    }
}

@Composable
private fun FAQEntry(
    faq: Faq, 
    userRole: UserRole, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (userRole == UserRole.ADMIN || userRole == UserRole.SUPPORT) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                } else {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Cerrar" else "Expandir"
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = faq.answer, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun FaqDialog(vm: FaqViewModel, onDismiss: () -> Unit) {
    val formState by vm.form.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { 
            Button(onClick = {
                vm.guardar()
                onDismiss()
            }) { Text("Guardar") } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if(formState.id == 0) "Nueva Pregunta" else "Editar Pregunta") },
        text = {
            Column {
                OutlinedTextField(
                    value = formState.question,
                    onValueChange = { vm.onFormChange(it, formState.answer) },
                    label = { Text("Pregunta") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = formState.answer,
                    onValueChange = { vm.onFormChange(formState.question, it) },
                    label = { Text("Respuesta") },
                    minLines = 3
                )
            }
        }
    )
}