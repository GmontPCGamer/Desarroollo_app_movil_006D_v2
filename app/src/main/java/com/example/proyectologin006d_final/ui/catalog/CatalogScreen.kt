package com.example.proyectologin006d_final.ui.catalog

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.ui.theme.LevelUpBlack
import com.example.proyectologin006d_final.ui.theme.LevelUpBlue
import com.example.proyectologin006d_final.ui.theme.LevelUpGreen
import com.example.proyectologin006d_final.ui.theme.LevelUpWhite
import com.example.proyectologin006d_final.ui.cart.CartViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ProductCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color
)

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val category: String,
    val description: String,
    val manufacturer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
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

    val categories = listOf(
        ProductCategory("juegos-mesa", "Juegos de Mesa", "🎲", LevelUpBlue),
        ProductCategory("accesorios", "Accesorios", "🎮", LevelUpGreen),
        ProductCategory("consolas", "Consolas", "🕹️", LevelUpBlue),
        ProductCategory("computadores", "Computadores Gamers", "💻", LevelUpGreen),
        ProductCategory("sillas", "Sillas Gamers", "🪑", LevelUpBlue),
        ProductCategory("mouse", "Mouse", "🖱️", LevelUpGreen),
        ProductCategory("mousepad", "Mousepad", "🖼️", LevelUpBlue),
        ProductCategory("poleras", "Poleras Personalizadas", "👕", LevelUpGreen),
        ProductCategory("polerones", "Polerones Gamers", "🧥", LevelUpBlue)
    )

    val sampleProducts = listOf(
        Product("JM001", "Catan", "$29.990 CLP", "Juegos de Mesa", "Un clásico juego de estrategia", "Catan Studio"),
        Product("JM002", "Carcassonne", "$24.990 CLP", "Juegos de Mesa", "Juego de colocación de fichas", "Z-Man Games"),
        Product("AC001", "Controlador Xbox Series X", "$59.990 CLP", "Accesorios", "Controlador inalámbrico", "Microsoft"),
        Product("AC002", "Auriculares HyperX Cloud II", "$79.990 CLP", "Accesorios", "Sonido envolvente de calidad", "HyperX"),
        Product("CO001", "PlayStation 5", "$549.990 CLP", "Consolas", "Consola de última generación", "Sony"),
        Product("CG001", "PC Gamer ASUS ROG Strix", "$1.299.990 CLP", "Computadores Gamers", "Potente equipo para gamers", "ASUS"),
        Product("SG001", "Silla Secretlab Titan", "$349.990 CLP", "Sillas Gamers", "Máximo confort para gaming", "Secretlab"),
        Product("MS001", "Mouse Logitech G502 HERO", "$49.990 CLP", "Mouse", "Sensor de alta precisión", "Logitech"),
        Product("MP001", "Mousepad Razer Goliathus", "$29.990 CLP", "Mousepad", "Iluminación RGB personalizable", "Razer"),
        Product("PP001", "Polera Gamer 'Level-Up'", "$14.990 CLP", "Poleras Personalizadas", "Personalizable con tu gamer tag", "Level-Up Gamer")
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Level-Up Gamer",
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Botón del carrito con badge
                    BadgedBox(
                        badge = {
                            if (uiState.totalItems > 0) {
                                Badge(
                                    containerColor = LevelUpGreen,
                                    contentColor = LevelUpBlack
                                ) {
                                    Text("${uiState.totalItems}")
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { navController.navigate("cart/$username") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ir al carrito",
                                tint = LevelUpWhite
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(LevelUpBlack)
        ) {
            // Header con saludo y animación decorativa simple
            var headerFloating by remember { mutableStateOf(false) }
            val headerOffset by animateFloatAsState(
                targetValue = if (headerFloating) 5f else 0f,
                animationSpec = tween(2000),
                label = "header-offset"
            )
            
            LaunchedEffect(Unit) {
                while (true) {
                    headerFloating = !headerFloating
                    kotlinx.coroutines.delay(2000)
                }
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = headerOffset.dp),
                colors = CardDefaults.cardColors(containerColor = LevelUpBlue.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Bienvenido, $username!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explora nuestro catálogo gamer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LevelUpWhite.copy(alpha = 0.8f)
                    )
                }
            }

            // Categorías
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.headlineSmall,
                color = LevelUpWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { /* Sin funcionalidad - solo visual */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Productos destacados
            Text(
                text = "Productos Destacados",
                style = MaterialTheme.typography.headlineSmall,
                color = LevelUpWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { /* Sin funcionalidad - solo visual */ },
                        onAddToCart = {
                            cartViewModel.addToCart(
                                productId = product.id,
                                productName = product.name,
                                productPrice = product.price,
                                category = product.category,
                                description = product.description,
                                manufacturer = product.manufacturer,
                                username = username
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: ProductCategory,
    onClick: () -> Unit
) {
    // Animación simple de escala al presionar
    val categoryInteraction = remember { MutableInteractionSource() }
    val categoryPressed by categoryInteraction.collectIsPressedAsState()
    val categoryScale by animateFloatAsState(
        targetValue = if (categoryPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "category-scale"
    )

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp)
            .scale(categoryScale)
            .clickable(
                interactionSource = categoryInteraction,
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = category.color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación decorativa simple para el icono
            var iconRotating by remember { mutableStateOf(false) }
            val iconRotation by animateFloatAsState(
                targetValue = if (iconRotating) 360f else 0f,
                animationSpec = tween(3000),
                label = "icon-rotation"
            )
            
            LaunchedEffect(Unit) {
                while (true) {
                    iconRotating = !iconRotating
                    kotlinx.coroutines.delay(3000)
                }
            }
            
            Text(
                text = category.icon,
                fontSize = 24.sp,
                modifier = Modifier.rotate(iconRotation)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                color = LevelUpWhite,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    // Scope para coroutines
    val coroutineScope = rememberCoroutineScope()
    
    // Animación simple de escala al presionar (usa InteractionSource para detectar press)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "press-scale"
    )
    
    // Animación para el botón de agregar al carrito
    var isAddingToCart by remember { mutableStateOf(false) }
    val addToCartScale by animateFloatAsState(
        targetValue = if (isAddingToCart) 1.2f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "add-cart-scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Fabricante: ${product.manufacturer}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpWhite.copy(alpha = 0.5f)
                    )
                }
                
                // Animación decorativa simple para el precio
                var priceGlowing by remember { mutableStateOf(false) }
                val priceAlpha by animateFloatAsState(
                    targetValue = if (priceGlowing) 1f else 0.7f,
                    animationSpec = tween(1500),
                    label = "price-alpha"
                )
                
                LaunchedEffect(Unit) {
                    while (true) {
                        priceGlowing = !priceGlowing
                        kotlinx.coroutines.delay(1500)
                    }
                }
                
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleLarge,
                    color = LevelUpBlue.copy(alpha = priceAlpha),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón de agregar al carrito
            Button(
                onClick = {
                    isAddingToCart = true
                    onAddToCart()
                    coroutineScope.launch {
                        delay(150)
                        isAddingToCart = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .scale(addToCartScale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LevelUpGreen,
                    contentColor = LevelUpBlack
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Agregar al carrito",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar al Carrito",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    CatalogScreen(
        navController = rememberNavController(),
        username = "Gamer123"
    )
}
