package com.example.projecto_prueba_final.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faqs")
data class Faq(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val answer: String
)