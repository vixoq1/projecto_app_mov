package com.example.projecto_prueba_final.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(entities = [Producto::class, User::class, Faq::class], version = 5, exportSchema = false) // VERSIÓN INCREMENTADA
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun userDao(): UserDao
    abstract fun faqDao(): FaqDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productos.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-poblar la base de datos con FAQs genéricas usando SQL directo
                        Executors.newSingleThreadExecutor().execute {
                            db.execSQL("INSERT INTO faqs (question, answer) VALUES ('¿Cómo puedo comprar un producto?', 'Para comprar un producto, simplemente navega a la categoría deseada, selecciona el producto que te interesa y presiona el botón \'COMPRAR AHORA\'.')")
                            db.execSQL("INSERT INTO faqs (question, answer) VALUES ('¿Qué métodos de pago aceptan?', 'Actualmente, esta es una aplicación de demostración y la función de compra es simulada. No se procesan pagos reales.')")
                            db.execSQL("INSERT INTO faqs (question, answer) VALUES ('¿Puedo editar un producto después de agregarlo?', 'Sí, pero solo si eres Administrador o Moderador. En la pantalla de detalle del producto, encontrarás un ícono de lápiz para editar.')")
                            db.execSQL("INSERT INTO faqs (question, answer) VALUES ('La imagen que seleccioné no se muestra, ¿qué hago?', 'Asegúrate de que la imagen seleccionada sea un formato compatible (JPG, PNG, etc.) y que la aplicación tenga los permisos necesarios para acceder al almacenamiento.')")
                            db.execSQL("INSERT INTO faqs (question, answer) VALUES ('¿Cómo puedo buscar un producto específico?', 'En la parte superior de la lista de productos, encontrarás una barra de búsqueda. Simplemente escribe el nombre del producto que buscas y la lista se filtrará automáticamente.')")
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}