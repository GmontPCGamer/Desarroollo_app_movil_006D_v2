package com.example.proyectologin006d_final.ui.gamification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import com.example.proyectologin006d_final.data.model.LevelUpCalculator
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.PurchaseHistory
import com.example.proyectologin006d_final.data.model.Referral
import com.example.proyectologin006d_final.data.repository.LevelUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LevelUpUiState(
    val userPoints: LevelUpPoints? = null,
    val totalPoints: Int = 0,
    val currentLevel: Int = 1,
    val levelTitle: String = "Novato",
    val levelProgress: Float = 0f,
    val pointsToNextLevel: Int = 0,
    val discount: Int = 5,
    val multiplier: Double = 1.0,
    val referralCode: String = "",
    val referrals: List<Referral> = emptyList(),
    val referralCount: Int = 0,
    val purchaseHistory: List<PurchaseHistory> = emptyList(),
    val totalPurchases: Int = 0,
    val totalSpent: Double = 0.0,
    val rankPosition: Int = 0,
    val topUsers: List<LevelUpPoints> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
)

class LevelUpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LevelUpRepository

    private val _uiState = MutableStateFlow(LevelUpUiState())
    val uiState: StateFlow<LevelUpUiState> = _uiState.asStateFlow()

    init {
        val levelUpDao = ProductoDatabase.getDatabase(application).levelUpDao()
        repository = LevelUpRepository(levelUpDao)
    }

    // Cargar todos los datos del usuario
    fun loadUserData(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Inicializar usuario si no existe
            repository.inicializarUsuarioSiNoExiste(username)
        }

        // Observar puntos del usuario
        viewModelScope.launch {
            repository.obtenerPuntosUsuario(username).collectLatest { points ->
                points?.let {
                    val level = LevelUpCalculator.calculateLevel(it.points)
                    _uiState.value = _uiState.value.copy(
                        userPoints = it,
                        totalPoints = it.points,
                        currentLevel = level,
                        levelTitle = LevelUpCalculator.getTitleForLevel(level),
                        levelProgress = LevelUpCalculator.progressToNextLevel(it.points),
                        pointsToNextLevel = LevelUpCalculator.pointsToNextLevel(it.points),
                        discount = LevelUpCalculator.getDiscountForLevel(level),
                        multiplier = LevelUpCalculator.levelMultipliers[level] ?: 1.0,
                        referralCode = it.referralCode,
                        isLoading = false
                    )
                }
            }
        }

        // Observar referidos
        viewModelScope.launch {
            repository.obtenerReferidos(username).collectLatest { referrals ->
                _uiState.value = _uiState.value.copy(
                    referrals = referrals,
                    referralCount = referrals.size
                )
            }
        }

        // Observar historial de compras
        viewModelScope.launch {
            repository.obtenerUltimasCompras(username, 10).collectLatest { history ->
                _uiState.value = _uiState.value.copy(
                    purchaseHistory = history
                )
            }
        }

        // Observar total gastado
        viewModelScope.launch {
            repository.obtenerTotalGastado(username).collectLatest { total ->
                _uiState.value = _uiState.value.copy(
                    totalSpent = total ?: 0.0
                )
            }
        }

        // Observar cantidad de compras
        viewModelScope.launch {
            repository.contarCompras(username).collectLatest { count ->
                _uiState.value = _uiState.value.copy(
                    totalPurchases = count
                )
            }
        }

        // Observar posición en ranking
        viewModelScope.launch {
            repository.obtenerPosicionRanking(username).collectLatest { position ->
                _uiState.value = _uiState.value.copy(
                    rankPosition = position
                )
            }
        }

        // Observar top usuarios
        viewModelScope.launch {
            repository.obtenerTopUsuarios().collectLatest { topUsers ->
                _uiState.value = _uiState.value.copy(
                    topUsers = topUsers
                )
            }
        }
    }

    // Procesar una nueva compra (llamado desde el checkout)
    fun processPurchase(
        username: String,
        totalAmount: Double,
        itemsCount: Int,
        itemsSummary: String
    ) {
        viewModelScope.launch {
            try {
                val purchase = repository.procesarCompra(
                    username = username,
                    totalAmount = totalAmount,
                    itemsCount = itemsCount,
                    itemsSummary = itemsSummary
                )
                
                val totalPoints = purchase.pointsEarned + purchase.bonusPoints
                _uiState.value = _uiState.value.copy(
                    message = "¡Ganaste $totalPoints puntos LevelUp!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error al procesar puntos: ${e.message}"
                )
            }
        }
    }

    // Copiar código de referido
    fun copyReferralCode(): String {
        return _uiState.value.referralCode
    }

    // Limpiar mensaje
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    // Obtener información de beneficios por nivel
    fun getLevelBenefits(): List<LevelBenefit> {
        return listOf(
            LevelBenefit(1, "Novato", 5, "Descuento del 5% en productos", 0),
            LevelBenefit(2, "Aprendiz", 8, "Descuento del 8% + x1.1 puntos", 500),
            LevelBenefit(3, "Jugador", 12, "Descuento del 12% + x1.2 puntos", 1500),
            LevelBenefit(4, "Experto", 15, "Descuento del 15% + envío gratis", 3500),
            LevelBenefit(5, "Veterano", 18, "Descuento del 18% + x1.5 puntos", 7000),
            LevelBenefit(6, "Maestro", 20, "Descuento del 20% + productos exclusivos", 12000),
            LevelBenefit(7, "Campeón", 22, "Descuento del 22% + x2.0 puntos", 20000),
            LevelBenefit(8, "Leyenda", 25, "Descuento del 25% + acceso anticipado", 35000),
            LevelBenefit(9, "Mítico", 28, "Descuento del 28% + x2.7 puntos", 60000),
            LevelBenefit(10, "Inmortal", 30, "Descuento del 30% + x3.0 puntos + VIP", 100000)
        )
    }
}

data class LevelBenefit(
    val level: Int,
    val title: String,
    val discount: Int,
    val description: String,
    val pointsRequired: Int
)

