package com.stylelens.app.camera

import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.stylelens.app.vision.FaceLandmarkerHelper

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onFaceResult: (
        FaceLandmarkerHelper.ResultBundle
    ) -> Unit
) {

    val context = LocalContext.current

    val faceLandmarkerHelper = remember {

        FaceLandmarkerHelper(
            context = context,
            runningMode = RunningMode.LIVE_STREAM,

            faceLandmarkerHelperListener =
                object : FaceLandmarkerHelper.LandmarkerListener {

                    override fun onError(
                        error: String,
                        errorCode: Int
                    ) {
                        Log.e(
                            "StyleLens",
                            "Face Landmarker error: $error"
                        )
                    }

                    override fun onResults(
                        resultBundle:
                        FaceLandmarkerHelper.ResultBundle
                    ) {

                        Log.d(
                            "StyleLens",
                            "Faces detected: ${
                                resultBundle.result
                                    .faceLandmarks()
                                    .size
                            }"
                        )

                        onFaceResult(resultBundle)
                    }

                    override fun onEmpty() {

                        Log.d(
                            "StyleLens",
                            "No face detected"
                        )
                    }
                }
        )
    }

    DisposableEffect(Unit) {

        onDispose {
            faceLandmarkerHelper.clearFaceLandmarker()
        }
    }

    AndroidView(
        modifier = modifier,

        factory = {

            val previewView =
                PreviewView(context).apply {

                    // Makes coordinate mapping more predictable
                    scaleType =
                        PreviewView.ScaleType.FILL_CENTER
                }

            CameraController.startCamera(
                context = context,
                previewView = previewView,
                faceLandmarkerHelper =
                    faceLandmarkerHelper
            )

            previewView
        }
    )
}