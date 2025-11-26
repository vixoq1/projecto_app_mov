package com.example.projecto_prueba_final.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.projecto_prueba_final.data.Producto
import com.example.projecto_prueba_final.ui.ProductoViewModel
import com.example.projecto_prueba_final.ui.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListScreen(
    vm: ProductoViewModel, 
    initialCategory: String, 
    userRole: UserRole,
    onNavigateBack: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val productos by vm.productos.collectAsStateWithLifecycle()
    val formState by vm.form.collectAsStateWithLifecycle()
    val snackbarMessage by vm.snackbarMessage.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val categories = listOf("Todas") + productos.map { it.categoria }.distinct().filter { it.isNotBlank() }
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearSnackbarMessage()
        }
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
        ProductoDialog(
            vm = vm,
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false },
            isCategoryPreselected = selectedCategory != "Todas"
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(selectedCategory, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = {
                        vm.editar(null, if (selectedCategory != "Todas") selectedCategory else "")
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.AddShoppingCart, contentDescription = "Añadir producto", tint = MaterialTheme.colorScheme.onPrimary) }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val filteredProductos = productos.filter {
            (selectedCategory == "Todas" || it.categoria.equals(selectedCategory, ignoreCase = true)) &&
            (searchQuery.isEmpty() || it.nombre.contains(searchQuery, ignoreCase = true))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar...") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryFilter(categories = categories, selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })
                }
            }

            if (filteredProductos.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        Modifier.fillMaxWidth().height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (productos.isEmpty()) "No hay productos." else "No se encontraron productos.")
                    }
                }
            } else {
                items(filteredProductos, key = { it.id }) { p ->
                    ProductoItem(
                        producto = p,
                        onClick = { onProductClick(p.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilter(categories: List<String>, selectedCategory: String, onCategorySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedCategory, color = MaterialTheme.colorScheme.primary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(text = { Text(category) }, onClick = {
                    onCategorySelected(category)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            SubcomposeAsyncImage(
                model = producto.imagenUrl,
                contentDescription = "Imagen de ${producto.nombre}",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentScale = ContentScale.Crop,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
                error = {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                        Text(text = producto.nombre.firstOrNull()?.toString()?.uppercase() ?: "", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
