package com.example.projecto_prueba_final

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projecto_prueba_final.data.Producto
import com.example.projecto_prueba_final.data.ProductoRepository
import com.example.projecto_prueba_final.ui.ProductoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepo: ProductoRepository
    private lateinit var viewModel: ProductoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mockk(relaxed = true)
        viewModel = ProductoViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardar un nuevo producto funciona`() = runTest {
        viewModel.onFormChange("Test", "Desc", "10.0", "Cat", "url")
        viewModel.guardar()
        advanceUntilIdle()

        val expectedProduct = Producto(id = 0, nombre = "Test", descripcion = "Desc", precio = 10.0, categoria = "Cat", imagenUrl = "url")
        coVerify { mockRepo.save(expectedProduct) }
    }

    @Test
    fun `editar un producto actualiza el formulario`() {
        val producto = Producto(1, "Original", "OrigDesc", 20.0, "OrigCat", "origUrl")
        viewModel.editar(producto)

        val formState = viewModel.form.value
        assertEquals("Original", formState.nombre)
        assertEquals("20.0", formState.precio)
    }
    
    @Test
    fun `comprar un producto llama a delete y muestra snackbar`() = runTest {
        val producto = Producto(1, "A Comprar", "Desc", 30.0, "Cat", "url")
        viewModel.comprar(producto)
        advanceUntilIdle()
        
        coVerify { mockRepo.delete(producto) }
        assertEquals("¡Gracias por tu compra!", viewModel.snackbarMessage.value)
    }

    @Test
    fun `cargar un producto por id lo actualiza en el estado`() = runTest {
        val producto = Producto(1, "Cargado", "Desc", 40.0, "Cat", "url")
        coEvery { mockRepo.getById(1) } returns producto

        viewModel.loadProductById(1)
        advanceUntilIdle()

        assertEquals(producto, viewModel.producto.value)
    }
}