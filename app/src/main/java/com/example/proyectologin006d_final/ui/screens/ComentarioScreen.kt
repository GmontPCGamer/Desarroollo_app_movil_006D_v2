package com.example.proyectologin006d_final.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectologin006d_final.viewmodel.ComentarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentarioScreen(
    viewModel: ComentarioViewModel,
    postId: Int? = null // si se pasa, filtra por publicación
) {
    if (postId == null) {
        viewModel.cargarTodos()
    } else {
        viewModel.cargarPorPublicacion(postId)
    }

    val comentarios = viewModel.comentarios.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Comentarios") }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(comentarios) { comentario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "De: ${comentario.name} (${comentario.email})")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = comentario.body)
                        }
                    }
                }
            }
        }
    }
}


//