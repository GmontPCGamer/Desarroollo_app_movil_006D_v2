package com.example.proyectologin006d_final.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectologin006d_final.data.model.Discount
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscountDao {

    // Insertar un nuevo descuento
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscount(discount: Discount)

    // Actualizar un descuento
    @Update
    suspend fun updateDiscount(discount: Discount)

    // Eliminar un descuento
    @Delete
    suspend fun deleteDiscount(discount: Discount)

    // Obtener todos los descuentos de un usuario
    @Query("SELECT * FROM discounts WHERE username = :username ORDER BY scannedAt DESC")
    fun getDiscountsByUser(username: String): Flow<List<Discount>>

    // Obtener descuentos activos (no usados) de un usuario
    @Query("SELECT * FROM discounts WHERE username = :username AND isUsed = 0 ORDER BY scannedAt DESC")
    fun getActiveDiscountsByUser(username: String): Flow<List<Discount>>

    // Verificar si un código ya existe para un usuario
    @Query("SELECT * FROM discounts WHERE code = :code AND username = :username LIMIT 1")
    suspend fun getDiscountByCode(code: String, username: String): Discount?

    // Marcar un descuento como usado
    @Query("UPDATE discounts SET isUsed = 1 WHERE id = :discountId")
    suspend fun markAsUsed(discountId: Int)

    // Contar descuentos activos de un usuario
    @Query("SELECT COUNT(*) FROM discounts WHERE username = :username AND isUsed = 0")
    fun getActiveDiscountCount(username: String): Flow<Int>

    // Eliminar descuentos expirados
    @Query("DELETE FROM discounts WHERE expiresAt IS NOT NULL AND expiresAt < :currentTime")
    suspend fun deleteExpiredDiscounts(currentTime: Long)

    // Eliminar un descuento por ID
    @Query("DELETE FROM discounts WHERE id = :discountId")
    suspend fun deleteDiscountById(discountId: Int)
}

