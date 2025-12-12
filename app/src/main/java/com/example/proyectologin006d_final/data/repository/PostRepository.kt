package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.model.Post
import com.example.proyectologin006d_final.data.remote.RetrofitInstance

// Este repositorio se encarga de acceder a los datos Retrofit

class PostRepository {

    // Funcion que obtienen los post desde la API
    suspend fun getPosts(): List<Post>{
        return RetrofitInstance.api.getPosts()
    }
}// fin clase