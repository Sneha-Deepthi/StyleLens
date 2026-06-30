package com.stylelens.app.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.camera.core.ImageAnalysis
import java.util.concurrent.Executors

object CameraController {

    fun startCamera(
        context: Context,
        previewView: PreviewView
    ) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()

            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor()
            ) { imageProxy ->

                // We'll process this frame using MediaPipe
                imageProxy.close()

            }

            preview.surfaceProvider =
                previewView.surfaceProvider

            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA

            val lifecycleOwner =
                previewView.findViewTreeLifecycleOwner()
                    ?: return@addListener

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(context))

    }

}