package com.example.projecto_prueba_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecto_prueba_final.data.Producto
import com.example.projecto_prueba_final.data.ProductoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductoFormState(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val categoria: String = "",
    val imagenUrl: String = "",
    val error: String? = null
)

class ProductoViewModel(private val repo: ProductoRepository) : ViewModel() {
    private val _form = MutableStateFlow(ProductoFormState())
    val form: StateFlow<ProductoFormState> = _form.asStateFlow()

    val productos: StateFlow<List<Producto>> = repo.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    
    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onFormChange(nombre: String, descripcion: String, precio: String, categoria: String, imagenUrl: String) {
        _form.update {
            it.copy(nombre = nombre, descripcion = descripcion, precio = precio, categoria = categoria, imagenUrl = imagenUrl)
        }
    }

    fun editar(producto: Producto?, categoriaInicial: String? = null) {
        _form.update {
            it.copy(
                id = producto?.id ?: 0,
                nombre = producto?.nombre ?: "",
                descripcion = producto?.descripcion ?: "",
                precio = producto?.precio?.toString() ?: "",
                categoria = producto?.categoria ?: categoriaInicial ?: "",
                imagenUrl = producto?.imagenUrl ?: ""
            )
        }
    }

    fun guardar() {
        viewModelScope.launch {
            try {
                val producto = Producto(
                    id = form.value.id,
                    nombre = form.value.nombre,
                    descripcion = form.value.descripcion,
                    precio = form.value.precio.toDouble(),
                    categoria = form.value.categoria,
                    imagenUrl = form.value.imagenUrl
                )
                repo.save(producto)
                limpiarForm()
            } catch (e: Exception) {
                _form.update { it.copy(error = "Error al guardar el producto: ${e.message}") }
            }
        }
    }

    fun comprar(producto: Producto) {
        viewModelScope.launch {
            repo.delete(producto)
            _snackbarMessage.value = "¡Gracias por tu compra!"
        }
    }

    fun loadProductById(id: Int) {
        viewModelScope.launch {
            _producto.value = repo.getById(id)
        }
    }

    fun limpiarForm() {
        _form.update { ProductoFormState() }
    }

    fun limpiarError() {
        _form.update { it.copy(error = null) }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun clearProduct() {
        _producto.value = null
    }
}