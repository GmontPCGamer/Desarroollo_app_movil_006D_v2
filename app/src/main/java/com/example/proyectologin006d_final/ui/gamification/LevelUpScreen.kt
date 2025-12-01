package com.example.proyectologin006d_final.ui.gamification

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.data.model.LevelUpCalculator
import com.example.proyectologin006d_final.data.model.PurchaseHistory
import com.example.proyectologin006d_final.data.model.Referral
import com.example.proyectologin006d_final.ui.theme.LevelUpBlack
import com.example.proyectologin006d_final.ui.theme.LevelUpBlue
import com.example.proyectologin006d_final.ui.theme.LevelUpGreen
import com.example.proyectologin006d_final.ui.theme.LevelUpWhite
import com.example.proyectologin006d_final.ui.theme.LevelUpGray
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpScreen(
    navController: NavController,
    username: String,
    levelUpViewModel: LevelUpViewModel = viewModel()
) {
    val uiState by levelUpViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar datos del usuario
    LaunchedEffect(username) {
        levelUpViewModel.loadUserData(username)
    }

    // Mostrar mensajes
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            levelUpViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = LevelUpGreen
                        )
                        Text(
                            "Level-Up Points",
                            color = LevelUpWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = LevelUpWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpBlack
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            LevelUpBlack,
                            Color(0xFF0A0A0A),
                            LevelUpBlack
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con puntos y nivel
            item {
                UserStatsCard(
                    username = username,
                    totalPoints = uiState.totalPoints,
                    currentLevel = uiState.currentLevel,
                    levelTitle = uiState.levelTitle,
                    levelProgress = uiState.levelProgress,
                    pointsToNextLevel = uiState.pointsToNextLevel,
                    discount = uiState.discount,
                    multiplier = uiState.multiplier,
                    rankPosition = uiState.rankPosition
                )
            }

            // Estadísticas de compras
            item {
                PurchaseStatsCard(
                    totalPurchases = uiState.totalPurchases,
                    totalSpent = uiState.totalSpent
                )
            }

            // Código de referido
            item {
                ReferralCodeCard(
                    referralCode = uiState.referralCode,
                    referralCount = uiState.referralCount,
                    context = context
                )
            }

            // Historial de compras
            if (uiState.purchaseHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "📦 Historial de Compras",
                        style = MaterialTheme.typography.titleLarge,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                itemsIndexed(uiState.purchaseHistory.take(5)) { index, purchase ->
                    PurchaseHistoryCard(
                        purchase = purchase,
                        animationDelay = index * 100
                    )
                }
            }

            // Lista de referidos
            if (uiState.referrals.isNotEmpty()) {
                item {
                    Text(
                        text = "👥 Tus Referidos (${uiState.referralCount})",
                        style = MaterialTheme.typography.titleLarge,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.referrals) { referral ->
                    ReferralCard(referral = referral)
                }
            }

            // Beneficios por nivel
            item {
                LevelBenefitsCard(
                    currentLevel = uiState.currentLevel,
                    benefits = levelUpViewModel.getLevelBenefits()
                )
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UserStatsCard(
    username: String,
    totalPoints: Int,
    currentLevel: Int,
    levelTitle: String,
    levelProgress: Float,
    pointsToNextLevel: Int,
    discount: Int,
    multiplier: Double,
    rankPosition: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) levelProgress / 100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Bienvenida y ranking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¡Hola, $username!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
                
                if (rankPosition > 0) {
                    Badge(
                        containerColor = LevelUpBlue,
                        contentColor = LevelUpWhite
                    ) {
                        Text("#$rankPosition")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Puntos y Nivel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Puntos
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(LevelUpGreen, LevelUpBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = LevelUpWhite,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatNumber(totalPoints),
                        style = MaterialTheme.typography.headlineMedium,
                        color = LevelUpGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Puntos",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray
                    )
                }

                // Nivel
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(LevelUpBlue, Color(0xFF9C27B0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentLevel",
                            style = MaterialTheme.typography.headlineLarge,
                            color = LevelUpWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = levelTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nivel",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Barra de progreso
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso al siguiente nivel",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray
                    )
                    Text(
                        text = "${levelProgress.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = LevelUpGreen,
                    trackColor = LevelUpGray.copy(alpha = 0.3f),
                )

                if (pointsToNextLevel > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Faltan ${formatNumber(pointsToNextLevel)} puntos para el siguiente nivel",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Beneficios actuales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BenefitBadge(
                    icon = Icons.Default.Discount,
                    value = "$discount%",
                    label = "Descuento"
                )
                BenefitBadge(
                    icon = Icons.Default.TrendingUp,
                    value = "x$multiplier",
                    label = "Multiplicador"
                )
            }
        }
    }
}

@Composable
fun BenefitBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LevelUpBlue.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LevelUpGreen,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = LevelUpGray
                )
            }
        }
    }
}

@Composable
fun PurchaseStatsCard(
    totalPurchases: Int,
    totalSpent: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = LevelUpBlue,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$totalPurchases",
                    style = MaterialTheme.typography.headlineSmall,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Compras",
                    style = MaterialTheme.typography.bodySmall,
                    color = LevelUpGray
                )
            }

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = LevelUpGray.copy(alpha = 0.3f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = LevelUpGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(totalSpent),
                    style = MaterialTheme.typography.titleLarge,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total gastado",
                    style = MaterialTheme.typography.bodySmall,
                    color = LevelUpGray
                )
            }
        }
    }
}

@Composable
fun ReferralCodeCard(
    referralCode: String,
    referralCount: Int,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LevelUpGreen.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = LevelUpGreen
                    )
                    Text(
                        text = "Tu Código de Referido",
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Badge(
                    containerColor = LevelUpGreen,
                    contentColor = LevelUpBlack
                ) {
                    Text("$referralCount amigos")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Código con botón copiar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = referralCode.ifEmpty { "Generando..." },
                    style = MaterialTheme.typography.titleLarge,
                    color = LevelUpGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (referralCode.isNotEmpty()) {
                    FilledIconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Código de referido", referralCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "¡Código copiado!", Toast.LENGTH_SHORT).show()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = LevelUpGreen,
                            contentColor = LevelUpBlack
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🎁 Comparte y gana 100 puntos por cada amigo que se registre",
                style = MaterialTheme.typography.bodySmall,
                color = LevelUpGray
            )
        }
    }
}

@Composable
fun PurchaseHistoryCard(
    purchase: PurchaseHistory,
    animationDelay: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LevelUpBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = LevelUpBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info de compra
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${purchase.itemsCount} productos",
                        style = MaterialTheme.typography.titleSmall,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = purchase.itemsSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date(purchase.purchaseDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGray.copy(alpha = 0.7f)
                    )
                }

                // Puntos ganados
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatPrice(purchase.totalAmount),
                        style = MaterialTheme.typography.titleSmall,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val totalPoints = purchase.pointsEarned + purchase.bonusPoints
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = LevelUpGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+$totalPoints pts",
                            style = MaterialTheme.typography.bodySmall,
                            color = LevelUpGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (purchase.bonusPoints > 0) {
                        Text(
                            text = "(+${purchase.bonusPoints} bonus)",
                            style = MaterialTheme.typography.bodySmall,
                            color = LevelUpBlue,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralCard(referral: Referral) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LevelUpBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = referral.referredUsername.take(2).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = LevelUpBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = referral.referredUsername,
                    style = MaterialTheme.typography.titleSmall,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = referral.dateCreated.ifEmpty { "Referido" },
                    style = MaterialTheme.typography.bodySmall,
                    color = LevelUpGray
                )
            }

            Badge(
                containerColor = LevelUpGreen.copy(alpha = 0.2f),
                contentColor = LevelUpGreen
            ) {
                Text("+${referral.pointsEarned} pts")
            }
        }
    }
}

@Composable
fun LevelBenefitsCard(
    currentLevel: Int,
    benefits: List<LevelBenefit>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = LevelUpBlue
                )
                Text(
                    text = "Beneficios por Nivel",
                    style = MaterialTheme.typography.titleMedium,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            benefits.forEach { benefit ->
                val isCurrentLevel = benefit.level == currentLevel
                val isUnlocked = benefit.level <= currentLevel

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicador de nivel
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrentLevel -> Brush.linearGradient(
                                        colors = listOf(LevelUpGreen, LevelUpBlue)
                                    )
                                    isUnlocked -> Brush.linearGradient(
                                        colors = listOf(LevelUpGreen.copy(alpha = 0.5f), LevelUpBlue.copy(alpha = 0.5f))
                                    )
                                    else -> Brush.linearGradient(
                                        colors = listOf(LevelUpGray.copy(alpha = 0.3f), LevelUpGray.copy(alpha = 0.3f))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = LevelUpWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "${benefit.level}",
                                style = MaterialTheme.typography.bodySmall,
                                color = LevelUpGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${benefit.title} - ${benefit.discount}% descuento",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUnlocked) LevelUpWhite else LevelUpGray,
                            fontWeight = if (isCurrentLevel) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = benefit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isUnlocked) LevelUpGray else LevelUpGray.copy(alpha = 0.5f)
                        )
                    }

                    if (isCurrentLevel) {
                        Badge(
                            containerColor = LevelUpGreen,
                            contentColor = LevelUpBlack
                        ) {
                            Text("ACTUAL", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// Funciones auxiliares
private fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$${formatter.format(price.toLong())}"
}

@Preview(showBackground = true)
@Composable
fun LevelUpScreenPreview() {
    LevelUpScreen(
        navController = rememberNavController(),
        username = "Gamer123"
    )
}
