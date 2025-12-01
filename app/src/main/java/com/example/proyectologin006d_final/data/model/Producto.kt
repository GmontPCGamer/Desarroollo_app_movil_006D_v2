package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val codigo: String, // Código del producto (JM001, AC001, etc.)
    val categoria: String, // Categoría del producto
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val cantidad: String,
    val direccion: String,
    val fabricante: String = "", // Para autenticidad y calidad
    val esPersonalizado: Boolean = false // Para poleras/polerones personalizados
)