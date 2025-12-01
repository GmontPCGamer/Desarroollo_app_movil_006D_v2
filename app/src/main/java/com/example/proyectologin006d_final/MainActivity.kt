package com.example.proyectologin006d_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

// Importa tus pantallas principales del proyecto
import com.example.proyectologin006d_final.ui.login.LoginScreen
import com.example.proyectologin006d_final.ui.register.RegisterScreen
import com.example.proyectologin006d_final.ui.home.MuestrasDatosScreen

// Importa las pantallas de API REST (asegúrate de que estén disponibles)
import com.example.proyectologin006d_final.ui.screens.PostScreen
import com.example.proyectologin006d_final.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para que la app dibuje contenido debajo de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Inicia Jetpack Compose con navegación unificada
        setContent {
            AppNav()
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Pantallas principales de login/registro
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        // Pantalla de API REST
        composable("posts") {
            val postViewModel: PostViewModel = viewModel()
            PostScreen(
                viewModel = postViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// Si necesitas mantener el tema de API REST, puedes combinarlo así:
@Composable
fun CombinedTheme(content: @Composable () -> Unit) {
    // Si tienes temas diferentes, usa el de tu proyecto principal
    // o crea uno combinado
    content()
}