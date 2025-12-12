package com.example.proyectologin006d_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.model.Comentario
import com.example.proyectologin006d_final.data.repository.ComentarioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComentarioViewModel(
    private val repositorio: ComentarioRepositorio = ComentarioRepositorio()
) : ViewModel() {

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios

    fun cargarTodos() {
        viewModelScope.launch {
            try {
                _comentarios.value = repositorio.obtenerComentarios()
            } catch (e: Exception) {
                println("Error al obtener comentarios: ${e.localizedMessage}")
            }
        }
    }

    fun cargarPorPublicacion(postId: Int) {
        viewModelScope.launch {
            try {
                _comentarios.value = repositorio.obtenerComentariosPorPublicacion(postId)
            } catch (e: Exception) {
                println("Error al obtener comentarios por publicación: ${e.localizedMessage}")
            }
        }
    }
}

//ffdgf