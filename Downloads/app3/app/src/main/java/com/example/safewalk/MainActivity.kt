package com.example.safewalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Detectar si es premium
    private var esPremium = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener rol premium
        esPremium = intent.getBooleanExtra(
            "premium",
            false
        )

        // Permiso micrófono
        verificarPermisoMicrofono()

        // Mostrar tipo usuario
        if (esPremium) {

            Toast.makeText(
                this,
                "⭐ Usuario Premium",
                Toast.LENGTH_LONG
            ).show()

        } else {

            Toast.makeText(
                this,
                "👤 Modo invitado",
                Toast.LENGTH_LONG
            ).show()
        }

        // Botones
        val btnNav =
            findViewById<CardView>(R.id.btnNav)

        val btnVoice =
            findViewById<CardView>(R.id.btnSOSManual)

        // Ruta segura
        btnNav.setOnClickListener {

            val intent =
                Intent(this, RutaActivity::class.java)

            startActivity(intent)
        }

        // Emergencia por voz
        btnVoice.setOnClickListener {

            val intent =
                Intent(
                    this,
                    VoiceEmergencyActivity::class.java
                )

            startActivity(intent)
        }

        // EJEMPLO PREMIUM
        // Aquí luego puedes desbloquear funciones
        if (!esPremium) {

            // Ejemplo:
            // btnPremium.isEnabled = false
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
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),
                100
            )
        }
    }
}