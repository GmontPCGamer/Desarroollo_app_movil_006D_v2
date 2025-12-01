package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.DiscountDao
import com.example.proyectologin006d_final.data.model.Discount
import kotlinx.coroutines.flow.Flow

class DiscountRepository(private val discountDao: DiscountDao) {

    // Obtener todos los descuentos de un usuario
    fun getDiscounts(username: String): Flow<List<Discount>> {
        return discountDao.getDiscountsByUser(username)
    }

    // Obtener descuentos activos
    fun getActiveDiscounts(username: String): Flow<List<Discount>> {
        return discountDao.getActiveDiscountsByUser(username)
    }

    // Agregar un nuevo descuento desde un código QR
    suspend fun addDiscountFromQr(qrContent: String, username: String): Result<Discount> {
        return try {
            // Verificar si el código ya existe
            val existingDiscount = discountDao.getDiscountByCode(qrContent, username)
            if (existingDiscount != null) {
                return Result.failure(Exception("Este código de descuento ya fue escaneado"))
            }

            // Parsear el código QR y crear el descuento
            val discount = parseQrToDiscount(qrContent, username)
            discountDao.insertDiscount(discount)
            Result.success(discount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Parsear el contenido del QR a un objeto Discount
    private fun parseQrToDiscount(qrContent: String, username: String): Discount {
        // Formato esperado del QR: "DESCUENTO:PORCENTAJE:DESCRIPCION"
        // Ejemplo: "LEVELUP:20:20% descuento en consolas"
        // O simplemente el código con porcentaje aleatorio
        
        val parts = qrContent.split(":")
        
        return when {
            parts.size >= 3 && parts[0].uppercase() == "LEVELUP" -> {
                // Formato estructurado
                val percentage = parts[1].toIntOrNull() ?: 10
                val description = parts.drop(2).joinToString(":")
                Discount(
                    code = qrContent,
                    description = description.ifEmpty { "Descuento Level-Up Gamer" },
                    percentage = percentage.coerceIn(1, 100),
                    username = username,
                    expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // 30 días
                )
            }
            qrContent.contains("%") || qrContent.lowercase().contains("descuento") -> {
                // Intenta extraer el porcentaje del texto
                val percentageMatch = Regex("(\\d+)%?").find(qrContent)
                val percentage = percentageMatch?.groupValues?.get(1)?.toIntOrNull() ?: 10
                Discount(
                    code = qrContent,
                    description = qrContent,
                    percentage = percentage.coerceIn(1, 100),
                    username = username,
                    expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
                )
            }
            else -> {
                // Código genérico - asignar porcentaje aleatorio entre 5 y 25
                val randomPercentage = (5..25).random()
                Discount(
                    code = qrContent,
                    description = "Descuento especial: $qrContent",
                    percentage = randomPercentage,
                    username = username,
                    expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
                )
            }
        }
    }

    // Marcar descuento como usado
    suspend fun useDiscount(discountId: Int) {
        discountDao.markAsUsed(discountId)
    }

    // Eliminar descuento
    suspend fun deleteDiscount(discount: Discount) {
        discountDao.deleteDiscount(discount)
    }

    // Eliminar descuento por ID
    suspend fun deleteDiscountById(discountId: Int) {
        discountDao.deleteDiscountById(discountId)
    }

    // Obtener conteo de descuentos activos
    fun getActiveDiscountCount(username: String): Flow<Int> {
        return discountDao.getActiveDiscountCount(username)
    }

    // Limpiar descuentos expirados
    suspend fun cleanExpiredDiscounts() {
        discountDao.deleteExpiredDiscounts(System.currentTimeMillis())
    }
}

