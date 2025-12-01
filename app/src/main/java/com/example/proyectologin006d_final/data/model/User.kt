package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        androidx.room.Index(value = ["username"], unique = true),
        androidx.room.Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    val isDuocUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
