package com.example.proyectologin006d_final.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun updateImageUri(uri: Uri?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                imageUri = uri,
                isLoading = false,
                error = null
            )
        }
    }
    
    fun setLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = isLoading)
        }
    }
    
    fun setError(error: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = error)
        }
    }
    
    fun clearError() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
}
