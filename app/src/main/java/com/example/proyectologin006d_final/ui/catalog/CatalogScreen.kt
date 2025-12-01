package com.example.proyectologin006d_final.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.data.model.Producto
import com.example.proyectologin006d_final.ui.theme.LevelUpBlack
import com.example.proyectologin006d_final.ui.theme.LevelUpBlue
import com.example.proyectologin006d_final.ui.theme.LevelUpGreen
import com.example.proyectologin006d_final.ui.theme.LevelUpWhite
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectologin006d_final.viewmodel.CartViewModel
import com.example.proyectologin006d_final.viewmodel.CartViewModelFactory
import com.example.proyectologin006d_final.viewmodel.ProductoViewModel
import com.example.proyectologin006d_final.viewmodel.ProductoViewModelFactory
import com.example.proyectologin006d_final.data.repository.ProductoRepository
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch

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
    val cartState by cartViewModel.cartState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
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
                    val cartState by cartViewModel.cartState.collectAsState()
                    IconButton(onClick = {
                        navController.navigate("paymentExpress/$username")
                    }) {
                        BadgedBox(
                            badge = {
                                if (cartState.totalItems > 0) {
                                    Badge {
                                        Text(text = cartState.totalItems.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = LevelUpWhite
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpBlack
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleProducts) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { uiProduct ->
                            // Convertimos Product de la UI a Producto de dominio
                            val producto = Producto(
                                codigo = uiProduct.id,
                                categoria = uiProduct.category,
                                nombre = uiProduct.name,
                                precio = uiProduct.price,
                                descripcion = uiProduct.description,
                                cantidad = "1",
                                direccion = "",
                                fabricante = uiProduct.manufacturer
                            )
                            cartViewModel.addToCart(producto)
                            // Mostrar snackbar de confirmación
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${uiProduct.name} agregado al carrito",
                                    actionLabel = "Ver carrito",
                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                ).let { result ->
                                    if (result == SnackbarResult.ActionPerformed) {
                                        navController.navigate("paymentExpress/$username")
                                    }
                                }
                            }
                        }
                    )
                }
                
                // Resumen del carrito al final de la lista
                item {
                    if (cartState.items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = LevelUpGreen.copy(alpha = 0.2f)
                            ),
                            onClick = {
                                navController.navigate("paymentExpress/$username")
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Carrito: ${cartState.totalItems} ${if (cartState.totalItems == 1) "producto" else "productos"}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = LevelUpWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Total: ${cartState.totalAmount.toInt()} CLP",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = LevelUpGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = {
                                        navController.navigate("paymentExpress/$username")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LevelUpGreen
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Ir al carrito",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pagar")
                                }
                            }
                        }
                    }
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
    onAddToCart: (Product) -> Unit
) {
    // Animación simple de escala al presionar (usa InteractionSource para detectar press)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "press-scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = LevelUpBlack),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                        color = LevelUpWhite.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
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

            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Agregar al carrito")
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
