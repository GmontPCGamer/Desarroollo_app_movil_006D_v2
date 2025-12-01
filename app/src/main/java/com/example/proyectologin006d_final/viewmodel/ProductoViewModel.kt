package com.example.proyectologin006d_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {
private val _productos = MutableStateFlow<List<Producto>> (emptyList())

val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

fun guardarProducto(producto: Producto){
    viewModelScope.launch{
        val nuevaLista  = _productos.value +producto
        _productos.value =nuevaLista

    }
}// fin fun
} // ViewModel
