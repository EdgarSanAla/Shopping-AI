package com.example.prueba

import Producto
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Ticket : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextview: TextView
    private lateinit var adapter: ProductosAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        totalTextview = findViewById(R.id.ticket_total)
        recyclerView = findViewById(R.id.myRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val productosSeleccionados = intent.getParcelableArrayExtra("productosSeleccionados")?.mapNotNull {
            it as? Producto
        } ?: emptyList()

        adapter = ProductosAdapter(productosSeleccionados)
        recyclerView.adapter = adapter

        val total = productosSeleccionados.sumBy { it.precio }
        totalTextview.text = "Total: ${total}"

        findViewById<Button>(R.id.ticket_volver).setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}