package com.example.projecto_prueba_final

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projecto_prueba_final.data.User
import com.example.projecto_prueba_final.data.UserDao
import com.example.projecto_prueba_final.ui.AuthViewModel
import com.example.projecto_prueba_final.ui.UserRole
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockUserDao: UserDao
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUserDao = mockk(relaxed = true)
        viewModel = AuthViewModel(mockUserDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login con credenciales de ADMIN funciona`() = runTest {
        viewModel.onLoginFormChange("ADMIN", "ADMIN")
        viewModel.login()
        advanceUntilIdle() // Asegura que la corutina del login termine
        assertEquals(UserRole.ADMIN, viewModel.userRole.value)
        assertEquals(true, viewModel.loginFormState.value.loginSuccess)
    }

    @Test
    fun `login con credenciales de MODERATOR funciona`() = runTest {
        viewModel.onLoginFormChange("moderador@duocuc.cl", "moderador123")
        viewModel.login()
        advanceUntilIdle()
        assertEquals(UserRole.MODERATOR, viewModel.userRole.value)
    }

    @Test
    fun `login con credenciales de SUPPORT funciona`() = runTest {
        viewModel.onLoginFormChange("support@duocuc.cl", "support123")
        viewModel.login()
        advanceUntilIdle()
        assertEquals(UserRole.SUPPORT, viewModel.userRole.value)
    }

    @Test
    fun `login con credenciales de USER funciona`() = runTest {
        val fakeUser = User(email = "test@test.com", password = "password")
        coEvery { mockUserDao.findByEmail("test@test.com") } returns fakeUser

        viewModel.onLoginFormChange("test@test.com", "password")
        viewModel.login()
        advanceUntilIdle()
        assertEquals(UserRole.USER, viewModel.userRole.value)
    }
    
    @Test
    fun `login con contraseña incorrecta falla`() = runTest {
        val fakeUser = User(email = "test@test.com", password = "password")
        coEvery { mockUserDao.findByEmail("test@test.com") } returns fakeUser

        viewModel.onLoginFormChange("test@test.com", "wrongpassword")
        viewModel.login()
        advanceUntilIdle()
        assertEquals("Contraseña incorrecta", viewModel.loginFormState.value.loginError)
    }

    @Test
    fun `login con usuario no existente falla`() = runTest {
        coEvery { mockUserDao.findByEmail(any()) } returns null

        viewModel.onLoginFormChange("nonexistent@test.com", "password")
        viewModel.login()
        advanceUntilIdle()
        assertEquals("Usuario no encontrado", viewModel.loginFormState.value.loginError)
    }

    @Test
    fun `registro con email reservado falla`() = runTest {
        viewModel.onSignUpFormChange("ADMIN", "password", "password")
        viewModel.signUp()
        advanceUntilIdle()
        assertEquals("Este email está reservado.", viewModel.signUpFormState.value.signUpError)
    }

    @Test
    fun `logout resetea el rol`() = runTest {
        // Login como admin
        viewModel.onLoginFormChange("ADMIN", "ADMIN")
        viewModel.login()
        advanceUntilIdle()
        assertEquals(UserRole.ADMIN, viewModel.userRole.value)

        // Logout
        viewModel.logout()
        advanceUntilIdle()
        assertEquals(UserRole.ANONYMOUS, viewModel.userRole.value)
    }
}