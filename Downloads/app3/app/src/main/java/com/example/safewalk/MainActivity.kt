package com.example.safewalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verificarPermisoMicrofono()

        val btnNav = findViewById<CardView>(R.id.btnNav)
        val btnVoice = findViewById<CardView>(R.id.btnSOSManual)
        val navZonas = findViewById<LinearLayout>(R.id.navZonas) // Asegúrate de que el ID coincida en el XML
        // Ruta segura

        btnNav.setOnClickListener {

            val intent = Intent(this, RutaActivity::class.java)
            startActivity(intent)
        }

        btnVoice.setOnClickListener {

            val intent = Intent(this, VoiceEmergencyActivity::class.java)
            startActivity(intent)
        }

        navZonas.setOnClickListener {
            val intent = Intent(this, ZonasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verificarPermisoMicrofono() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }
    }
}