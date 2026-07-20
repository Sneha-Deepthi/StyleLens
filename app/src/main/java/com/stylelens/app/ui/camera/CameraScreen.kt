package com.stylelens.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.stylelens.app.camera.CameraPreview
import com.stylelens.app.vision.FaceLandmarkerHelper
import com.stylelens.app.vision.HandLandmarkerHelper

@Composable
fun CameraScreen() {

    val context = LocalContext.current

    var hasCameraPermission by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var handResult by remember {
        mutableStateOf<
                HandLandmarkerHelper.ResultBundle?
                >(null)
    }

    var faceResult by remember {
        mutableStateOf<
                FaceLandmarkerHelper.ResultBundle?
                >(null)
    }

    var selectedShade by remember {

        mutableStateOf(
            lipstickShades[1]
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    val isLipOccluded =
        LipOcclusionDetector.isHandOverMouth(
            faceResult =
                faceResult?.result,

            handResult =
                handResult?.result
        )

    if (hasCameraPermission) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            CameraPreview(
                modifier = Modifier.fillMaxSize(),

                onFaceResult = { result ->
                    faceResult = result
                },

                onFaceLost = {
                    faceResult = null
                },

                onHandResult = { result ->
                    handResult = result
                },

                onHandLost = {
                    handResult = null
                }
            )

            LipLandmarkOverlay(
                result =
                    if (isLipOccluded) {
                        null
                    } else {
                        faceResult?.result
                    },

                inputImageWidth =
                    faceResult?.inputImageWidth ?: 0,

                inputImageHeight =
                    faceResult?.inputImageHeight ?: 0,

                lipstickColor =
                    selectedShade.color,

                intensity =
                    selectedShade.intensity,

                modifier = Modifier.fillMaxSize()
            )

            ShadeSelector(
                shades = lipstickShades,

                selectedShade =
                    selectedShade,

                onShadeSelected = { shade ->
                    selectedShade = shade
                },

                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
            )
        }

    } else {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Button(
                onClick = {

                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            ) {

                Text("Allow Camera")
            }
        }
    }
}