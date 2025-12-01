package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.LevelUpDao
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.Referral
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LevelUpRepository(private val levelUpDao: LevelUpDao) {
    
    suspend fun crearUsuarioNuevo(username: String) {
        val puntos = LevelUpPoints(
            username = username,
            points = 50, // Puntos de bienvenida
            level = 1,
            referralCode = generarCodigoReferido(username)
        )
        levelUpDao.insertarPuntos(puntos)
    }
    
    suspend fun agregarPuntos(username: String, puntos: Int) {
        val puntosActuales = levelUpDao.obtenerPuntosUsuario(username)
        // En una implementación real, aquí se actualizarían los puntos
    }
    
    suspend fun procesarReferido(referrerUsername: String, referredUsername: String) {
        val referral = Referral(
            referrerUsername = referrerUsername,
            referredUsername = referredUsername,
            referralCode = generarCodigoReferido(referrerUsername),
            pointsEarned = 100
        )
        levelUpDao.insertarReferido(referral)
    }
    
    fun obtenerPuntosUsuario(username: String): Flow<LevelUpPoints?> {
        return levelUpDao.obtenerPuntosUsuario(username)
    }
    
    fun obtenerReferidos(username: String): Flow<List<Referral>> {
        return levelUpDao.obtenerReferidos(username)
    }
    
    private fun generarCodigoReferido(username: String): String {
        return "REF-${username.uppercase()}-${UUID.randomUUID().toString().substring(0, 6)}"
    }
}


