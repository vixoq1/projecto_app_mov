package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(onDismiss: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var isCalling by remember { mutableStateOf(false) }
    val dialPadButtons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "*", "0", "#"
    )

    if (isCalling) {
        CallingScreen(phoneNumber = phoneNumber, onHangUp = { isCalling = false })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marcar número") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = phoneNumber, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                items(dialPadButtons.size) { index ->
                    val buttonText = dialPadButtons[index]
                    Button(
                        onClick = { phoneNumber += buttonText },
                        modifier = Modifier.padding(4.dp).aspectRatio(1f)
                    ) {
                        Text(text = buttonText, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            Button(onClick = { if (phoneNumber.isNotEmpty()) isCalling = true }, modifier = Modifier.padding(top = 24.dp)) {
                Text("Llamar")
            }
        }
    }
}