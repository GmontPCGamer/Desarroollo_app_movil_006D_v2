package com.example.proyectologin006d_final.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectologin006d_final.ui.login.LoginScreen
import com.example.proyectologin006d_final.ui.register.RegisterScreen
import com.example.proyectologin006d_final.ui.profile.ProfileScreen
import com.example.proyectologin006d_final.ui.catalog.CatalogScreen
import com.example.proyectologin006d_final.ui.gamification.LevelUpScreen
import com.example.proyectologin006d_final.ui.cart.CartScreen
import com.example.proyectologin006d_final.view.DrawerMenu
import com.example.proyectologin006d_final.view.ProductoFormScreen
import com.example.proyectologin006d_final.ui.map.MapScreen


import com.example.proyectologin006d_final.ui.screens.PostScreen
import com.example.proyectologin006d_final.viewmodel.PostViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        
        composable(
            route = "profile/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            ProfileScreen(
                username = username,
                navController = navController
            )
        }

        composable(
            route = "catalog/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            CatalogScreen(navController = navController, username = username)
        }


        composable("posts") {
            val postViewModel: PostViewModel = viewModel()
            PostScreen(viewModel = postViewModel)
        }


        composable(
            route = "levelup/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            LevelUpScreen(navController = navController, username = username)
        }

        // Ruta del Carrito de Compras
        composable(
            route = "cart/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            CartScreen(navController = navController, username = username)
        }

        composable(
            route = "DrawerMenu/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            DrawerMenu(username = username,
                navController = navController,
                currentRoute = "DrawerMenu/$username",
                onCloseDrawer = {} // no hace nada (solo evita el error
            )
        }

        composable(
            route = "ProductoFormScreen/{nombre}/{precio}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
            val precio = Uri.decode(backStackEntry.arguments?.getString("precio") ?: "")
            ProductoFormScreen(navController, nombre = nombre, precio = precio)
        }

        composable("map") {
            MapScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
