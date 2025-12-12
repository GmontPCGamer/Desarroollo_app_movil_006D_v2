package com.example.proyectologin006d_final.view


import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.osmdroid.views.overlay.LineDrawer

@Composable

fun DrawerMenu(
    username:String,
    navController: NavController,
    currentRoute: String?, //necesarias para mapa
    onCloseDrawer: ()-> Unit //necesarias para mapa
) { // inicio
    Column(modifier = Modifier.fillMaxSize())
    { // inicio columna
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) // fin box

        { // inicio contenido
            Text(
                text="Level-Up Gamer\nBienvenido, $username" ,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
            )// fin texto
        }// fin contenido

        //  Items


        //LazyColumn: Crea una lista de elementos que se pueden desplazar verticalmente.
        // Solo los elementos que están visibles en la pantalla se crean y se muestran,
        // lo que mejora el rendimiento, especialmente para listas grandes.

        LazyColumn( modifier = Modifier.weight(1f)) {
            item{ // inicio item 1
                NavigationDrawerItem( // inicio DrawerItem
                    label = {Text("Catálogo de Productos")},
                    selected =false,
                    onClick = {
                        navController.navigate("catalog/$username")
                    }, // fin onclick
                    icon = {Icon(Icons.Default.Gamepad ,  contentDescription ="Catálogo" )}

                ) // fin DrawerItem
            } // fin item 1

            item{ // inicio item 2
                NavigationDrawerItem( // inicio DrawerItem
                    label = {Text("Mi Perfil")},
                    selected =false,
                    onClick = { 
                        navController.navigate("profile/$username")
                    }, // fin onclick
                    icon = {Icon(Icons.Default.Person ,  contentDescription ="Perfil" )}

                ) // fin DrawerItem
            } // fin item 2

            item{ // inicio item 4
                NavigationDrawerItem( // inicio DrawerItem
                    label = {Text("Carrito de Compras")},
                    selected = currentRoute == "cart/$username",
                    onClick = { 
                        navController.navigate("cart/$username")
                        onCloseDrawer()
                    }, // fin onclick
                    icon = {Icon(Icons.Default.ShoppingCart ,  contentDescription ="Carrito" )}

                ) // fin DrawerItem
            } // fin item 4

            item{ // inicio item 5
                NavigationDrawerItem( // inicio DrawerItem
                    label = {Text("LevelUp Points")},
                    selected =false,
                    onClick = { 
                        navController.navigate("levelup/$username")
                    }, // fin onclick
                    icon = {Icon(Icons.Default.EmojiEvents ,  contentDescription ="LevelUp" )}

                ) // fin DrawerItem
            } // fin item 5


            item { //inicio item 7 (mapa tienda)
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Ubicación de la tienda"
                        )
                    },
                    label = {
                        Text(text = "Ubicación de la tienda")
                    },
                    selected = currentRoute == "map",
                    onClick = {
                        navController.navigate(route = "map")
                        onCloseDrawer()
                    }
                )
            }

            item { // inicio item (posts API)
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Posts"
                        )
                    },
                    label = {
                        Text(text = "Posts (API)")
                    },
                    selected = currentRoute == "posts",
                    onClick = {
                        navController.navigate(route = "posts")
                        onCloseDrawer()
                    }
                )
            }

            item {// inicio item (Comentarios API)
                NavigationDrawerItem(
                    label = { Text("Comentarios") },
                    selected = currentRoute?.startsWith("comentarios") == true,
                    onClick = {
                        navController.navigate("comentarios")
                        onCloseDrawer()
                    },
                    icon = { Icon(Icons.Default.QuestionAnswer, contentDescription = "Comentarios") }
                )
            }



//
        } // fin Lazy

//  Footer del drawer
        Text(
            text ="@ 2025 Level-Up Gamer",
            style = MaterialTheme.typography.bodySmall,

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        ) // fin footer

    } // termino columna

} // fin


@Preview(showBackground = true)
@Composable


fun DrawerMenuPreview(){
    val navController = androidx.navigation.compose.rememberNavController()
    DrawerMenu(
        username = "Usuario Prueba",
        navController = navController,
        currentRoute = null,   // o "catalog" si quieres simular selección
        onCloseDrawer = {}     // no hace nada en el preview
    )
}
