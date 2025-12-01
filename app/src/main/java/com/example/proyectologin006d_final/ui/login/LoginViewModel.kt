package com.example.proyectologin006d_final.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyectologin006d_final.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, error = null)
    }

    fun onEmailChange(value: String) {
        val isDuocUser = value.endsWith("@duocuc.cl") || value.endsWith("@duoc.cl")
        uiState = uiState.copy(email = value, isDuocUser = isDuocUser, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun submit(onSuccess: (String) -> Unit) {
        uiState = uiState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val result = userRepository.loginUser(uiState.username.trim(), uiState.password)
                result.fold(
                    onSuccess = { user ->
                        uiState = uiState.copy(isLoading = false)
                        onSuccess(user.username)
                    },
                    onFailure = { exception ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Credenciales inválidas"
                        )
                    }
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }
    
    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}
