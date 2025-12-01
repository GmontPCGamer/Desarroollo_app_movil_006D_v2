package com.example.proyectologin006d_final.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.Referral
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelUpDao {
    
    @Insert
    suspend fun insertarPuntos(puntos: LevelUpPoints)
    
    @Update
    suspend fun actualizarPuntos(puntos: LevelUpPoints)
    
    @Query("SELECT * FROM levelup_points WHERE username = :username")
    fun obtenerPuntosUsuario(username: String): Flow<LevelUpPoints?>
    
    @Query("SELECT * FROM levelup_points")
    fun obtenerTodosLosPuntos(): Flow<List<LevelUpPoints>>
    
    @Insert
    suspend fun insertarReferido(referral: Referral)
    
    @Query("SELECT * FROM referrals WHERE referrerUsername = :username")
    fun obtenerReferidos(username: String): Flow<List<Referral>>
    
    @Query("SELECT * FROM referrals WHERE referredUsername = :username")
    fun obtenerQuienMeRefirio(username: String): Flow<Referral?>
}


