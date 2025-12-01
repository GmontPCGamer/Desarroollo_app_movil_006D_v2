package com.example.proyectologin006d_final.ui.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectologin006d_final.ui.theme.LevelUpBlack
import com.example.proyectologin006d_final.ui.theme.LevelUpBlue
import com.example.proyectologin006d_final.ui.theme.LevelUpGreen
import com.example.proyectologin006d_final.ui.theme.LevelUpWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpScreen(
    navController: NavController,
    username: String
) {
    // Datos simulados para la demo
    val puntosUsuario = 1250
    val nivelUsuario = 3
    val codigoReferido = "REF-$username-ABC123"
    val referidos = listOf("GamerPro", "LevelMaster", "GameKing")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Level-Up Points",
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
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
                .padding(16.dp)
        ) {
            // Header con puntos y nivel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LevelUpBlue.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Bienvenido, $username!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$puntosUsuario",
                                style = MaterialTheme.typography.headlineLarge,
                                color = LevelUpGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Puntos LevelUp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LevelUpWhite.copy(alpha = 0.8f)
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nivel $nivelUsuario",
                                style = MaterialTheme.typography.headlineLarge,
                                color = LevelUpBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tu Nivel",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LevelUpWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Código de referido
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LevelUpGreen.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tu Código de Referido",
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = codigoReferido,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LevelUpGreen,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Comparte este código y gana 100 puntos por cada amigo que se registre",
                        style = MaterialTheme.typography.bodySmall,
                        color = LevelUpWhite.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de referidos
            Text(
                text = "Tus Referidos",
                style = MaterialTheme.typography.titleLarge,
                color = LevelUpWhite,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (referidos.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(referidos) { referido ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LevelUpBlack),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👤",
                                    fontSize = 24.sp
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = referido,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = LevelUpWhite,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "+100 puntos ganados",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LevelUpGreen
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Aún no tienes referidos. ¡Comparte tu código!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LevelUpWhite.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Beneficios por nivel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LevelUpBlue.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Beneficios por Nivel",
                        style = MaterialTheme.typography.titleMedium,
                        color = LevelUpWhite,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val beneficios = listOf(
                        "Nivel 1: Descuento del 5% en todos los productos",
                        "Nivel 2: Descuento del 10% + envío gratis",
                        "Nivel 3: Descuento del 15% + productos exclusivos",
                        "Nivel 4+: Descuento del 20% + acceso anticipado"
                    )
                    
                    beneficios.forEach { beneficio ->
                        Text(
                            text = "• $beneficio",
                            style = MaterialTheme.typography.bodySmall,
                            color = LevelUpWhite.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LevelUpScreenPreview() {
    LevelUpScreen(
        navController = rememberNavController(),
        username = "Gamer123"
    )
}


