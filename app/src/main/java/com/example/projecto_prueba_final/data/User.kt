package com.example.projecto_prueba_final.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String // En una app real, esto debería estar hasheado
)