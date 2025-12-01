package com.example.proyectologin006d_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.proyectologin006d_final.navigation.AppNav
import com.example.proyectologin006d_final.ui.theme.ProyectoLogin006D_finalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para que la app dibuje contenido debajo de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Inicia Jetpack Compose con navegación unificada
        setContent {
            ProyectoLogin006D_finalTheme {
                AppNav()
            }
        }
    }
}
