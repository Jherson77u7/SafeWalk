package com.example.safewalk

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class RutaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (e: Exception) { e.printStackTrace() }

        val miUbicacion = LatLng(-17.412000, -66.040000)
        val zonaPeligrosa = LatLng(-17.405962, -66.041504)
        val destino = LatLng(-17.395000, -66.045000)

        mMap.addCircle(CircleOptions()
            .center(zonaPeligrosa)
            .radius(180.0)
            .strokeColor(Color.RED)
            .fillColor(0x44FF0000))

        val opcionesRuta = PolylineOptions()
            .add(miUbicacion)
            .add(LatLng(-17.410000, -66.045000))
            .add(LatLng(-17.400000, -66.046000))
            .add(destino)
            .color(Color.parseColor("#10B981"))
            .width(18f)
            .jointType(JointType.ROUND)

        mMap.addPolyline(opcionesRuta)
        mMap.addMarker(MarkerOptions().position(destino).title("Destino Seguro"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f))
    }
}