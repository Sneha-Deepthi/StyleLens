package com.stylelens.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clipToBounds

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

    val occlusionStabilizer =
        remember {
            OcclusionStabilizer(
                framesToHide = 2,
                framesToShow = 4
            )
        }

    var isLipOccluded by remember {
        mutableStateOf(false)
    }

    if (hasCameraPermission) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .clipToBounds()
        ) {

            CameraPreview(
                modifier = Modifier.fillMaxSize(),

                onFaceResult = { result ->
                    faceResult = result
                },

                onFaceLost = {

                    faceResult = null
                    handResult = null

                    occlusionStabilizer.reset()

                    isLipOccluded = false
                },

                onHandResult = { result ->

                    handResult = result

                    val rawOccluded =
                        LipOcclusionDetector.isHandOverMouth(
                            faceResult = faceResult?.result,
                            handResult = result.result
                        )

                    isLipOccluded =
                        occlusionStabilizer.update(
                            rawOccluded
                        )
                },

                onHandLost = {

                    handResult = null

                    isLipOccluded =
                        occlusionStabilizer.update(
                            false
                        )
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

                selectedShade = selectedShade,

                onShadeSelected = { shade ->
                    selectedShade = shade
                },

                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .offset(
                        y = 135.dp
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