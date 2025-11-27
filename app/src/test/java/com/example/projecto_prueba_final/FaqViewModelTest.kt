package com.example.projecto_prueba_final

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projecto_prueba_final.data.Faq
import com.example.projecto_prueba_final.data.FaqDao
import com.example.projecto_prueba_final.ui.FaqViewModel
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
class FaqViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockDao: FaqDao
    private lateinit var viewModel: FaqViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockDao = mockk(relaxed = true)
        viewModel = FaqViewModel(mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardar una nueva faq funciona`() = runTest {
        // Arrange
        viewModel.onFormChange("Pregunta Nueva", "Respuesta Nueva")
        
        // Act
        viewModel.guardar()
        advanceUntilIdle() // <-- LA CLAVE: Espera a que la corutina termine

        // Assert
        val expectedFaq = Faq(id = 0, question = "Pregunta Nueva", answer = "Respuesta Nueva")
        coVerify { mockDao.save(expectedFaq) }
    }

    @Test
    fun `editar una faq actualiza el formulario`() {
        // Arrange
        val faq = Faq(1, "Pregunta Original", "Respuesta Original")
        
        // Act
        viewModel.editar(faq)
        
        // Assert
        val formState = viewModel.form.value
        assertEquals("Pregunta Original", formState.question)
        assertEquals("Respuesta Original", formState.answer)
    }

    @Test
    fun `eliminar una faq llama a delete`() = runTest {
        // Arrange
        val faq = Faq(1, "A Eliminar", "Respuesta")
        
        // Act
        viewModel.eliminar(faq)
        advanceUntilIdle() // <-- LA CLAVE: Espera a que la corutina termine
        
        // Assert
        coVerify { mockDao.delete(faq) }
    }
}