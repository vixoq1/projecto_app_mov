package com.example.projecto_prueba_final.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FaqDao {
    @Query("SELECT * FROM faqs ORDER BY id DESC")
    fun getAll(): Flow<List<Faq>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(faq: Faq)

    @Delete
    suspend fun delete(faq: Faq)
}