package com.example.prueba

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prueba.databinding.ActivityCameraBinding
import com.example.prueba.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ingenieriiajhr.jhrCameraX.ImageProxyResponse

class CameraActivity : AppCompatActivity() {

    lateinit var binding: ActivityCameraBinding
    lateinit var cameraJhr: CameraJhr

    companion object {
        const val INPUT_SIZE = 224
        const val OUTPUT_SIZE = 3 // Número de etiquetas de salida
    }

    val classes = arrayOf("IRON", "PELOTICA", "NORMAL")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init cameraJHR
        cameraJhr = CameraJhr(this)

        //classifyImageTf = ClassifyImageTf(this)
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
                    runOnUiThread {
                        binding.imgPreview.setImageBitmap(bitmap)
                    }

                    /*
                    if (System.currentTimeMillis()>timeRepeat+1000){
                        classifyImage(bitmap)
                        timeRepeat = System.currentTimeMillis()
                    }
*/
                }
            }
        })
        cameraJhr.initBitmap()
        cameraJhr.initImageProxy()

        cameraJhr.start(1,0,binding.cameraPreview,true,false,true)
    }

}