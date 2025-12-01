package com.example.proyectologin006d_final.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.model.User
import com.example.proyectologin006d_final.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }
    
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }
    
    fun registerUser(onSuccess: (String) -> Unit) {
        val currentState = _uiState.value
        
        // Validaciones
        if (currentState.username.isBlank()) {
            _uiState.value = currentState.copy(error = "El nombre de usuario es requerido")
            return
        }
        
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(error = "El correo electrónico es requerido")
            return
        }
        
        if (!isValidEmail(currentState.email)) {
            _uiState.value = currentState.copy(error = "El formato del correo electrónico no es válido")
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(error = "La contraseña es requerida")
            return
        }
        
        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        
        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(error = "Las contraseñas no coinciden")
            return
        }
        
        _uiState.value = currentState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val user = User(
                    username = currentState.username,
                    email = currentState.email,
                    password = currentState.password,
                    isDuocUser = currentState.email.contains("@duoc.cl", ignoreCase = true)
                )
                
                val result = userRepository.registerUser(user)
                result.fold(
                    onSuccess = { userId ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        )
                        onSuccess(currentState.username)
                    },
                    onFailure = { exception ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al registrar usuario"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
