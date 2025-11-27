package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.projecto_prueba_final.ui.ProductoViewModel
import com.example.projecto_prueba_final.ui.UserRole

data class Category(val name: String, val icon: ImageVector)

private val predefinedCategories = listOf(
    Category("Consolas", Icons.Default.SportsEsports),
    Category("Portátiles", Icons.Default.Laptop),
    Category("Móviles", Icons.Default.PhoneAndroid),
    Category("PC", Icons.Default.Computer),
    Category("Accesorios", Icons.Default.Headphones),
    Category("Videojuegos", Icons.Default.VideogameAsset)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    vm: ProductoViewModel, 
    userRole: UserRole, // <-- Se necesita el rol para el debug
    onCategorySelected: (String) -> Unit, 
    onNavigateToFAQ: () -> Unit, 
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido a Games Top") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToFAQ) {
                Icon(Icons.Default.HelpOutline, contentDescription = "Dudas Frecuentes")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Explora nuestras categorías", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            
            // --- TEXTO DE DEPURACIÓN ---
            Spacer(modifier = Modifier.height(12.dp))
            Text("Rol Actual: ${userRole.name}", color = Color.Red, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(predefinedCategories) { category ->
                    CategoryCard(category = category, onClick = { onCategorySelected(category.name) })
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(category.icon, contentDescription = category.name, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}