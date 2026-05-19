package com.example.safewalk

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNav = findViewById<CardView>(R.id.btnNav)
        val btnVoice = findViewById<CardView>(R.id.btnSOSManual)
        val navZonas = findViewById<LinearLayout>(R.id.navZonas) // Asegúrate de que el ID coincida en el XML
        // Ruta segura
        btnNav.setOnClickListener {

            val intent = Intent(this, RutaActivity::class.java)
            startActivity(intent)
        }

        // Emergencia por voz
        btnVoice.setOnClickListener {

            val intent = Intent(this, VoiceEmergencyActivity::class.java)
            startActivity(intent)
        }

        navZonas.setOnClickListener {
            val intent = Intent(this, ZonasActivity::class.java)
            startActivity(intent)
        }
    }
}