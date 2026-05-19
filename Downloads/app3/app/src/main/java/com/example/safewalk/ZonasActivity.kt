package com.example.safewalk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class ZonasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zonas)

        // Inicializar el cliente de ubicación de Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cargar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapZonas) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el botón celeste (Brújula)
        findViewById<CardView>(R.id.btnMyLocation).setOnClickListener {
            centrarEnUbicacionReal()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 1. Aplicar estilo oscuro (Yango Style)
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) { /* Error al cargar estilo */ }
        } catch (e: Exception) { e.printStackTrace() }

        // 2. Verificar Permisos y activar Punto Azul
        comprobarPermisosYActivarGps()

        // 3. Cargar las zonas de riesgo del PDF
        dibujarZonasDeRiesgo()

        // 4. Zoom inicial (Cochabamba Centro)
        val centroCbba = LatLng(-17.3935, -66.1568)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroCbba, 13f))
    }

    private fun comprobarPermisosYActivarGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false // Usamos nuestro botón celeste
            centrarEnUbicacionReal()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }
    }

    private fun centrarEnUbicacionReal() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val miPosicion = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 16f))
            } else {
                Toast.makeText(this, "Buscando señal de satélite...", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dibujarZonasDeRiesgo() {
        // Lista de coordenadas extraídas de tu PDF
        val puntosInseguros = listOf(
            Triple(-17.3935, -66.1568, "Crítico"),
            Triple(-17.3930, -66.1570, "Crítico"),
            Triple(-17.3942, -66.2815, "Medio"),
            Triple(-17.3970, -66.2880, "Medio"),
            Triple(-17.407879, -66.164018, "Crítico"), // Zona Aroma
            Triple(-17.414110, -66.165290, "Crítico"),
            Triple(-17.4500, -65.7500, "Medio"),
            Triple(-17.410114, -65.700441, "Crítico"),
            Triple(-17.7260, -65.1930, "Crítico"),
            Triple(-17.401228, -66.163418, "Crítico"),
            Triple(-17.389812, -66.157135, "Crítico"),
            Triple(-17.430221, -66.170552, "Crítico"),
            Triple(-17.418774, -66.150981, "Crítico"),
            Triple(-17.392041, -66.279102, "Crítico"),
            Triple(-17.389113, -66.301882, "Crítico"),
            Triple(-17.397482, -65.287441, "Medio"),
            Triple(-16.998321, -65.421114, "Crítico"),
            Triple(-16.989872, -65.141203, "Crítico"),
            Triple(-16.980552, -65.250781, "Crítico"),
            Triple(-17.980552, -65.250781, "Medio"),
            Triple(-17.568442, -65.768220, "Medio"),
            Triple(-17.608114, -66.018334, "Medio"),
            Triple(-17.371974, -66.14455, "Medio"),

        )

        for (p in puntosInseguros) {
            val colorHex = if (p.third == "Crítico") "#EF4444" else "#F59E0B"
            val colorBase = Color.parseColor(colorHex)

            mMap.addCircle(CircleOptions()
                .center(LatLng(p.first, p.second))
                .radius(120.0) // Tamaño del círculo
                .strokeWidth(3f)
                .strokeColor(colorBase)
                .fillColor(Color.argb(80, Color.red(colorBase), Color.green(colorBase), Color.blue(colorBase))))
        }
    }

    // Si el usuario acepta los permisos en el momento
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate() // Reiniciar para aplicar cambios
        }
    }
}