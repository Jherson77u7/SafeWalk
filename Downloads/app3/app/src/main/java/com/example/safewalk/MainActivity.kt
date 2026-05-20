package com.example.safewalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var esPremium = false

    private lateinit var tvContactosCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        esPremium = intent.getBooleanExtra("premium", false)

        initViews()
        verificarPermisoMicrofono()
        setupListeners()
        actualizarContadorContactos()

        mostrarEstadoUsuario()
    }

    // ---------------- INIT ----------------

    private fun initViews() {
        tvContactosCount = findViewById(R.id.tvContactosCount)
    }

    // ---------------- UI STATE ----------------

    private fun mostrarEstadoUsuario() {
        Toast.makeText(
            this,
            if (esPremium) "⭐ Usuario Premium" else "👤 Modo invitado",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------- LISTENERS ----------------

    private fun setupListeners() {

        findViewById<CardView>(R.id.btnNav).setOnClickListener {
            startActivity(Intent(this, RutaActivity::class.java))
        }

        findViewById<CardView>(R.id.btnSOSManual).setOnClickListener {
            startActivity(Intent(this, VoiceEmergencyActivity::class.java))
        }

        findViewById<CardView>(R.id.btnContactos).setOnClickListener {
            startActivity(Intent(this, TrustedContactsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navZonas).setOnClickListener {
            startActivity(Intent(this, ZonasActivity::class.java))
        }
    }

    // ---------------- CONTACTOS ----------------

    private fun actualizarContadorContactos() {
        val cantidad = obtenerCantidadContactos()
        tvContactosCount.text = "Contactos: $cantidad/3"
    }

    private fun obtenerCantidadContactos(): Int {
        val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

        var count = 0

        for (i in 1..3) {
            val nombre = prefs.getString("contacto_${i}_nombre", null)
            val numero = prefs.getString("contacto_${i}_numero", null)

            if (!nombre.isNullOrEmpty() && !numero.isNullOrEmpty()) {
                count++
            }
        }

        return count
    }

    // ---------------- PERMISSIONS ----------------

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

    // ---------------- LIFECYCLE ----------------

    override fun onResume() {
        super.onResume()
        actualizarContadorContactos()
    }
}