package com.example.projecto_prueba_final.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
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

@Composable
fun ProductoDialog(vm: ProductoViewModel, onDismiss: () -> Unit, onSaved: () -> Unit, isCategoryPreselected: Boolean) {
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

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { vm.guardar(); onSaved() }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if (formState.id == 0) "Nuevo Producto" else "Editar Producto") },
        text = {
            Column {
                OutlinedTextField(
                    value = formState.nombre,
                    onValueChange = { vm.onFormChange(it, formState.descripcion, formState.precio, formState.categoria, formState.imagenUrl) },
                    label = { Text("Nombre del Producto") }
                )
                OutlinedTextField(
                    value = formState.descripcion,
                    onValueChange = { vm.onFormChange(formState.nombre, it, formState.precio, formState.categoria, formState.imagenUrl) },
                    label = { Text("Descripción") }
                )
                OutlinedTextField(
                    value = formState.precio,
                    onValueChange = { vm.onFormChange(formState.nombre, formState.descripcion, it, formState.categoria, formState.imagenUrl) },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (formState.id != 0 || !isCategoryPreselected) {
                    OutlinedTextField(
                        value = formState.categoria,
                        onValueChange = { vm.onFormChange(formState.nombre, formState.descripcion, formState.precio, it, formState.imagenUrl) },
                        label = { Text("Categoría") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Imagen")
                    }
                    if (formState.imagenUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = Uri.parse(formState.imagenUrl),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    )
}
