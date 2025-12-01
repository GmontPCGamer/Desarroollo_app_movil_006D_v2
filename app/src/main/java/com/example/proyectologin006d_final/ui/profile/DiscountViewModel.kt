package com.example.proyectologin006d_final.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import com.example.proyectologin006d_final.data.model.Discount
import com.example.proyectologin006d_final.data.repository.DiscountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DiscountUiState(
    val discounts: List<Discount> = emptyList(),
    val activeDiscountCount: Int = 0,
    val isLoading: Boolean = false,
    val message: String? = null,
    val lastScannedDiscount: Discount? = null
)

class DiscountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DiscountRepository

    private val _uiState = MutableStateFlow(DiscountUiState())
    val uiState: StateFlow<DiscountUiState> = _uiState.asStateFlow()

    init {
        val discountDao = ProductoDatabase.getDatabase(application).discountDao()
        repository = DiscountRepository(discountDao)
    }

    // Cargar descuentos de un usuario
    fun loadDiscounts(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Limpiar descuentos expirados
            repository.cleanExpiredDiscounts()

            // Observar descuentos
            repository.getDiscounts(username).collectLatest { discounts ->
                _uiState.value = _uiState.value.copy(
                    discounts = discounts,
                    isLoading = false
                )
            }
        }

        // Observar conteo de descuentos activos
        viewModelScope.launch {
            repository.getActiveDiscountCount(username).collectLatest { count ->
                _uiState.value = _uiState.value.copy(
                    activeDiscountCount = count
                )
            }
        }
    }

    // Agregar descuento desde código QR
    fun addDiscountFromQr(qrContent: String, username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.addDiscountFromQr(qrContent, username)

            result.fold(
                onSuccess = { discount ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "¡Descuento del ${discount.percentage}% agregado!",
                        lastScannedDiscount = discount
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = error.message ?: "Error al procesar el descuento"
                    )
                }
            )
        }
    }

    // Marcar descuento como usado
    fun useDiscount(discountId: Int) {
        viewModelScope.launch {
            try {
                repository.useDiscount(discountId)
                _uiState.value = _uiState.value.copy(
                    message = "Descuento aplicado correctamente"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al usar el descuento: ${e.message}"
                )
            }
        }
    }

    // Eliminar descuento
    fun deleteDiscount(discount: Discount) {
        viewModelScope.launch {
            try {
                repository.deleteDiscount(discount)
                _uiState.value = _uiState.value.copy(
                    message = "Descuento eliminado"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al eliminar: ${e.message}"
                )
            }
        }
    }

    // Limpiar mensaje
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    // Limpiar último descuento escaneado
    fun clearLastScannedDiscount() {
        _uiState.value = _uiState.value.copy(lastScannedDiscount = null)
    }
}

