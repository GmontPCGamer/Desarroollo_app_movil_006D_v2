package com.example.proyectologin006d_final.ui.map

import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {

    // Coordenadas de tu tienda (cambia por tus coordenadas reales)
    val storeLocation = GeoPoint(-33.59834, -70.57867) // Ejemplo: Duoc Puente Alto

    val storeName = "Tienda LevelUp"
    val storeAddress = "Av. Concha y Toro 1340, Puente Alto, Region Metropolitana"
    val storePhone = "+569 43430909"
}

