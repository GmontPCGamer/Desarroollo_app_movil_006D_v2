package com.example.proyectologin006d_final.ui.profile

import android.net.Uri

data class ProfileUiState(
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
