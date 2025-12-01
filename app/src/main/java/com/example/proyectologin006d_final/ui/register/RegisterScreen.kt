package com.example.proyectologin006d_final.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.R
import com.example.proyectologin006d_final.ui.register.RegisterViewModelFactory
import com.example.proyectologin006d_final.data.repository.UserRepository
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.animateFloatAsState as animateAlpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            UserRepository(
                ProductoDatabase.getDatabase(LocalContext.current).userDao()
            )
        )
    )
) {
    val state by vm.uiState.collectAsState()
    var showPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }
    var isAdultVerified by remember { mutableStateOf(false) }

    val ColorScheme = darkColorScheme(
        primary = Color(0xFF98222E),
        onPrimary = Color.White,
        onSurface = Color(0xFF333333),
    )

    MaterialTheme(colorScheme = ColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Registro",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color(0xFFF0F0F0)),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Únete a Level-Up Gamer!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Image(
                    painter = painterResource(id = R.drawable.logolevelup),
                    contentDescription = "Logo App",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Checkbox(
                        checked = isAdultVerified,
                        onCheckedChange = { isAdultVerified = it }
                    )
                    Text(
                        text = "Confirmo que soy mayor de 18 años",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = state.username,
                    onValueChange = vm::onUsernameChange,
                    label = { Text("Nombre de Usuario") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .alpha(animateAlpha(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 300),
                            label = "username-fade"
                        ).value)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = vm::onEmailChange,
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .alpha(animateAlpha(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 400),
                            label = "email-fade"
                        ).value)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = vm::onPasswordChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showPass = !showPass }) {
                            Text(if (showPass) "Ocultar" else "Ver")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .alpha(animateAlpha(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 500),
                            label = "password-fade"
                        ).value)
                )

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = vm::onConfirmPasswordChange,
                    label = { Text("Confirmar Contraseña") },
                    singleLine = true,
                    visualTransformation = if (showConfirmPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showConfirmPass = !showConfirmPass }) {
                            Text(if (showConfirmPass) "Ocultar" else "Ver")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .alpha(animateAlpha(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 600),
                            label = "confirm-fade"
                        ).value)
                )

                if (state.email.contains("@duoc.cl", ignoreCase = true)) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "🎉 ¡Descuento del 20% aplicado para usuarios Duoc!",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (state.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón con animación de escala
                val buttonInteraction = remember { MutableInteractionSource() }
                val buttonPressed by buttonInteraction.collectIsPressedAsState()
                val buttonScale by animateFloatAsState(
                    targetValue = if (buttonPressed) 0.95f else 1f,
                    animationSpec = tween(durationMillis = 100),
                    label = "button-scale"
                )

                Button(
                    onClick = {
                        if (!isAdultVerified) {
                            return@Button
                        }
                        vm.registerUser { username ->
                            navController.navigate("DrawerMenu/$username") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    enabled = !state.isLoading && isAdultVerified,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .scale(buttonScale)
                        .clickable(
                            interactionSource = buttonInteraction,
                            indication = null,
                            onClick = { /* handled by Button onClick */ }
                        )
                ) {
                    Text(if (state.isLoading) "Registrando..." else "Registrarse")
                }

                Spacer(modifier = Modifier.height(5.dp))

                TextButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController = navController)
}
