package com.example.proyectologin006d_final.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Instancia la Api
    val api:ApiService by lazy{
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create()) // Conversor Json

            .build()
            .create(ApiService::class.java)
    }

}