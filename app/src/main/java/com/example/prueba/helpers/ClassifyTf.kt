package com.example.prueba.helpers

import android.content.Context
import android.graphics.Bitmap
import com.example.prueba.CameraActivity
import com.example.prueba.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClassifyTf(val context: Context) {

    val model = ModelUnquant.newInstance(context)

    lateinit var returnInterpreter: ReturnInterpreter

    fun listenerInterpreter(returnInterpreter: ReturnInterpreter){
        this.returnInterpreter = returnInterpreter
    }

    fun classify(bitmap: Bitmap) {
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, CameraActivity.INPUT_SIZE, CameraActivity.INPUT_SIZE, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * CameraActivity.INPUT_SIZE * CameraActivity.INPUT_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValue = IntArray(CameraActivity.INPUT_SIZE * CameraActivity.INPUT_SIZE)

        bitmap.getPixels(intValue, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in intValue){
            byteBuffer.putFloat((pixelValue shr 16 and 0XFF) * (1f/255))
            byteBuffer.putFloat((pixelValue shr 8 and 0XFF) * (1f/255))
            byteBuffer.putFloat((pixelValue and 0XFF) * (1f/255))
        }


        inputFeature.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature)
        val outputFeature = outputs.outputFeature0AsTensorBuffer
        val confidence = outputFeature.floatArray
/*
        confidence 0 Sopa Fideo
        confidence 1 Desodorante Old Spice
        confidence 2 Vualá Roll
        confidence 3 Sopa Fideo y Desodorante
        confidence 4 Sopa Fideo y Vualá Roll
        confidence 5 Desodorante y Vualá Roll
        confidence 6 tres
        confidence 7 normal
*/

        val maxPos = confidence.indices.maxByOrNull { confidence[it] } ?: 0

        returnInterpreter.classify(confidence, maxPos)
    }

}