package com.example.safewalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class VoiceEmergencyActivity : AppCompatActivity() {

    private lateinit var txtSpeech: TextView
    private lateinit var btnMic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_emergency)

        txtSpeech = findViewById(R.id.txtSpeech)
        btnMic = findViewById(R.id.btnMic)

        verificarPermisos()

        btnMic.setOnClickListener {
            iniciarReconocimiento()
        }
    }

    private fun verificarPermisos() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    private fun iniciarReconocimiento() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "es-ES"
        )

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            val resultados = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            if (!resultados.isNullOrEmpty()) {

                txtSpeech.text = resultados[0]
            }
        }
    }
}