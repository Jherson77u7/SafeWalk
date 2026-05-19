package com.example.safewalk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class ZonasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zonas)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapZonas) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Botón celeste de ubicación
        findViewById<CardView>(R.id.btnMyLocation).setOnClickListener {
            centrarEnMiUbicacion()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (e: Exception) { e.printStackTrace() }

        // Activar capa de ubicación real (punto azul)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false // Ocultamos el de Google para usar el nuestro
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        cargarPuntosInseguros()

        // Centrar cámara inicialmente en Cochabamba
        val cbba = LatLng(-17.3935, -66.1568)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cbba, 12f))
    }

    private fun cargarPuntosInseguros() {
        // Coordenadas extraídas de tu PDF
        val puntos = listOf(
            // PÁGINA 1
            Triple(-17.3935, -66.1568, "C"), Triple(-17.3930, -66.1570, "C"), Triple(-17.3929, -66.1579, "C"),
            Triple(-17.3957, -66.1547, "C"), Triple(-17.4012, -66.1634, "C"), Triple(-17.3898, -66.1571, "C"),
            Triple(-17.4300, -66.1700, "C"), Triple(-17.4180, -66.1500, "C"), Triple(-17.3942, -66.2815, "M"),
            Triple(-17.3970, -66.2880, "M"), Triple(-16.9980, -65.4210, "C"), Triple(-16.9890, -65.1410, "C"),
            // PÁGINA 2
            Triple(-17.0600, -66.8500, "C"), Triple(-17.0500, -66.9000, "C"), Triple(-17.7260, -65.1930, "C"),
            Triple(-17.7400, -65.1700, "C"), Triple(-17.9660, -66.5300, "B"), Triple(-17.9800, -65.3000, "C"),
            // PÁGINA 3
            Triple(-17.6200, -66.6100, "B"), Triple(-17.4100, -65.7000, "C"), Triple(-17.4500, -65.7500, "M")
        )

        for (p in puntos) {
            val colorBase = when(p.third) {
                "C" -> Color.RED
                "M" -> Color.parseColor("#F59E0B") // Naranja/Ambar
                else -> Color.GREEN
            }

            mMap.addCircle(CircleOptions()
                .center(LatLng(p.first, p.second))
                .radius(150.0)
                .strokeWidth(2f)
                .strokeColor(colorBase)
                .fillColor(adjustAlpha(colorBase, 0.3f)))
        }
    }

    private fun centrarEnMiUbicacion() {
        // En una app real usarías FusedLocationProvider,
        // para la feria simularemos el salto a la posición actual:
        val miSimulacion = LatLng(-17.3935, -66.1568)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miSimulacion, 15f))
        Toast.makeText(this, "Centrando en tu ubicación...", Toast.LENGTH_SHORT).show()
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}