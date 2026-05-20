package com.example.safewalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUser: EditText
    private lateinit var edtPass: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnGuest: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUser = findViewById(R.id.edtUser)
        edtPass = findViewById(R.id.edtPass)

        btnLogin = findViewById(R.id.btnLogin)
        btnGuest = findViewById(R.id.btnGuest)

        btnLogin.setOnClickListener {

            validarLogin()
        }

        btnGuest.setOnClickListener {

            val intent =
                Intent(this, MainActivity::class.java)

            intent.putExtra("premium", false)

            startActivity(intent)

            finish()
        }
    }

    private fun validarLogin() {

        val usuario =
            edtUser.text.toString()

        val password =
            edtPass.text.toString()

        // ADMIN 1
        if (
            usuario == "admin1" &&
            password == "1234"
        ) {

            entrarPremium()

            return
        }

        // ADMIN 2
        if (
            usuario == "admin2" &&
            password == "5678"
        ) {

            entrarPremium()

            return
        }

        Toast.makeText(
            this,
            "Credenciales incorrectas",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun entrarPremium() {

        val intent =
            Intent(this, MainActivity::class.java)

        intent.putExtra("premium", true)

        startActivity(intent)

        finish()
    }
}