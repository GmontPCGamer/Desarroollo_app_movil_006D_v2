package com.example.proyectologin006d_final.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectologin006d_final.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Insertar un item al carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    // Actualizar un item del carrito (ej: cambiar cantidad)
    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    // Eliminar un item del carrito
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    // Obtener todos los items del carrito de un usuario
    @Query("SELECT * FROM cart_items WHERE username = :username ORDER BY addedAt DESC")
    fun getCartItemsByUser(username: String): Flow<List<CartItem>>

    // Obtener un item específico por productId y username
    @Query("SELECT * FROM cart_items WHERE productId = :productId AND username = :username LIMIT 1")
    suspend fun getCartItemByProductId(productId: String, username: String): CartItem?

    // Eliminar todos los items del carrito de un usuario
    @Query("DELETE FROM cart_items WHERE username = :username")
    suspend fun clearCartByUser(username: String)

    // Obtener el total de items en el carrito de un usuario
    @Query("SELECT SUM(quantity) FROM cart_items WHERE username = :username")
    fun getCartItemCount(username: String): Flow<Int?>

    // Obtener el total del carrito de un usuario
    @Query("SELECT SUM(priceValue * quantity) FROM cart_items WHERE username = :username")
    fun getCartTotal(username: String): Flow<Double?>

    // Actualizar la cantidad de un item específico
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: Int, quantity: Int)

    // Eliminar un item por su ID
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItemById(itemId: Int)
}

