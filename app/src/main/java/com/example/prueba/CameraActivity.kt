package com.example.prueba

import Producto
import Variante
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.prueba.databinding.ActivityCameraBinding
import com.example.prueba.helpers.ClassifyTf
import com.example.prueba.helpers.ReturnInterpreter
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ingenieriiajhr.jhrCameraX.ImageProxyResponse

class CameraActivity : AppCompatActivity() {

    lateinit var binding: ActivityCameraBinding
    lateinit var cameraJhr: CameraJhr

    lateinit var classifyTf: ClassifyTf

    private val handler = Handler()
    companion object {
        const val INPUT_SIZE = 224
        const val OUTPUT_SIZE = 8
    }

    val classes = arrayOf(Producto(0,"Agua 500ml Members mark", 0, null),
        Producto(1,"Aderezo Cesar Clemente jaques", 50, null),
        Producto(2,"Mostaza French", 20, listOf(Variante("150 gr", 20, false), Variante("250 gr", 30, false), Variante("500 gr", 50, false))),
        Producto(3,"Catsup del monte", 20, null),
        Producto(4,"Normal", 0, null))

    var productosSeleccionados = mutableListOf<Producto>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init cameraJHR
        cameraJhr = CameraJhr(this)

        classifyTf = ClassifyTf(this)

        binding.cameraContinuar.setOnClickListener {
            if (productosSeleccionados.isNotEmpty()){
                val intent = Intent(this, Ticket::class.java)
                intent.putExtra("productosSeleccionados", productosSeleccionados.toTypedArray())
                startActivity(intent)
            }else{
                Toast.makeText(this, "Agregue algún producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted() && !cameraJhr.ifStartCamera){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }
    private fun startCameraJhr() {
        var timeRepeat = System.currentTimeMillis()
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    if (System.currentTimeMillis() > timeRepeat + 2000){
                        classifyImage(bitmap)
                        timeRepeat = System.currentTimeMillis()
                    }
                }
            }
        })
        cameraJhr.initBitmap()
        cameraJhr.initImageProxy()

        cameraJhr.start(1,0,binding.cameraPreview,true,false,true)
    }

    private fun classifyImage(bitmap: Bitmap){
        val bitmapScale = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)

        classifyTf.listenerInterpreter(object: ReturnInterpreter{
            override fun classify(confidence: FloatArray, maxConfidence: Int) {

                runOnUiThread {
                    //binding.txtResult.text = "Resultado: ${classes[maxConfidence]}"
                    val productoSelect = classes.find { it.id == maxConfidence }
                    productoSelect?.let {
                        if (it.id != 4 && it.id != 0) {
                            if (it.variantes != null){
                                mostrarDialogoSeleccion(it)
                            }else{
                                binding.txtResult.visibility = View.VISIBLE
                                binding.txtResult.text = "A G R E G A D O\n${it.nombre}"
                                productosSeleccionados.add(it)
                                handler.postDelayed({
                                    binding.txtResult.visibility = View.GONE
                                }, 2000)
                            }
                        }
                    }
                }
            }
        })

        classifyTf.classify(bitmapScale)

        runOnUiThread {
            binding.imgPreview.setImageBitmap(bitmapScale)
        }
    }


    private fun mostrarDialogoSeleccion(producto: Producto) {

        var selectedOption = -1
        val variantesArray = producto.variantes?.map { it.nombre }?.toTypedArray() ?: arrayOf()

        AlertDialog.Builder(this)
            .setTitle("Selecciona una variante para ${producto.nombre}")
            .setSingleChoiceItems(variantesArray, -1) { dialog, which ->
                selectedOption = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                if (selectedOption != -1 && selectedOption < variantesArray.size) {
                    val productoClonado = clonarProductoConVarianteSeleccionada(producto, selectedOption)
                    productosSeleccionados.add(productoClonado)
                } else {
                    Toast.makeText(this, "No se seleccionó ninguna variante", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    fun clonarProductoConVarianteSeleccionada(producto: Producto, selectedOption: Int): Producto {
        // Clonar variantes
        val variantesClonadas = producto.variantes?.map {
            Variante(it.nombre, it.precio, it.seleccionada)
        }?.toMutableList()

        // Marcar la variante seleccionada
        variantesClonadas?.forEachIndexed { index, variante ->
            variante.seleccionada = (index == selectedOption)
        }

        // Clonar el producto con las variantes clonadas
        return Producto(
            id = producto.id,
            nombre = producto.nombre,
            precio = producto.precio,
            variantes = variantesClonadas
        )
    }


}
