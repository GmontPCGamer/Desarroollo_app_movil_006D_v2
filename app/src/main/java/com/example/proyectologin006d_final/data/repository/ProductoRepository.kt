package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.ProductoDao
import com.example.proyectologin006d_final.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository (private val productoDao: ProductoDao){

    suspend fun insertarProducto(producto: Producto){
        productoDao.insertarProducto(producto)
    }

    fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerProductos()
    }


    // Flow: permite al DAO observar los datos de los productos
    // en tiempo real, culquier cambio en BD se refleja automaticamente
    // en UI



    // el Repository es una capa intermedia BBDD(DAO) y la UI
    // encapsulamos las operaciones

}// fin class