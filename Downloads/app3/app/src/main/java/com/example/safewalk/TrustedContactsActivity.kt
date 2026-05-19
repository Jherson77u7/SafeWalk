package com.example.safewalk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.provider.ContactsContract
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TrustedContactsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnAdd: Button
    private lateinit var contactsContainer: LinearLayout

    private var cantidadContactos = 0
    private val maxContactos = 3

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trusted_contacts)

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                10
            )
        }

        btnBack = findViewById(R.id.btnBackContacts)
        btnAdd = findViewById(R.id.btnAddContact)
        contactsContainer = findViewById(R.id.contactsContainer)

        cargarContactosGuardados()

        btnBack.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {

            if (cantidadContactos >= maxContactos) {

                Toast.makeText(
                    this,
                    "Máximo 3 contactos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            abrirContactos()
        }
    }

    private fun agregarContacto(
        nombre: String,
        telefono: String
    ) {

        val indice = cantidadContactos + 1

        val prefs =
            getSharedPreferences(
                "SafeWalkContacts",
                MODE_PRIVATE
            )

        prefs.edit()
            .putString(
                "contacto_${indice}_nombre",
                nombre
            )
            .putString(
                "contacto_${indice}_numero",
                telefono
            )
            .apply()

        agregarContactoUI(
            nombre,
            telefono,
            indice
        )

        cantidadContactos++
    }

    private fun abrirContactos() {

        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )

        startActivityForResult(intent, 200)
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {

            val contactUri = data?.data ?: return

            val cursor = contentResolver.query(
                contactUri,
                null,
                null,
                null,
                null
            )

            cursor?.moveToFirst()

            val nombreIndex =
                cursor?.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

            val numeroIndex =
                cursor?.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

            val nombre =
                cursor?.getString(nombreIndex ?: 0)

            val numero =
                cursor?.getString(numeroIndex ?: 0)

            if (nombre != null && numero != null) {

                agregarContacto(nombre, numero)
            }

            cursor?.close()
        }
    }

    private fun cargarContactosGuardados() {

        val prefs =
            getSharedPreferences(
                "SafeWalkContacts",
                MODE_PRIVATE
            )

        for (i in 1..3) {

            val nombre =
                prefs.getString(
                    "contacto_${i}_nombre",
                    null
                )

            val numero =
                prefs.getString(
                    "contacto_${i}_numero",
                    null
                )

            if (
                !nombre.isNullOrEmpty() &&
                !numero.isNullOrEmpty()
            ) {

                agregarContactoUI(
                    nombre,
                    numero,
                    i
                )

                cantidadContactos++
            }
        }
    }

    private fun agregarContactoUI(
        nombre: String,
        telefono: String,
        indice: Int
    ) {

        val contactView = layoutInflater.inflate(
            R.layout.item_contact,
            null
        )

        val txtName =
            contactView.findViewById<TextView>(R.id.txtContactName)

        val txtPhone =
            contactView.findViewById<TextView>(R.id.txtContactPhone)

        val btnCall =
            contactView.findViewById<Button>(R.id.btnCall)

        val btnDelete =
            contactView.findViewById<ImageButton>(R.id.btnDelete)

        txtName.text = nombre
        txtPhone.text = telefono

        // LLAMADA AUTOMÁTICA
        btnCall.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:$telefono")
            )

            startActivity(intent)
        }

        // ELIMINAR
        btnDelete.setOnClickListener {

            val prefs =
                getSharedPreferences(
                    "SafeWalkContacts",
                    MODE_PRIVATE
                )

            prefs.edit()
                .remove("contacto_${indice}_nombre")
                .remove("contacto_${indice}_numero")
                .apply()

            contactsContainer.removeView(contactView)

            cantidadContactos--

            Toast.makeText(
                this,
                "Contacto eliminado",
                Toast.LENGTH_SHORT
            ).show()
        }

        contactsContainer.addView(contactView)
    }

}