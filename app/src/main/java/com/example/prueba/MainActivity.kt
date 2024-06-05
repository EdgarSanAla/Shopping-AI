package com.example.prueba

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.prueba.helpers.FirestoreQuerys
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val editTextCorreo = findViewById<EditText>(R.id.main_email)
        val editTextContrasena = findViewById<EditText>(R.id.main_password)

        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Cargando...")
        progressDialog?.setCancelable(false)

        findViewById<Button>(R.id.main_btnEntrar).setOnClickListener {
            progressDialog?.show()
            val correo = editTextCorreo.text.toString()
            val contrasena = editTextContrasena.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                progressDialog?.dismiss()
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                progressDialog?.dismiss()
                Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirestoreQuerys().iniciarSesion(correo, contrasena) { msg ->
                progressDialog?.dismiss()
                if (msg != null) {
                    Toast.makeText(this, "${msg!!}", Toast.LENGTH_SHORT).show()
                    return@iniciarSesion
                }
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.main_btnRegistro).setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

    }
}