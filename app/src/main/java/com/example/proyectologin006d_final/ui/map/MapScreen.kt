package com.example.proyectologin006d_final.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBackClick: () -> Unit,
    storeLocation: GeoPoint = GeoPoint(-33.59834, -70.57867) // Duoc puente alto (cambia por tu ubicación)
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // Configurar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            initializeMap(mapView, storeLocation)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación de la Tienda") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            ) { view ->
                // Configurar OSMDroid
                Configuration.getInstance().userAgentValue = context.packageName

                // Verificar permisos
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    initializeMap(view, storeLocation)
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onPause()
        }
    }
}

private fun initializeMap(mapView: MapView, storeLocation: GeoPoint) {
    mapView.apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)

        // Configurar ubicación de la tienda
        controller.setZoom(15.0)
        controller.setCenter(storeLocation)

        // Agregar marcador en la ubicación de la tienda
        val marker = Marker(mapView).apply {
            position = storeLocation
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Nuestra Tienda"
            snippet = "¡Visítanos!"
        }
        overlays.add(marker)
    }
}