package com.example.prueba

import Producto
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Ticket : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var adapter: ProductosAdapter
    private var productosSeleccionadosMutable: MutableList<Producto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        totalTextView = findViewById(R.id.ticket_total)
        recyclerView = findViewById(R.id.myRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar productosSeleccionadosMutable con los productos recibidos
        val productosSeleccionados = intent.getParcelableArrayExtra("productosSeleccionados")?.mapNotNull {
            it as? Producto
        } ?: emptyList()
        productosSeleccionadosMutable = productosSeleccionados.toMutableList()

        // Configurar el adaptador con la lista mutable
        adapter = ProductosAdapter(productosSeleccionadosMutable) { position ->
            mostrarDialogoConfirmacion(position)
        }
        recyclerView.adapter = adapter

        // Calcular y mostrar el total inicial
        actualizarTotal()

        findViewById<Button>(R.id.ticket_volver).setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarDialogoConfirmacion(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmación")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setPositiveButton("Sí") { dialog, _ ->
                eliminarProducto(position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun eliminarProducto(position: Int) {
        if (position < productosSeleccionadosMutable.size) {
            productosSeleccionadosMutable.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, productosSeleccionadosMutable.size)
            actualizarTotal()
        }
    }

    private fun actualizarTotal() {
        val total = productosSeleccionadosMutable.sumBy { producto ->
            producto.variantes?.find { it.seleccionada == true }?.precio ?: producto.precio
        }
        totalTextView.text = "Total: $${total}"
    }

}
