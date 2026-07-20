package com.stylelens.app.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    private val context: Context,
    private val runningMode: RunningMode = RunningMode.LIVE_STREAM,
    private val listener: LandmarkerListener
) {

    private var handLandmarker: HandLandmarker? = null

    init {
        setupHandLandmarker()
    }

    private fun setupHandLandmarker() {

        try {

            val baseOptions =
                BaseOptions.builder()
                    .setDelegate(Delegate.CPU)
                    .setModelAssetPath(HAND_LANDMARKER_TASK)
                    .build()

            val optionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(
                        DEFAULT_HAND_DETECTION_CONFIDENCE
                    )
                    .setMinTrackingConfidence(
                        DEFAULT_HAND_TRACKING_CONFIDENCE
                    )
                    .setMinHandPresenceConfidence(
                        DEFAULT_HAND_PRESENCE_CONFIDENCE
                    )
                    .setNumHands(DEFAULT_NUM_HANDS)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {

                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }

            handLandmarker =
                HandLandmarker.createFromOptions(
                    context,
                    optionsBuilder.build()
                )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to initialize Hand Landmarker",
                e
            )

            listener.onError(
                e.message ?: "Failed to initialize Hand Landmarker"
            )
        }
    }

    /*
     * This method expects CameraX ImageAnalysis to use:
     *
     * ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
     *
     * This matches the configuration already used by StyleLens.
     */
    fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ) {

        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalStateException(
                "Hand Landmarker must use LIVE_STREAM mode"
            )
        }

        val frameTime =
            SystemClock.uptimeMillis()

        val width =
            imageProxy.width

        val height =
            imageProxy.height

        val rotationDegrees =
            imageProxy.imageInfo.rotationDegrees

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

            processBitmap(
                bitmap = bitmap,
                rotationDegrees = rotationDegrees,
                isFrontCamera = isFrontCamera,
                frameTime = frameTime
            )

        } finally {

            imageProxy.close()
        }
    }

    /*
     * We keep Bitmap processing separate because later the same camera
     * frame can be shared between Face Landmarker and Hand Landmarker
     * without trying to consume one ImageProxy twice.
     */
    fun detectBitmap(
        bitmap: Bitmap,
        rotationDegrees: Int,
        isFrontCamera: Boolean,
        frameTime: Long = SystemClock.uptimeMillis()
    ) {

        processBitmap(
            bitmap = bitmap,
            rotationDegrees = rotationDegrees,
            isFrontCamera = isFrontCamera,
            frameTime = frameTime
        )
    }

    private fun processBitmap(
        bitmap: Bitmap,
        rotationDegrees: Int,
        isFrontCamera: Boolean,
        frameTime: Long
    ) {

        val matrix =
            Matrix().apply {

                postRotate(
                    rotationDegrees.toFloat()
                )

                if (isFrontCamera) {
                    postScale(
                        -1f,
                        1f
                    )
                }
            }

        val rotatedBitmap =
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

        val mpImage: MPImage =
            BitmapImageBuilder(
                rotatedBitmap
            ).build()

        handLandmarker?.detectAsync(
            mpImage,
            frameTime
        )
    }

    private fun returnLivestreamResult(
        result: HandLandmarkerResult,
        input: MPImage
    ) {

        if (result.landmarks().isNotEmpty()) {

            listener.onResults(
                ResultBundle(
                    result = result,
                    inputImageHeight = input.height,
                    inputImageWidth = input.width
                )
            )

        } else {

            listener.onEmpty()
        }
    }

    private fun returnLivestreamError(
        error: RuntimeException
    ) {

        listener.onError(
            error.message
                ?: "Unknown Hand Landmarker error"
        )
    }

    fun clearHandLandmarker() {

        handLandmarker?.close()

        handLandmarker = null
    }

    data class ResultBundle(

        val result: HandLandmarkerResult,

        val inputImageHeight: Int,

        val inputImageWidth: Int
    )

    interface LandmarkerListener {

        fun onResults(
            resultBundle: ResultBundle
        )

        fun onError(
            error: String
        )

        fun onEmpty() {}
    }

    companion object {

        private const val TAG =
            "HandLandmarkerHelper"

        private const val HAND_LANDMARKER_TASK =
            "hand_landmarker.task"

        private const val DEFAULT_HAND_DETECTION_CONFIDENCE =
            0.5F

        private const val DEFAULT_HAND_TRACKING_CONFIDENCE =
            0.5F

        private const val DEFAULT_HAND_PRESENCE_CONFIDENCE =
            0.5F

        private const val DEFAULT_NUM_HANDS =
            1
    }
}