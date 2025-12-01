package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.LevelUpDao
import com.example.proyectologin006d_final.data.model.LevelUpCalculator
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.PurchaseHistory
import com.example.proyectologin006d_final.data.model.Referral
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class LevelUpRepository(private val levelUpDao: LevelUpDao) {
    
    // ============ GESTIÓN DE USUARIO ============
    
    suspend fun crearUsuarioNuevo(username: String) {
        val existingUser = levelUpDao.obtenerPuntosUsuarioSync(username)
        if (existingUser == null) {
            val puntos = LevelUpPoints(
                username = username,
                points = 50, // Puntos de bienvenida
                level = 1,
                referralCode = generarCodigoReferido(username)
            )
            levelUpDao.insertarPuntos(puntos)
        }
    }
    
    suspend fun inicializarUsuarioSiNoExiste(username: String) {
        val existingUser = levelUpDao.obtenerPuntosUsuarioSync(username)
        if (existingUser == null) {
            crearUsuarioNuevo(username)
        }
    }
    
    fun obtenerPuntosUsuario(username: String): Flow<LevelUpPoints?> {
        return levelUpDao.obtenerPuntosUsuario(username)
    }
    
    // ============ GESTIÓN DE PUNTOS ============
    
    suspend fun agregarPuntos(username: String, puntos: Int) {
        // Asegurar que el usuario existe
        inicializarUsuarioSiNoExiste(username)
        
        // Agregar puntos
        levelUpDao.agregarPuntos(username, puntos)
        
        // Verificar y actualizar nivel
        val userData = levelUpDao.obtenerPuntosUsuarioSync(username)
        userData?.let {
            val nuevoNivel = LevelUpCalculator.calculateLevel(it.points)
            if (nuevoNivel != it.level) {
                levelUpDao.actualizarNivel(username, nuevoNivel)
            }
        }
    }
    
    // ============ GESTIÓN DE COMPRAS ============
    
    suspend fun procesarCompra(
        username: String,
        totalAmount: Double,
        itemsCount: Int,
        itemsSummary: String
    ): PurchaseHistory {
        // Asegurar que el usuario existe
        inicializarUsuarioSiNoExiste(username)
        
        // Obtener datos del usuario para calcular puntos
        val userData = levelUpDao.obtenerPuntosUsuarioSync(username)
        val userLevel = userData?.level ?: 1
        
        // Calcular puntos de la compra
        val (basePoints, bonusPoints) = LevelUpCalculator.calculatePurchasePoints(totalAmount, userLevel)
        val totalPoints = basePoints + bonusPoints
        
        // Crear registro de compra
        val orderNumber = generarNumeroOrden()
        val purchase = PurchaseHistory(
            username = username,
            totalAmount = totalAmount,
            itemsCount = itemsCount,
            pointsEarned = basePoints,
            bonusPoints = bonusPoints,
            orderNumber = orderNumber,
            itemsSummary = itemsSummary
        )
        
        // Guardar compra
        levelUpDao.insertarCompra(purchase)
        
        // Agregar puntos al usuario
        agregarPuntos(username, totalPoints)
        
        // Registrar fecha de compra
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        levelUpDao.registrarCompra(username, dateFormat.format(Date()))
        
        return purchase
    }
    
    fun obtenerHistorialCompras(username: String): Flow<List<PurchaseHistory>> {
        return levelUpDao.obtenerHistorialCompras(username)
    }
    
    fun obtenerUltimasCompras(username: String, limit: Int = 5): Flow<List<PurchaseHistory>> {
        return levelUpDao.obtenerUltimasCompras(username, limit)
    }
    
    fun obtenerTotalGastado(username: String): Flow<Double?> {
        return levelUpDao.obtenerTotalGastado(username)
    }
    
    fun contarCompras(username: String): Flow<Int> {
        return levelUpDao.contarCompras(username)
    }
    
    // ============ GESTIÓN DE REFERIDOS ============
    
    suspend fun procesarReferido(referrerUsername: String, referredUsername: String): Boolean {
        return try {
            // Verificar que el referido no se haya usado antes
            val referral = Referral(
                referrerUsername = referrerUsername,
                referredUsername = referredUsername,
                referralCode = generarCodigoReferido(referrerUsername),
                pointsEarned = 100,
                dateCreated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            levelUpDao.insertarReferido(referral)
            
            // Agregar puntos al que refiere
            agregarPuntos(referrerUsername, 100)
            
            // Agregar puntos bonus al referido (50 puntos extra)
            agregarPuntos(referredUsername, 50)
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun obtenerReferidos(username: String): Flow<List<Referral>> {
        return levelUpDao.obtenerReferidos(username)
    }
    
    fun contarReferidos(username: String): Flow<Int> {
        return levelUpDao.contarReferidos(username)
    }
    
    // ============ RANKING ============
    
    fun obtenerTopUsuarios(): Flow<List<LevelUpPoints>> {
        return levelUpDao.obtenerTopUsuarios()
    }
    
    fun obtenerPosicionRanking(username: String): Flow<Int> {
        return levelUpDao.obtenerPosicionRanking(username)
    }
    
    // ============ UTILIDADES ============
    
    private fun generarCodigoReferido(username: String): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomPart = (1..6).map { chars.random() }.joinToString("")
        return "LVLUP-${username.take(4).uppercase()}-$randomPart"
    }
    
    private fun generarNumeroOrden(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "ORD-$timestamp-$random"
    }
}
