package com.example.prueba

import Producto
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductosAdapter(
    private val productos: MutableList<Producto>,
    private val onProductoEliminar: (Int) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.productoNombre.text = producto.nombre
        holder.productoPrecio.text = "$ ${producto.precio}"

        producto.variantes?.let {variantes ->
            val variante = variantes.find { it.seleccionada ?: false }
            if (variante != null) {
                holder.productoNombre.text = "${producto.nombre} ${variante.nombre}"
                holder.productoPrecio.text = "$ ${variante.precio}"
            }
        }
        holder.productoQuitar.setOnClickListener {
            onProductoEliminar(position)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productoNombre: TextView = itemView.findViewById(R.id.productoNombre)
        val productoPrecio: TextView = itemView.findViewById(R.id.productoPrecio)
        val productoQuitar: ImageView = itemView.findViewById(R.id.productoQuitar)
    }
}
