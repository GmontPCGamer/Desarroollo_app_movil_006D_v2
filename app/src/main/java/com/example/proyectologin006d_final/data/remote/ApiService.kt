package com.example.proyectologin006d_final.data.remote

import com.example.proyectologin006d_final.data.model.Post

import retrofit2.http.GET


interface ApiService {
    // Define una solicitud GET
    @GET(value="/posts")
    suspend fun getPosts():List<Post>


}
//comento