package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.projecto_prueba_final.data.Producto
import com.example.projecto_prueba_final.ui.ProductoViewModel
import com.example.projecto_prueba_final.ui.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetailScreen(
    vm: ProductoViewModel,
    productoId: Int,
    userRole: UserRole,
    onNavigateBack: () -> Unit,
    onBuy: (Producto) -> Unit
) {
    LaunchedEffect(productoId) {
        vm.loadProductById(productoId)
    }

    val producto by vm.producto.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        ProductoDialog(
            vm = vm,
            onDismiss = { showEditDialog = false },
            onSaved = { 
                showEditDialog = false 
                // Recargar el producto para ver los cambios
                vm.loadProductById(productoId)
            },
            isCategoryPreselected = true // En edición, la categoría no se puede cambiar
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Cargando...") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = {
                    if (userRole == UserRole.ADMIN && producto != null) {
                        IconButton(onClick = { 
                            vm.editar(producto)
                            showEditDialog = true 
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Producto")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val isUser = userRole == UserRole.USER
            val isAdmin = userRole == UserRole.ADMIN

            if (isUser || isAdmin) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${producto?.precio ?: "--"}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = { producto?.let(onBuy) }, 
                            modifier = Modifier.height(48.dp), 
                            enabled = producto != null,
                            colors = if (isAdmin) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                        ) {
                            Text(
                                text = if (isAdmin) "ELIMINAR" else "COMPRAR AHORA", 
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        producto?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SubcomposeAsyncImage(
                    model = p.imagenUrl,
                    contentDescription = "Imagen de ${p.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = { CircularProgressIndicator() }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(p.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                AssistChip(onClick = {}, label = { Text(p.categoria) })
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Descripción", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(p.descripcion, style = MaterialTheme.typography.bodyLarge)
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}