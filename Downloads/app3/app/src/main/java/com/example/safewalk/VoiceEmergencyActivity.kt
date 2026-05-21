package com.example.safewalk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class VoiceEmergencyActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent

    private lateinit var txtSpeech: TextView
    private lateinit var txtStatus: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnBack: ImageButton

    private lateinit var bars: List<View>

    private lateinit var btnRespondio: Button
    private lateinit var btnNoRespondio: Button

    private var escuchando = false
    private var sosActivo = false
    private var listaContactos = listOf<String>()
    private var indexActual = 0
    private var contactoActual = ""
    private var esperandoRespuesta = false
    private var bloqueoSOS = false   // 🔥 FIX ANTI-SPAM

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_emergency)

        txtSpeech = findViewById(R.id.txtSpeech)
        txtStatus = findViewById(R.id.txtStatus)
        btnMic = findViewById(R.id.btnMic)
        btnBack = findViewById(R.id.btnBack)

        btnRespondio = findViewById(R.id.btnRespondio)
        btnNoRespondio = findViewById(R.id.btnNoRespondio)

        bars = listOf(
            findViewById(R.id.bar1),
            findViewById(R.id.bar2),
            findViewById(R.id.bar3),
            findViewById(R.id.bar4),
            findViewById(R.id.bar5)
        )

        btnRespondio.visibility = View.GONE
        btnNoRespondio.visibility = View.GONE

        configurarSpeech()

        btnMic.setOnClickListener {
            if (escuchando) detenerEscucha()
            else iniciarEscucha()
        }

        btnBack.setOnClickListener {
            detenerSOS()
            finish()
        }

        btnRespondio.setOnClickListener { responder() }
        btnNoRespondio.setOnClickListener { noResponder() }
    }

    // ---------------- DETECCIÓN ----------------

    private fun detectar(texto: String) {

        if (bloqueoSOS) return   // 🔥 evita duplicados

        if (texto.contains("ayuda") || texto.contains("peligro") ||
            texto.contains("robo") || texto.contains("me siguen")) {

            iniciarSOS()
        }
    }

    // ---------------- SOS ----------------

    private fun iniciarSOS() {

        if (sosActivo) return

        sosActivo = true
        bloqueoSOS = true  // 🔥 activa bloqueo
        indexActual = 0

        val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

        val numeros = (1..3).mapNotNull {
            prefs.getString("contacto_${it}_numero", null)
        }

        listaContactos = if (numeros.isNotEmpty()) numeros else listOf("911")

        llamarSiguiente()
    }

    private fun llamarSiguiente() {

        if (!sosActivo) return
        if (esperandoRespuesta) return

        if (indexActual >= listaContactos.size) {
            txtStatus.text = "🚨 Llamando 911"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:911")))
            return
        }

        contactoActual = listaContactos[indexActual]

        txtStatus.text = "📞 Llamando a $contactoActual"

        esperandoRespuesta = true

        btnRespondio.visibility = View.VISIBLE
        btnNoRespondio.visibility = View.VISIBLE

        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$contactoActual")))
    }

    // 🔥 IMPORTANTE: avanzar contacto aquí
    private fun noResponder() {

        if (!esperandoRespuesta) return

        btnRespondio.visibility = View.GONE
        btnNoRespondio.visibility = View.GONE

        esperandoRespuesta = false
        indexActual++   // ✔ FIX CLAVE

        handler.postDelayed({
            llamarSiguiente()
        }, 1200)
    }

    private fun responder() {

        if (!esperandoRespuesta) return

        detenerSOS()

        Toast.makeText(this, "Contacto respondió", Toast.LENGTH_SHORT).show()
    }

    private fun detenerSOS() {

        sosActivo = false
        esperandoRespuesta = false
        bloqueoSOS = false  // 🔥 desbloquea

        btnRespondio.visibility = View.GONE
        btnNoRespondio.visibility = View.GONE

        handler.removeCallbacksAndMessages(null)

        txtStatus.text = "🛑 SOS detenido"
    }
    private fun configurarSpeech() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-BO")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                txtStatus.text = "🎤 Escuchando..."
            }

            override fun onBeginningOfSpeech() {
                txtStatus.text = "🧠 Detectando voz..."
            }

            private var lastAnim = 0L

            override fun onRmsChanged(rmsdB: Float) {
                val now = System.currentTimeMillis()
                if (now - lastAnim > 120) {
                    animarBarras()
                    lastAnim = now
                }
            }

            override fun onEndOfSpeech() {
                txtStatus.text = "Procesando..."
            }

            override fun onError(error: Int) {
                if (escuchando) {
                    speechRecognizer.cancel()
                    speechRecognizer.startListening(speechIntent)
                }
            }

            override fun onResults(results: Bundle?) {

                val texto = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.lowercase() ?: ""

                txtSpeech.text = texto
                detectar(texto)

                if (escuchando) {
                    speechRecognizer.startListening(speechIntent)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun iniciarEscucha() {
        escuchando = true
        txtStatus.text = "🎤 SafeWalk activo"
        btnMic.setImageResource(R.drawable.ic_mic_on)

        try {
            speechRecognizer.startListening(speechIntent)
        } catch (e: Exception) {
            speechRecognizer.cancel()
            speechRecognizer.startListening(speechIntent)
        }
    }

    private fun detenerEscucha() {
        escuchando = false
        txtStatus.text = "Micrófono apagado"
        btnMic.setImageResource(R.drawable.ic_mic_off)

        speechRecognizer.stopListening()
    }

    private fun animarBarras()
    {
        for (bar in bars)
        {
            val altura = Random.nextInt(40, 180)
            val anim = android.animation.ValueAnimator.ofInt(bar.height, altura)
            anim.duration = 200
            anim.addUpdateListener{ bar.layoutParams.height = it.animatedValue as Int
                bar.requestLayout() }
            anim.start()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        handler.removeCallbacksAndMessages(null)
    }
}