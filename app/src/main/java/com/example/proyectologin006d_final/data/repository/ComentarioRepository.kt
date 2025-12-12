package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.model.Comentario
import com.example.proyectologin006d_final.data.remote.RetrofitInstance

class ComentarioRepositorio {
    suspend fun obtenerComentarios(): List<Comentario> =
        RetrofitInstance.api.obtenerComentarios()

    suspend fun obtenerComentariosPorPublicacion(postId: Int): List<Comentario> =
        RetrofitInstance.api.obtenerComentariosPorPublicacion(postId)
}

//