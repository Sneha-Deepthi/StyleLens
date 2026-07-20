package com.stylelens.app.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.camera.core.ImageAnalysis
import com.stylelens.app.vision.FaceLandmarkerHelper
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.stylelens.app.vision.HandLandmarkerHelper
import java.util.concurrent.Executors

object CameraController {

    fun startCamera(
        context: Context,
        previewView: PreviewView,
        faceLandmarkerHelper: FaceLandmarkerHelper,
        handLandmarkerHelper: HandLandmarkerHelper
    ) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor()
            ) { imageProxy ->

                val width = imageProxy.width
                val height = imageProxy.height

                val rotationDegrees =
                    imageProxy.imageInfo.rotationDegrees

                val frameTime =
                    SystemClock.uptimeMillis()

                try {

                    val buffer =
                        imageProxy.planes[0].buffer

                    buffer.rewind()

                    val bitmap =
                        Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.ARGB_8888
                        )

                    bitmap.copyPixelsFromBuffer(buffer)

                    // Send the SAME camera frame to Face Landmarker.
                    faceLandmarkerHelper.detectBitmap(
                        bitmap = bitmap,
                        rotationDegrees = rotationDegrees,
                        isFrontCamera = true,
                        frameTime = frameTime
                    )

                    // Send the SAME camera frame to Hand Landmarker.
                    handLandmarkerHelper.detectBitmap(
                        bitmap = bitmap,
                        rotationDegrees = rotationDegrees,
                        isFrontCamera = true,
                        frameTime = frameTime
                    )

                } catch (e: Exception) {

                    Log.e(
                        "CameraController",
                        "Error analyzing camera frame",
                        e
                    )

                } finally {

                    // CameraController now owns ImageProxy.
                    // Close every frame exactly once.
                    imageProxy.close()
                }
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