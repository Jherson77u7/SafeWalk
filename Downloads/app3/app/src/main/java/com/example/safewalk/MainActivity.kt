package com.example.safewalk

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView // Para que reconozca el CardView
import com.example.safewalk.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNav = findViewById<CardView>(R.id.btnNav)
        val btnSOS = findViewById<CardView>(R.id.btnSOSManual)

        // Ir a Pantalla de Ruta Segura
        btnNav.setOnClickListener {
            val intent = Intent(this, RutaActivity::class.java)
            startActivity(intent)
        }

        // Simular alerta
        btnSOS.setOnClickListener {
            Toast.makeText(this, "SOS ACTIVADO", Toast.LENGTH_SHORT).show()
        }
        // Aquí simularemos que el sistema de voz está activo
        iniciarEscuchaVoz()
    }

    private fun iniciarEscuchaVoz() {
        // En la siguiente fase aquí pondremos el SpeechRecognizer
        // Por ahora, solo mostramos que la app está lista.
        Toast.makeText(this, "Sistema SafeWalk Activo en Cochabamba", Toast.LENGTH_SHORT).show()
    }

    private fun enviarAlertaEmergencia(tipo: String) {
        // Esta función enviará el SMS y GPS
        Toast.makeText(this, "¡ALERTA $tipo ENVIADA A POLICÍA Y CONTACTOS!", Toast.LENGTH_LONG).show()
    }
}