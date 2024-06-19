package com.example.prueba

import Producto
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prueba.databinding.ActivityCameraBinding
import com.example.prueba.databinding.ActivityMainBinding
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

    val classes = arrayOf(Producto(0,"Agua 500ml Members mark", 0),
        Producto(1,"Aderezo Cesar Clemente jaques", 50),
        Producto(2,"Mostaza French", 20),
        Producto(3,"Catsup del monte", 20),
        Producto(4,"Normal", 0))

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
        })

        classifyTf.classify(bitmapScale)

        runOnUiThread {
            binding.imgPreview.setImageBitmap(bitmapScale)
        }
    }
}
