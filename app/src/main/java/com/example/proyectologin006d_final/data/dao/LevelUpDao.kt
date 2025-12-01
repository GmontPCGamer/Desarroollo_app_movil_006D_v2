package com.example.proyectologin006d_final.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.PurchaseHistory
import com.example.proyectologin006d_final.data.model.Referral
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelUpDao {
    
    // ============ PUNTOS DE USUARIO ============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPuntos(puntos: LevelUpPoints)
    
    @Update
    suspend fun actualizarPuntos(puntos: LevelUpPoints)
    
    @Query("SELECT * FROM levelup_points WHERE username = :username")
    fun obtenerPuntosUsuario(username: String): Flow<LevelUpPoints?>
    
    @Query("SELECT * FROM levelup_points WHERE username = :username")
    suspend fun obtenerPuntosUsuarioSync(username: String): LevelUpPoints?
    
    @Query("SELECT * FROM levelup_points")
    fun obtenerTodosLosPuntos(): Flow<List<LevelUpPoints>>
    
    @Query("UPDATE levelup_points SET points = points + :puntos WHERE username = :username")
    suspend fun agregarPuntos(username: String, puntos: Int)
    
    @Query("UPDATE levelup_points SET level = :nivel WHERE username = :username")
    suspend fun actualizarNivel(username: String, nivel: Int)
    
    @Query("UPDATE levelup_points SET totalPurchases = totalPurchases + 1, lastPurchaseDate = :fecha WHERE username = :username")
    suspend fun registrarCompra(username: String, fecha: String)
    
    // ============ REFERIDOS ============
    
    @Insert
    suspend fun insertarReferido(referral: Referral)
    
    @Query("SELECT * FROM referrals WHERE referrerUsername = :username ORDER BY id DESC")
    fun obtenerReferidos(username: String): Flow<List<Referral>>
    
    @Query("SELECT * FROM referrals WHERE referredUsername = :username")
    fun obtenerQuienMeRefirio(username: String): Flow<Referral?>
    
    @Query("SELECT COUNT(*) FROM referrals WHERE referrerUsername = :username")
    fun contarReferidos(username: String): Flow<Int>
    
    // ============ HISTORIAL DE COMPRAS ============
    
    @Insert
    suspend fun insertarCompra(purchase: PurchaseHistory)
    
    @Query("SELECT * FROM purchase_history WHERE username = :username ORDER BY purchaseDate DESC")
    fun obtenerHistorialCompras(username: String): Flow<List<PurchaseHistory>>
    
    @Query("SELECT * FROM purchase_history WHERE username = :username ORDER BY purchaseDate DESC LIMIT :limit")
    fun obtenerUltimasCompras(username: String, limit: Int): Flow<List<PurchaseHistory>>
    
    @Query("SELECT SUM(totalAmount) FROM purchase_history WHERE username = :username")
    fun obtenerTotalGastado(username: String): Flow<Double?>
    
    @Query("SELECT SUM(pointsEarned + bonusPoints) FROM purchase_history WHERE username = :username")
    fun obtenerPuntosGanadosEnCompras(username: String): Flow<Int?>
    
    @Query("SELECT COUNT(*) FROM purchase_history WHERE username = :username")
    fun contarCompras(username: String): Flow<Int>
    
    // ============ RANKING ============
    
    @Query("SELECT * FROM levelup_points ORDER BY points DESC LIMIT 10")
    fun obtenerTopUsuarios(): Flow<List<LevelUpPoints>>
    
    @Query("SELECT COUNT(*) + 1 FROM levelup_points WHERE points > (SELECT points FROM levelup_points WHERE username = :username)")
    fun obtenerPosicionRanking(username: String): Flow<Int>
}
