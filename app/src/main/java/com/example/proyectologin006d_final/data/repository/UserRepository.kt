package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.UserDao
import com.example.proyectologin006d_final.data.model.User

class UserRepository(private val userDao: UserDao) {
    
    suspend fun registerUser(user: User): Result<Long> {
        return try {
            // Verificar si el usuario ya existe
            val existingUser = userDao.getUserByUsername(user.username)
            if (existingUser != null) {
                return Result.failure(Exception("El nombre de usuario ya existe"))
            }
            
            // Verificar si el email ya existe
            val existingEmail = userDao.getUserByEmail(user.email)
            if (existingEmail != null) {
                return Result.failure(Exception("El correo electrónico ya está registrado"))
            }
            
            val userId = userDao.insertUser(user)
            Result.success(userId)
        } catch (e: Exception) {
            // Manejar errores específicos de Room con más detalle
            val errorMessage = when {
                e.message?.contains("UNIQUE constraint failed") == true -> {
                    if (e.message?.contains("username") == true) {
                        "El nombre de usuario ya existe"
                    } else if (e.message?.contains("email") == true) {
                        "El correo electrónico ya está registrado"
                    } else {
                        "Ya existe un usuario con estos datos"
                    }
                }
                e.message?.contains("no such table") == true -> "Error de base de datos. Reinicia la aplicación"
                e.message?.contains("database") == true -> "Error de conexión a la base de datos"
                e.message?.contains("constraint") == true -> "Datos inválidos. Verifica la información"
                else -> "Error al registrar: ${e.message?.take(50) ?: "Error desconocido"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByCredentials(username, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            // Manejar errores específicos de Room
            val errorMessage = when {
                e.message?.contains("no such table") == true -> "Error de base de datos. Reinicia la aplicación"
                e.message?.contains("database") == true -> "Error de conexión. Inténtalo de nuevo"
                else -> "Error al iniciar sesión. Verifica tus credenciales"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    
    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
    
    // Función de debug para verificar la base de datos
    suspend fun debugDatabase(): String {
        return try {
            val users = userDao.getAllUsers()
            "Base de datos OK. Usuarios registrados: ${users.size}"
        } catch (e: Exception) {
            "Error en base de datos: ${e.message}"
        }
    }
}
