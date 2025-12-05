package com.example.proyectologin006d_final.ui.profile

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.data.model.Discount
import com.example.proyectologin006d_final.ui.qr.QrScannerScreen
import com.example.proyectologin006d_final.utils.CameraPermissionHelper
import com.example.proyectologin006d_final.viewmodel.QrViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    navController: NavController,
    vm: ProfileViewModel = viewModel(),
    discountViewModel: DiscountViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val discountUiState by discountViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showQrScanner by remember { mutableStateOf(false) }
    val qrViewModel: QrViewModel = viewModel()
    var hasCameraPermission by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        val message = if (isGranted) {
            "Permiso de cámara concedido"
        } else {
            "Se necesita permiso de cámara para escanear QR"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(context)
    }

    LaunchedEffect(username) {
        discountViewModel.loadDiscounts(username)
    }

    LaunchedEffect(discountUiState.message) {
        discountUiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            discountViewModel.clearMessage()
        }
    }

    LaunchedEffect(showQrScanner) {
        if (!showQrScanner) {
            qrViewModel.clearResult()
        }
    }

    val colorScheme = darkColorScheme(
        primary = Color(0xFF0BA360),      // Verde principal
        secondary = Color(0xFF0072FF),    // Azul de acento
        onPrimary = Color.White,
        onSurface = Color(0xFF333333),
    )

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (showQrScanner) "Escáner QR" else "Mi Perfil",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        if (showQrScanner) {
                            androidx.compose.material3.IconButton(
                                onClick = { showQrScanner = false }
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (showQrScanner) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    QrScannerScreen(
                        viewModel = qrViewModel,
                        hasCameraPermission = hasCameraPermission,
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onQrDetected = { qrContent ->
                            val sanitized = qrContent.trim()
                            if (sanitized.isNotEmpty()) {
                                discountViewModel.addDiscountFromQr(sanitized, username)
                            }
                            showQrScanner = false
                        }
                    )
                }
            } else {
                ProfileDetailsContent(
                    username = username,
                    innerPadding = innerPadding,
                    uiState = uiState,
                    discountUiState = discountUiState,
                    onScanClick = { showQrScanner = true },
                    onBackClick = { navController.navigateUp() },
                    onUseDiscount = { discount ->
                        discountViewModel.useDiscount(discount.id)
                    },
                    onDeleteDiscount = { discount ->
                        discountViewModel.deleteDiscount(discount)
                    },
                    onClearHighlight = {
                        discountViewModel.clearLastScannedDiscount()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileDetailsContent(
    username: String,
    innerPadding: PaddingValues,
    uiState: ProfileUiState,
    discountUiState: DiscountUiState,
    onScanClick: () -> Unit,
    onBackClick: () -> Unit,
    onUseDiscount: (Discount) -> Unit,
    onDeleteDiscount: (Discount) -> Unit,
    onClearHighlight: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0BA360), // verde
                        Color(0xFF0072FF)  // azul
                    )
                )
            )
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Hola, $username!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Escanea tus códigos QR para acumular descuentos exclusivos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onScanClick,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escáner QR",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Escanear QR de descuento")
            }
        }

        if (uiState.isLoading) {
            Text(
                text = "Procesando imagen...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        discountUiState.lastScannedDiscount?.let { discount ->
            DiscountHighlightCard(discount = discount, onDismiss = onClearHighlight)
        }

        DiscountSummaryCard(discountUiState.activeDiscountCount)

        if (discountUiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        DiscountListSection(
            discounts = discountUiState.discounts,
            onUseDiscount = onUseDiscount,
            onDeleteDiscount = onDeleteDiscount
        )

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun DiscountHighlightCard(discount: Discount, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¡Nuevo descuento agregado!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${discount.percentage}% - ${discount.description}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Código: ${discount.code}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    }
}

@Composable
private fun DiscountSummaryCard(activeDiscountCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Descuentos disponibles",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$activeDiscountCount activos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DiscountListSection(
    discounts: List<Discount>,
    onUseDiscount: (Discount) -> Unit,
    onDeleteDiscount: (Discount) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Historial de descuentos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (discounts.isEmpty()) {
                Text(
                    text = "Aún no has guardado descuentos. ¡Escanea tu primer código QR!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                discounts.forEach { discount ->
                    DiscountCard(
                        discount = discount,
                        onUseDiscount = onUseDiscount,
                        onDeleteDiscount = onDeleteDiscount
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscountCard(
    discount: Discount,
    onUseDiscount: (Discount) -> Unit,
    onDeleteDiscount: (Discount) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = discount.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Código: ${discount.code}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Descuento: ${discount.percentage}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            formatTimestamp(discount.expiresAt)?.let { expiration ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Expira: $expiration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!discount.isUsed) {
                    Button(
                        onClick = { onUseDiscount(discount) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Marcar usado")
                    }
                } else {
                    Text(
                        text = "✅ Canjeado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedButton(
                    onClick = { onDeleteDiscount(discount) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long?): String? {
    if (timestamp == null) return null
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
private fun ProfileDetailsContentPreview() {
    val sampleDiscounts = listOf(
        Discount(
            id = 1,
            code = "LEVELUP:20:Consolas",
            description = "20% en consolas seleccionadas",
            percentage = 20,
            username = "UsuarioGamer"
        ),
        Discount(
            id = 2,
            code = "GAMERFRIDAY",
            description = "15% en accesorios",
            percentage = 15,
            username = "UsuarioGamer",
            isUsed = true
        )
    )

    ProfileDetailsContent(
        username = "UsuarioGamer",
        innerPadding = PaddingValues(0.dp),
        uiState = ProfileUiState(),
        discountUiState = DiscountUiState(
            discounts = sampleDiscounts,
            activeDiscountCount = 1
        ),
        onScanClick = {},
        onBackClick = {},
        onUseDiscount = {},
        onDeleteDiscount = {},
        onClearHighlight = {}
    )
}
