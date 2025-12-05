package com.example.proyectologin006d_final.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import com.example.proyectologin006d_final.data.model.CartItem
import com.example.proyectologin006d_final.data.repository.CartRepository
import com.example.proyectologin006d_final.data.repository.LevelUpRepository
import com.example.proyectologin006d_final.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val subtotal: Double = 0.0,
    val discountPercent: Int = 0,
    val discountAmount: Double = 0.0,
    val totalPrice: Double = 0.0,
    val isDuocUser: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isCheckoutSuccess: Boolean = false,
    val pointsEarned: Int = 0,
    val bonusPoints: Int = 0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CartRepository
    private val levelUpRepository: LevelUpRepository
    private val userRepository: UserRepository
    
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        val database = ProductoDatabase.getDatabase(application)
        repository = CartRepository(database.cartDao())
        levelUpRepository = LevelUpRepository(database.levelUpDao())
        userRepository = UserRepository(database.userDao())
    }

    // Cargar items del carrito para un usuario específico
    fun loadCartItems(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Observar los items del carrito
            repository.getCartItems(username).collectLatest { items ->
                _uiState.value = _uiState.value.copy(
                    cartItems = items,
                    isLoading = false
                )
            }
        }
        
        // Observar el conteo total de items
        viewModelScope.launch {
            repository.getCartItemCount(username).collectLatest { count ->
                _uiState.value = _uiState.value.copy(
                    totalItems = count ?: 0
                )
            }
        }
        
        // Observar el total del precio
        viewModelScope.launch {
            repository.getCartTotal(username).collectLatest { total ->
                val subtotal = total ?: 0.0
                val currentState = _uiState.value
                val discountPercent = if (currentState.isDuocUser) 20 else 0
                val discountAmount = subtotal * discountPercent / 100.0
                val finalTotal = (subtotal - discountAmount).coerceAtLeast(0.0)
                _uiState.value = _uiState.value.copy(
                    subtotal = subtotal,
                    discountPercent = discountPercent,
                    discountAmount = discountAmount,
                    totalPrice = finalTotal
                )
            }
        }

        // Cargar datos del usuario para saber si es DUOC
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByUsername(username)
                val isDuoc = user?.isDuocUser == true
                val currentSubtotal = _uiState.value.subtotal
                val discountPercent = if (isDuoc) 20 else 0
                val discountAmount = currentSubtotal * discountPercent / 100.0
                val finalTotal = (currentSubtotal - discountAmount).coerceAtLeast(0.0)

                _uiState.value = _uiState.value.copy(
                    isDuocUser = isDuoc,
                    discountPercent = discountPercent,
                    discountAmount = discountAmount,
                    totalPrice = if (currentSubtotal > 0) finalTotal else _uiState.value.totalPrice
                )
            } catch (_: Exception) {
                // si falla, simplemente no aplicamos descuento DUOC
            }
        }
    }

    // Agregar producto al carrito
    fun addToCart(
        productId: String,
        productName: String,
        productPrice: String,
        category: String,
        description: String,
        manufacturer: String,
        username: String
    ) {
        viewModelScope.launch {
            try {
                // Convertir precio string a double
                val priceValue = extractPriceValue(productPrice)
                
                val cartItem = CartItem(
                    productId = productId,
                    productName = productName,
                    productPrice = productPrice,
                    priceValue = priceValue,
                    quantity = 1,
                    category = category,
                    description = description,
                    manufacturer = manufacturer,
                    username = username
                )
                
                repository.addToCart(cartItem)
                _uiState.value = _uiState.value.copy(
                    message = "¡$productName agregado al carrito!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al agregar al carrito: ${e.message}"
                )
            }
        }
    }

    // Actualizar cantidad de un item
    fun updateQuantity(itemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            try {
                repository.updateQuantity(itemId, newQuantity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al actualizar cantidad: ${e.message}"
                )
            }
        }
    }

    // Incrementar cantidad
    fun incrementQuantity(item: CartItem) {
        updateQuantity(item.id, item.quantity + 1)
    }

    // Decrementar cantidad
    fun decrementQuantity(item: CartItem) {
        if (item.quantity > 1) {
            updateQuantity(item.id, item.quantity - 1)
        } else {
            removeFromCart(item)
        }
    }

    // Eliminar item del carrito
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(cartItem)
                _uiState.value = _uiState.value.copy(
                    message = "${cartItem.productName} eliminado del carrito"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al eliminar del carrito: ${e.message}"
                )
            }
        }
    }

    // Vaciar carrito
    fun clearCart(username: String) {
        viewModelScope.launch {
            try {
                repository.clearCart(username)
                _uiState.value = _uiState.value.copy(
                    message = "Carrito vaciado exitosamente"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al vaciar el carrito: ${e.message}"
                )
            }
        }
    }

    // Proceso de checkout con puntos LevelUp
    fun checkout(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Obtener los items actuales del carrito antes de vaciar
                val currentItems = _uiState.value.cartItems
                val totalAmount = _uiState.value.totalPrice
                val itemsCount = _uiState.value.totalItems
                
                // Crear resumen de items
                val itemsSummary = currentItems.take(3).joinToString(", ") { it.productName } +
                    if (currentItems.size > 3) " y ${currentItems.size - 3} más" else ""
                
                // Simular procesamiento de pago
                kotlinx.coroutines.delay(1500)
                
                // Procesar puntos LevelUp
                val purchase = levelUpRepository.procesarCompra(
                    username = username,
                    totalAmount = totalAmount,
                    itemsCount = itemsCount,
                    itemsSummary = itemsSummary
                )
                
                val totalPoints = purchase.pointsEarned + purchase.bonusPoints
                
                // Vaciar el carrito después del checkout exitoso
                repository.clearCart(username)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCheckoutSuccess = true,
                    pointsEarned = purchase.pointsEarned,
                    bonusPoints = purchase.bonusPoints,
                    message = "¡Compra realizada! Ganaste $totalPoints puntos LevelUp 🎮"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error en el checkout: ${e.message}"
                )
            }
        }
    }

    // Resetear estado de checkout
    fun resetCheckoutState() {
        _uiState.value = _uiState.value.copy(isCheckoutSuccess = false)
    }

    // Limpiar mensaje
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    // Extraer valor numérico del precio
    private fun extractPriceValue(priceString: String): Double {
        // Formato esperado: "$29.990 CLP" o similar
        return try {
            priceString
                .replace("$", "")
                .replace("CLP", "")
                .replace(".", "")
                .replace(",", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
}

