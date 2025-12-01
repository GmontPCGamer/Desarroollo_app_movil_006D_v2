package com.example.proyectologin006d_final.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levelup_points")
data class LevelUpPoints(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val points: Int = 0,
    val level: Int = 1,
    val referralCode: String = "",
    val referredBy: String? = null, // Código de referido usado
    val totalPurchases: Int = 0,
    val lastPurchaseDate: String = ""
)

@Entity(tableName = "referrals")
data class Referral(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val referrerUsername: String, // Quien refiere
    val referredUsername: String,  // Quien es referido
    val referralCode: String,
    val pointsEarned: Int = 100, // Puntos por referido
    val dateCreated: String = ""
)


