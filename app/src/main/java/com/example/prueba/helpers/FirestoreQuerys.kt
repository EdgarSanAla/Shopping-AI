package com.example.prueba.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreQuerys {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun iniciarSesion(email: String, password: String, callback: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(null)
            }
            .addOnFailureListener { e ->
                callback(e.localizedMessage)
            }
    }

    fun registrarUsuario(email: String, password: String, callback: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser?.uid // Obtiene el ID del usuario registrado
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }

    fun guardarUsuario(userId: String, usuario: HashMap<String, String>, callback: (Boolean, String?) -> Unit) {
        db.collection("Usuarios")
            .document(userId)
            .set(usuario)
            .addOnSuccessListener { documentReference ->
                callback(true, null) // Envía true al callback si la operación fue exitosa
            }
            .addOnFailureListener { e ->
                callback(false, e.localizedMessage) // Envía false al callback si hubo un error en la operación
            }
    }

}