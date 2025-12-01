package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discounts")
data class Discount(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,                    // Código del descuento (del QR)
    val description: String,             // Descripción del descuento
    val percentage: Int,                 // Porcentaje de descuento (ej: 10, 20, 50)
    val username: String,                // Usuario dueño del descuento
    val isUsed: Boolean = false,         // Si ya fue usado
    val scannedAt: Long = System.currentTimeMillis(), // Fecha de escaneo
    val expiresAt: Long? = null          // Fecha de expiración (opcional)
)

