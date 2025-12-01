package com.example.proyectologin006d_final.ui.login


data class LoginUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDuocUser: Boolean = false // Para aplicar descuento del 20%
)
