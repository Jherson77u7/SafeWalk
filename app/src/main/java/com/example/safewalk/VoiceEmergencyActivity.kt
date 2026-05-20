package com.example.safewalk

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class VoiceEmergencyActivity : AppCompatActivity() {

    // Speech Recognition
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent

    private lateinit var btnBack: ImageButton
    // UI
    private lateinit var txtSpeech: TextView
    private lateinit var txtStatus: TextView
    private lateinit var btnMic: ImageButton

    // Barras de audio
    private lateinit var bars: List<View>

    // Estado
    private var escuchando = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_emergency)

        // Referencias UI
        txtSpeech = findViewById(R.id.txtSpeech)
        txtStatus = findViewById(R.id.txtStatus)
        btnMic = findViewById(R.id.btnMic)
        btnBack = findViewById(R.id.btnBack)

        // Barras visuales
        bars = listOf(
            findViewById(R.id.bar1),
            findViewById(R.id.bar2),
            findViewById(R.id.bar3),
            findViewById(R.id.bar4),
            findViewById(R.id.bar5)
        )

        // Animación del micrófono
        val pulse =
            AnimationUtils.loadAnimation(this, R.anim.pulse)

        btnMic.startAnimation(pulse)

        // Configurar SpeechRecognizer
        configurarSpeechRecognizer()

        // Botón micrófono ON/OFF
        btnMic.setOnClickListener {

            if (!escuchando) {

                iniciarEscucha()

            } else {

                detenerEscucha()
            }
        }
        //salir del microfono
        btnBack.setOnClickListener {

            finish()
        }
    }

    private fun configurarSpeechRecognizer() {

        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this)

        speechIntent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "es-BO"
        )

        speechIntent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        )

        speechRecognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {

                    txtStatus.text = "🎤 Escuchando entorno..."
                }

                override fun onBeginningOfSpeech() {

                    txtStatus.text = "🧠 Analizando voz..."
                }

                override fun onRmsChanged(rmsdB: Float) {

                    // Animar barras de audio
                    animarBarras()
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {

                    txtStatus.text = "Procesando..."
                }

                override fun onError(error: Int) {

                    txtStatus.text = "Reconectando micrófono..."

                    if (escuchando) {

                        speechRecognizer.startListening(speechIntent)
                    }
                }

                override fun onResults(results: Bundle?) {

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    if (!matches.isNullOrEmpty()) {

                        val texto = matches[0]

                        txtSpeech.text = texto

                        detectarPalabrasClave(texto)
                    }

                    // Escucha continua
                    if (escuchando) {

                        speechRecognizer.startListening(speechIntent)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {

                    val partial =
                        partialResults?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    if (!partial.isNullOrEmpty()) {

                        txtSpeech.text = partial[0]
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            }
        )
    }

    private fun iniciarEscucha() {

        escuchando = true

        txtStatus.text = "🎤 SafeWalk activo"

        btnMic.setImageResource(R.drawable.ic_mic_on)

        speechRecognizer.startListening(speechIntent)

        Toast.makeText(
            this,
            "SafeWalk escuchando...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun detenerEscucha() {

        escuchando = false

        txtStatus.text = "Micrófono apagado"

        btnMic.setImageResource(R.drawable.ic_mic_off)

        speechRecognizer.stopListening()

        Toast.makeText(
            this,
            "SafeWalk detenido",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun detectarPalabrasClave(texto: String) {

        val textoLower = texto.lowercase()

        // ALERTA AYUDA
        if (textoLower.contains("ayuda")) {

            Toast.makeText(
                this,
                "⚠ ALERTA DETECTADA",
                Toast.LENGTH_LONG
            ).show()

            llamarContactoEmergencia()
        }

        // RUTA SEGURA
        if (textoLower.contains("ruta segura")) {

            txtStatus.text =
                "🗺 Buscando ruta segura..."

            Toast.makeText(
                this,
                "🗺 Analizando zona",
                Toast.LENGTH_LONG
            ).show()
        }

        // PELIGRO
        if (
            textoLower.contains("me siguen") ||
            textoLower.contains("peligro") ||
            textoLower.contains("robo")
        ) {

            txtStatus.text =
                "🚨 POSIBLE PELIGRO DETECTADO"

            Toast.makeText(
                this,
                "🚨 Activando protocolo SafeWalk",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun animarBarras() {

        for (bar in bars) {

            val alturaRandom =
                Random.nextInt(40, 180)

            val animator =
                ValueAnimator.ofInt(
                    bar.height,
                    alturaRandom
                )

            animator.duration = 250

            animator.addUpdateListener {

                val value =
                    it.animatedValue as Int

                val params =
                    bar.layoutParams

                params.height = value

                bar.layoutParams = params
            }

            animator.start()
        }
    }

    private fun llamarContactoEmergencia() {

        val prefs =
            getSharedPreferences(
                "SafeWalkContacts",
                MODE_PRIVATE
            )

        var numeroEmergencia: String? = null

        for (i in 1..3) {

            val numero =
                prefs.getString(
                    "contacto_${i}_numero",
                    null
                )

            if (!numero.isNullOrEmpty()) {

                numeroEmergencia = numero
                break
            }
        }

        // Si no hay contactos -> 911
        if (numeroEmergencia == null) {

            numeroEmergencia = "911"
        }

        Toast.makeText(
            this,
            "🚨 Llamando a $numeroEmergencia",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:$numeroEmergencia")
        )

        startActivity(intent)
    }

    override fun onDestroy() {

        super.onDestroy()

        speechRecognizer.destroy()
    }
}