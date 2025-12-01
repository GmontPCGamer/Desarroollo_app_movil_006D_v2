package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.CartDao
import com.example.proyectologin006d_final.data.model.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    // Obtener todos los items del carrito de un usuario
    fun getCartItems(username: String): Flow<List<CartItem>> {
        return cartDao.getCartItemsByUser(username)
    }

    // Agregar un producto al carrito
    suspend fun addToCart(cartItem: CartItem) {
        // Verificar si el producto ya existe en el carrito
        val existingItem = cartDao.getCartItemByProductId(cartItem.productId, cartItem.username)
        
        if (existingItem != null) {
            // Si existe, actualizar la cantidad
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + cartItem.quantity
            )
            cartDao.updateCartItem(updatedItem)
        } else {
            // Si no existe, insertar nuevo item
            cartDao.insertCartItem(cartItem)
        }
    }

    // Actualizar cantidad de un item
    suspend fun updateQuantity(itemId: Int, quantity: Int) {
        if (quantity > 0) {
            cartDao.updateQuantity(itemId, quantity)
        } else {
            // Si la cantidad es 0 o menos, eliminar el item
            cartDao.deleteCartItemById(itemId)
        }
    }

    // Eliminar un item del carrito
    suspend fun removeFromCart(cartItem: CartItem) {
        cartDao.deleteCartItem(cartItem)
    }

    // Eliminar item por ID
    suspend fun removeFromCartById(itemId: Int) {
        cartDao.deleteCartItemById(itemId)
    }

    // Vaciar el carrito de un usuario
    suspend fun clearCart(username: String) {
        cartDao.clearCartByUser(username)
    }

    // Obtener el total de items en el carrito
    fun getCartItemCount(username: String): Flow<Int?> {
        return cartDao.getCartItemCount(username)
    }

    // Obtener el total del carrito
    fun getCartTotal(username: String): Flow<Double?> {
        return cartDao.getCartTotal(username)
    }

    // Verificar si un producto ya está en el carrito
    suspend fun isProductInCart(productId: String, username: String): Boolean {
        return cartDao.getCartItemByProductId(productId, username) != null
    }
}

