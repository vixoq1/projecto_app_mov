package com.example.projecto_prueba_final.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.projecto_prueba_final.ui.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormScreen(vm: ProductoViewModel, onNavigateBack: () -> Unit, onSave: () -> Unit) {
    val formState by vm.form.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
                vm.onFormChange(formState.nombre, formState.descripcion, formState.precio, formState.categoria, it.toString())
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (formState.id == 0) "Nuevo Producto" else "Editar Producto") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.guardar(); onSave() }) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = formState.nombre,
                onValueChange = { vm.onFormChange(it, formState.descripcion, formState.precio, formState.categoria, formState.imagenUrl) },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = formState.descripcion,
                onValueChange = { vm.onFormChange(formState.nombre, it, formState.precio, formState.categoria, formState.imagenUrl) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = formState.precio,
                onValueChange = { vm.onFormChange(formState.nombre, formState.descripcion, it, formState.categoria, formState.imagenUrl) },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            val isEditing = formState.id != 0
            // Si estamos editando, mostramos la categoría pero deshabilitada.
            if (isEditing) {
                OutlinedTextField(
                    value = formState.categoria,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Si estamos creando, solo mostramos el campo si la categoría no está pre-seleccionada.
                if (formState.categoria.isBlank()) {
                    OutlinedTextField(
                        value = formState.categoria,
                        onValueChange = { vm.onFormChange(formState.nombre, formState.descripcion, formState.precio, it, formState.imagenUrl) },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Seleccionar Imagen")
                }
                if (formState.imagenUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = Uri.parse(formState.imagenUrl),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}