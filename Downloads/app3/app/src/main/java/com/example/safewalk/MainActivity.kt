package com.example.safewalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var esPremium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // obtener estado premium
        esPremium = intent.getBooleanExtra("premium", false)

        verificarPermisoMicrofono()

        val btnNav = findViewById<CardView>(R.id.btnNav)
        val btnVoice = findViewById<CardView>(R.id.btnSOSManual)
        val btnContacts = findViewById<CardView>(R.id.btnContactos)
        val navZonas = findViewById<LinearLayout>(R.id.navZonas)

        if (esPremium) {
            Toast.makeText(this, "⭐ Usuario Premium", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "👤 Modo invitado", Toast.LENGTH_LONG).show()
        }

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

        // Contactos de confianza
        btnContacts.setOnClickListener {
            val intent = Intent(this, TrustedContactsActivity::class.java)
            startActivity(intent)
        }

        // Zonas seguras
        navZonas.setOnClickListener {
            val intent = Intent(this, ZonasActivity::class.java)
            startActivity(intent)
        }

        // Ejemplo premium (bloqueo futuro)
        if (!esPremium) {
            // aquí puedes desactivar features premium
            // btnPremium.isEnabled = false
        }
    }

    private fun verificarPermisoMicrofono() {
        if (ContextCompat.checkSelfPermission(
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