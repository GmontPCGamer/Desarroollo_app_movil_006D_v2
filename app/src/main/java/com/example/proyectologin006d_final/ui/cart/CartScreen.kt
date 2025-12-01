package com.example.proyectologin006d_final.ui.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.proyectologin006d_final.data.model.CartItem
import com.example.proyectologin006d_final.ui.theme.LevelUpBlack
import com.example.proyectologin006d_final.ui.theme.LevelUpBlue
import com.example.proyectologin006d_final.ui.theme.LevelUpGreen
import com.example.proyectologin006d_final.ui.theme.LevelUpWhite
import com.example.proyectologin006d_final.ui.theme.LevelUpGray
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    username: String,
    cartViewModel: CartViewModel = viewModel()
) {
    val uiState by cartViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar items del carrito al iniciar
    LaunchedEffect(username) {
        cartViewModel.loadCartItems(username)
    }

    // Mostrar mensajes
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            cartViewModel.clearMessage()
        }
    }

    // Manejar checkout exitoso
    LaunchedEffect(uiState.isCheckoutSuccess) {
        if (uiState.isCheckoutSuccess) {
            snackbarHostState.showSnackbar(
                message = "¡Compra realizada con éxito!",
                duration = SnackbarDuration.Long
            )
            cartViewModel.resetCheckoutState()
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
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = LevelUpGreen
                        )
                        Text(
                            "Mi Carrito",
                            color = LevelUpWhite,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.totalItems > 0) {
                            Badge(
                                containerColor = LevelUpGreen,
                                contentColor = LevelUpBlack
                            ) {
                                Text("${uiState.totalItems}")
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = LevelUpWhite
                        )
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = { cartViewModel.clearCart(username) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveShoppingCart,
                                contentDescription = "Vaciar carrito",
                                tint = Color.Red.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpBlack
                )
            )
        }
    ) { innerPadding ->
        Box(
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
                )
        ) {
            if (uiState.isLoading) {
                // Estado de carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = LevelUpGreen,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Procesando...",
                            color = LevelUpWhite,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (uiState.cartItems.isEmpty()) {
                // Carrito vacío
                EmptyCartContent(
                    onContinueShopping = { navController.popBackStack() }
                )
            } else {
                // Lista de items del carrito
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.cartItems,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            CartItemCard(
                                cartItem = item,
                                animationDelay = index * 50,
                                onIncrement = { cartViewModel.incrementQuantity(item) },
                                onDecrement = { cartViewModel.decrementQuantity(item) },
                                onRemove = { cartViewModel.removeFromCart(item) }
                            )
                        }
                    }

                    // Resumen y botón de checkout
                    CartSummary(
                        totalItems = uiState.totalItems,
                        totalPrice = uiState.totalPrice,
                        onCheckout = { cartViewModel.checkout(username) },
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartContent(
    onContinueShopping: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0.5f,
        animationSpec = tween(durationMillis = 500),
        label = "empty-cart-scale"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono animado
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = LevelUpBlue.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineMedium,
            color = LevelUpWhite,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¡Explora nuestro catálogo y encuentra productos increíbles!",
            style = MaterialTheme.typography.bodyMedium,
            color = LevelUpGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinueShopping,
            colors = ButtonDefaults.buttonColors(
                containerColor = LevelUpGreen,
                contentColor = LevelUpBlack
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                text = "Ir al Catálogo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    animationDelay: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
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
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Información del producto
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = cartItem.productName,
                            style = MaterialTheme.typography.titleMedium,
                            color = LevelUpWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = cartItem.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = LevelUpGreen,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = cartItem.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = LevelUpGray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (cartItem.manufacturer.isNotEmpty()) {
                            Text(
                                text = "Por: ${cartItem.manufacturer}",
                                style = MaterialTheme.typography.bodySmall,
                                color = LevelUpGray.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Precio y controles de cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Precio
                    Column {
                        Text(
                            text = cartItem.productPrice,
                            style = MaterialTheme.typography.titleLarge,
                            color = LevelUpBlue,
                            fontWeight = FontWeight.Bold
                        )
                        if (cartItem.quantity > 1) {
                            val subtotal = cartItem.priceValue * cartItem.quantity
                            Text(
                                text = "Subtotal: ${formatPrice(subtotal)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = LevelUpGreen
                            )
                        }
                    }

                    // Controles de cantidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón decrementar
                        FilledIconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = LevelUpBlue.copy(alpha = 0.3f),
                                contentColor = LevelUpWhite
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Disminuir cantidad",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Cantidad
                        Text(
                            text = "${cartItem.quantity}",
                            style = MaterialTheme.typography.titleMedium,
                            color = LevelUpWhite,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.widthIn(min = 40.dp),
                            textAlign = TextAlign.Center
                        )

                        // Botón incrementar
                        FilledIconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = LevelUpGreen,
                                contentColor = LevelUpBlack
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar cantidad",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    totalItems: Int,
    totalPrice: Double,
    onCheckout: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Línea decorativa
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LevelUpGray.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total de productos:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LevelUpGray
                )
                Text(
                    text = "$totalItems ${if (totalItems == 1) "item" else "items"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total a pagar:",
                    style = MaterialTheme.typography.titleMedium,
                    color = LevelUpWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatPrice(totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = LevelUpGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de checkout
            Button(
                onClick = onCheckout,
                enabled = !isLoading && totalItems > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LevelUpGreen,
                    contentColor = LevelUpBlack,
                    disabledContainerColor = LevelUpGray.copy(alpha = 0.3f),
                    disabledContentColor = LevelUpGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = LevelUpBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCartCheckout,
                            contentDescription = null
                        )
                        Text(
                            text = "Proceder al Pago",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Función para formatear precios
private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$${formatter.format(price.toLong())} CLP"
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen(
        navController = rememberNavController(),
        username = "Gamer123"
    )
}

