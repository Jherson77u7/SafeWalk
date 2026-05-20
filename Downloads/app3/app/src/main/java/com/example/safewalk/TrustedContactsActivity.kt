package com.example.safewalk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class TrustedContactsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnAdd: Button
    private lateinit var contactsContainer: LinearLayout

    private var cantidadContactos = 0
    private val maxContactos = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trusted_contacts)

        solicitarPermiso()

        btnBack = findViewById(R.id.btnBackContacts)
        btnAdd = findViewById(R.id.btnAddContact)
        contactsContainer = findViewById(R.id.contactsContainer)

        cargarContactosGuardados()

        btnBack.setOnClickListener { finish() }

        btnAdd.setOnClickListener {
            if (cantidadContactos >= maxContactos) {
                Toast.makeText(this, "Máximo 3 contactos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            abrirContactos()
        }
    }

    // ---------------- PERMISO ----------------

    private fun solicitarPermiso() {
        if (ContextCompat.checkSelfPermission(
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
    }

    // ---------------- CONTACTO PICK ----------------

    private fun abrirContactos() {
        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {

            val uri = data?.data ?: return

            val cursor = contentResolver.query(uri, null, null, null, null)

            cursor?.use {

                if (!it.moveToFirst()) return

                val nombreIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

                val numeroIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val nombre = it.getString(nombreIndex)
                val numero = it.getString(numeroIndex)

                if (!nombre.isNullOrEmpty() && !numero.isNullOrEmpty()) {
                    guardarContacto(nombre, numero)
                }
            }
        }
    }

    // ---------------- GUARDAR ----------------

    private fun guardarContacto(nombre: String, telefono: String) {

        val slot = obtenerSlotLibre()

        if (slot == -1) {
            Toast.makeText(this, "No hay espacio", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

        prefs.edit()
            .putString("contacto_${slot}_nombre", nombre)
            .putString("contacto_${slot}_numero", telefono)
            .apply()

        agregarContactoUI(nombre, telefono, slot)

        cantidadContactos++
    }

    // busca hueco libre real
    private fun obtenerSlotLibre(): Int {

        val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

        for (i in 1..3) {
            val nombre = prefs.getString("contacto_${i}_nombre", null)
            val numero = prefs.getString("contacto_${i}_numero", null)

            if (nombre.isNullOrEmpty() || numero.isNullOrEmpty()) {
                return i
            }
        }

        return -1
    }

    // ---------------- LOAD ----------------

    private fun cargarContactosGuardados() {

        val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

        cantidadContactos = 0
        contactsContainer.removeAllViews()

        for (i in 1..3) {

            val nombre = prefs.getString("contacto_${i}_nombre", null)
            val numero = prefs.getString("contacto_${i}_numero", null)

            if (!nombre.isNullOrEmpty() && !numero.isNullOrEmpty()) {
                agregarContactoUI(nombre, numero, i)
                cantidadContactos++
            }
        }
    }

    // ---------------- UI ----------------

    private fun agregarContactoUI(nombre: String, telefono: String, indice: Int) {

        val view = layoutInflater.inflate(R.layout.item_contact, null)

        val txtName = view.findViewById<TextView>(R.id.txtContactName)
        val txtPhone = view.findViewById<TextView>(R.id.txtContactPhone)
        val btnCall = view.findViewById<Button>(R.id.btnCall)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        txtName.text = nombre
        txtPhone.text = telefono

        btnCall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$telefono")))
        }

        btnDelete.setOnClickListener {

            val prefs = getSharedPreferences("SafeWalkContacts", MODE_PRIVATE)

            prefs.edit()
                .remove("contacto_${indice}_nombre")
                .remove("contacto_${indice}_numero")
                .apply()

            contactsContainer.removeView(view)
            cantidadContactos--

            Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
        }

        contactsContainer.addView(view)
    }
}