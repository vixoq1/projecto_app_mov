package com.example.projecto_prueba_final.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallingScreen(phoneNumber: String, onHangUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Llamando a...")
        Text(phoneNumber, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Button(onClick = onHangUp, modifier = Modifier.padding(top = 24.dp)) {
            Text("Colgar")
        }
    }
}