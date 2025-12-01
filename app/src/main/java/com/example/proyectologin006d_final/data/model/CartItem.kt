package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: String,           // ID del producto (ej: JM001)
    val productName: String,         // Nombre del producto
    val productPrice: String,        // Precio como string (ej: "$29.990 CLP")
    val priceValue: Double,          // Precio numérico para cálculos
    val quantity: Int = 1,           // Cantidad en el carrito
    val category: String,            // Categoría del producto
    val description: String,         // Descripción del producto
    val manufacturer: String,        // Fabricante
    val username: String,            // Usuario dueño del carrito
    val addedAt: Long = System.currentTimeMillis() // Fecha de agregado
)

