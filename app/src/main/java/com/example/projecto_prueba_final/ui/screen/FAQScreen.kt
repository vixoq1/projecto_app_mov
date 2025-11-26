package com.example.projecto_prueba_final.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FAQItem(val question: String, val answer: String)

private val faqList = listOf(
    FAQItem(
        question = "¿Cómo puedo comprar un producto?",
        answer = "Para comprar un producto, simplemente navega a la categoría deseada, selecciona el producto que te interesa y presiona el botón 'Comprar'. El producto se eliminará de la lista, simulando la compra."
    ),
    FAQItem(
        question = "¿Qué métodos de pago aceptan?",
        answer = "Actualmente, esta es una aplicación de demostración y la función de compra es simulada. No se procesan pagos reales."
    ),
    FAQItem(
        question = "¿Puedo editar un producto después de agregarlo?",
        answer = "Sí. En la lista de productos, cada artículo tiene un ícono de lápiz (Editar). Al presionarlo, se abrirá un formulario con los datos del producto para que puedas modificarlos."
    ),
    FAQItem(
        question = "La imagen que seleccioné no se muestra, ¿qué hago?",
        answer = "Asegúrate de que la imagen seleccionada sea un formato compatible (JPG, PNG, etc.) y que la aplicación tenga los permisos necesarios. Si el problema persiste, la imagen podría estar corrupta."
    ),
    FAQItem(
        question = "¿Cómo puedo buscar un producto específico?",
        answer = "En la parte superior de la lista de productos, encontrarás una barra de búsqueda. Simplemente escribe el nombre del producto que buscas y la lista se filtrará automáticamente."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dudas Frecuentes") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqList) { faq ->
                FAQEntry(faq = faq)
            }
        }
    }
}

@Composable
private fun FAQEntry(faq: FAQItem) {
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
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Cerrar" else "Expandir"
                )
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
