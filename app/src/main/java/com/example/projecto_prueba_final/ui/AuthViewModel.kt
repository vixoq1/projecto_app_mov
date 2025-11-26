package com.example.projecto_prueba_final.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecto_prueba_final.data.User
import com.example.projecto_prueba_final.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UserRole { ANONYMOUS, USER, ADMIN }

// --- Login State ---
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val loginError: String? = null,
    val loginSuccess: Boolean = false
)

// --- Sign Up State ---
data class SignUpFormState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val signUpError: String? = null,
    val signUpSuccess: Boolean = false
)

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState = _loginFormState.asStateFlow()

    private val _signUpFormState = MutableStateFlow(SignUpFormState())
    val signUpFormState = _signUpFormState.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.ANONYMOUS)
    val userRole = _userRole.asStateFlow()

    // --- Login Logic ---
    fun onLoginFormChange(email: String, pass: String) {
        _loginFormState.update { it.copy(email = email, password = pass, loginError = null) }
    }

    fun login() {
        viewModelScope.launch {
            val state = _loginFormState.value

            // Comprobación de credenciales de ADMIN
            if (state.email.equals("ADMIN@duocuc.cl", ignoreCase = true) && state.password == "admin123") {
                _userRole.value = UserRole.ADMIN
                _loginFormState.update { it.copy(loginSuccess = true, loginError = null) }
                return@launch
            }

            val user = userDao.findByEmail(state.email)
            if (user == null) {
                _loginFormState.update { it.copy(loginError = "Usuario no encontrado") }
            } else if (user.password != state.password) {
                _loginFormState.update { it.copy(loginError = "Contraseña incorrecta") }
            } else {
                _userRole.value = UserRole.USER
                _loginFormState.update { it.copy(loginSuccess = true, loginError = null) }
            }
        }
    }

    // --- Sign Up Logic ---
    fun onSignUpFormChange(email: String, pass: String, confirmPass: String) {
        _signUpFormState.update { it.copy(email = email, password = pass, confirmPassword = confirmPass, signUpError = null) }
    }

    fun signUp() {
        viewModelScope.launch {
            val state = _signUpFormState.value
            if (state.password != state.confirmPassword) {
                _signUpFormState.update { it.copy(signUpError = "Las contraseñas no coinciden") }
                return@launch
            }
            if (state.email.equals("ADMIN@duocuc.cl", ignoreCase = true)) {
                _signUpFormState.update { it.copy(signUpError = "Este email está reservado.") }
                return@launch
            }
            val newUser = User(email = state.email, password = state.password)
            val result = userDao.insert(newUser)
            if (result == -1L) {
                _signUpFormState.update { it.copy(signUpError = "El email ya está en uso") }
            } else {
                _signUpFormState.update { it.copy(signUpSuccess = true, signUpError = null) }
            }
        }
    }
    
    // --- Logout Logic ---
    fun logout() {
        _loginFormState.value = LoginFormState()
        _signUpFormState.value = SignUpFormState()
        _userRole.value = UserRole.ANONYMOUS // Resetea el rol al cerrar sesión
    }
}