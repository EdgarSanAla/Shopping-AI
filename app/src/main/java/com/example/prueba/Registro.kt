package com.example.prueba

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.prueba.helpers.FirestoreQuerys
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class Registro : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        FirebaseApp.initializeApp(applicationContext)

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val editTextCorreo = findViewById<EditText>(R.id.editTextCorreo)
        val editTextContrasena = findViewById<EditText>(R.id.editTextContrasena)
        val editTextConfirmarContrasena = findViewById<EditText>(R.id.editTextConfirmarContrasena)
        val buttonEnviar = findViewById<Button>(R.id.buttonEnviar)

        buttonEnviar.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val apellidos = editTextApellidos.text.toString()
            val correo = editTextCorreo.text.toString()
            val contrasena = editTextContrasena.text.toString()
            val confirmarContrasena = editTextConfirmarContrasena.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarYGuardarUsuario(correo, contrasena, nombre, apellidos, correo)
        }
    }
    private fun registrarYGuardarUsuario(email: String, password: String, nombre: String, apellidos: String, correo: String) {
        registrarUsuario(email, password) { user ->
            if (user != null) {
                val usuario = hashMapOf(
                    "id" to user,
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "correo" to correo
                )

                guardarUsuario(user, usuario) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                        entrar()
                    } else {
                        Toast.makeText(this, "Error al agregar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registrarUsuario(email: String, password: String, callback: (String?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser?.uid
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }

    fun guardarUsuario(userId: String, usuario: HashMap<String, String>, callback: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection("Usuarios")
            .document(userId)
            .set(usuario)
            .addOnSuccessListener { documentReference ->
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }

    fun entrar() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}