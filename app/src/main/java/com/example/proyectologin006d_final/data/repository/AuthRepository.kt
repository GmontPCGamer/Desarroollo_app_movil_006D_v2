package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.model.Credential

class AuthRepository(
    private val validCredential: Credential = Credential.Admin
) {
    fun login(username: String, password: String): Boolean {
        return username == validCredential.username && password == validCredential.password
    }
}
