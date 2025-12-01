package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_history")
data class PurchaseHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,                    // Usuario que realizó la compra
    val totalAmount: Double,                 // Monto total de la compra
    val itemsCount: Int,                     // Cantidad de items comprados
    val pointsEarned: Int,                   // Puntos ganados por esta compra
    val bonusPoints: Int = 0,                // Puntos bonus (por nivel, promociones, etc.)
    val purchaseDate: Long = System.currentTimeMillis(), // Fecha de compra
    val orderNumber: String,                 // Número de orden
    val itemsSummary: String                 // Resumen de items comprados
)

// Clase auxiliar para calcular niveles y beneficios
object LevelUpCalculator {
    
    // Puntos necesarios para cada nivel
    val levelThresholds = mapOf(
        1 to 0,
        2 to 500,
        3 to 1500,
        4 to 3500,
        5 to 7000,
        6 to 12000,
        7 to 20000,
        8 to 35000,
        9 to 60000,
        10 to 100000
    )
    
    // Descuento por nivel (%)
    val levelDiscounts = mapOf(
        1 to 5,
        2 to 8,
        3 to 12,
        4 to 15,
        5 to 18,
        6 to 20,
        7 to 22,
        8 to 25,
        9 to 28,
        10 to 30
    )
    
    // Multiplicador de puntos por nivel
    val levelMultipliers = mapOf(
        1 to 1.0,
        2 to 1.1,
        3 to 1.2,
        4 to 1.3,
        5 to 1.5,
        6 to 1.7,
        7 to 2.0,
        8 to 2.3,
        9 to 2.7,
        10 to 3.0
    )
    
    // Títulos por nivel
    val levelTitles = mapOf(
        1 to "Novato",
        2 to "Aprendiz",
        3 to "Jugador",
        4 to "Experto",
        5 to "Veterano",
        6 to "Maestro",
        7 to "Campeón",
        8 to "Leyenda",
        9 to "Mítico",
        10 to "Inmortal"
    )
    
    // Calcular nivel basado en puntos totales
    fun calculateLevel(totalPoints: Int): Int {
        for (level in 10 downTo 1) {
            if (totalPoints >= (levelThresholds[level] ?: 0)) {
                return level
            }
        }
        return 1
    }
    
    // Calcular puntos para el siguiente nivel
    fun pointsToNextLevel(currentPoints: Int): Int {
        val currentLevel = calculateLevel(currentPoints)
        if (currentLevel >= 10) return 0
        val nextLevelPoints = levelThresholds[currentLevel + 1] ?: 0
        return nextLevelPoints - currentPoints
    }
    
    // Calcular progreso hacia el siguiente nivel (0-100%)
    fun progressToNextLevel(currentPoints: Int): Float {
        val currentLevel = calculateLevel(currentPoints)
        if (currentLevel >= 10) return 100f
        
        val currentLevelPoints = levelThresholds[currentLevel] ?: 0
        val nextLevelPoints = levelThresholds[currentLevel + 1] ?: 0
        
        val pointsInLevel = currentPoints - currentLevelPoints
        val pointsNeeded = nextLevelPoints - currentLevelPoints
        
        return (pointsInLevel.toFloat() / pointsNeeded.toFloat() * 100f).coerceIn(0f, 100f)
    }
    
    // Calcular puntos por una compra
    fun calculatePurchasePoints(purchaseAmount: Double, userLevel: Int): Pair<Int, Int> {
        // 1 punto por cada $1.000 CLP
        val basePoints = (purchaseAmount / 1000).toInt()
        
        // Aplicar multiplicador por nivel
        val multiplier = levelMultipliers[userLevel] ?: 1.0
        val totalPoints = (basePoints * multiplier).toInt()
        
        val bonusPoints = totalPoints - basePoints
        
        return Pair(basePoints, bonusPoints)
    }
    
    // Obtener descuento por nivel
    fun getDiscountForLevel(level: Int): Int {
        return levelDiscounts[level] ?: 5
    }
    
    // Obtener título por nivel
    fun getTitleForLevel(level: Int): String {
        return levelTitles[level] ?: "Novato"
    }
}

