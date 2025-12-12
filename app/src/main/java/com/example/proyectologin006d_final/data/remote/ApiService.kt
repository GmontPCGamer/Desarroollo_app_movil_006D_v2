package com.example.proyectologin006d_final.data.remote
import com.example.proyectologin006d_final.data.model.Comentario
import com.example.proyectologin006d_final.data.model.Post

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Define una solicitud GET
    @GET(value="/posts")
    suspend fun getPosts():List<Post>

    @GET("/comments")
    suspend fun obtenerComentarios(): List<Comentario>

    @GET("/comments")
    suspend fun obtenerComentariosPorPublicacion(
        @Query("postId") postId: Int
    ): List<Comentario>


}
//comento