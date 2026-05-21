package com.example.safewalk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.safewalk.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto asocia el código con el diseño del logo que creaste
        setContentView(R.layout.activity_splash)

        // Lógica del temporizador: 2000 milisegundos = 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Crea la "intención" de ir desde esta pantalla a la principal (MainActivity)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finaliza el Splash para que si el usuario da "atrás", no vuelva al logo
            finish()
        }, 2000)
    }
}