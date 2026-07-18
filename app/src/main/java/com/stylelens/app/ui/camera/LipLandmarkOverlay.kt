package com.stylelens.app.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

@Composable
fun LipLandmarkOverlay(
    result: FaceLandmarkerResult?,
    inputImageWidth: Int,
    inputImageHeight: Int,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        val landmarks =
            result
                ?.faceLandmarks()
                ?.firstOrNull()
                ?: return@Canvas

        /*
         * Ordered MediaPipe landmarks forming the
         * OUTER boundary of the lips.
         */
        val outerLip = listOf(
            61,
            146,
            91,
            181,
            84,
            17,
            314,
            405,
            321,
            375,
            291,
            409,
            270,
            269,
            267,
            0,
            37,
            39,
            40,
            185
        )

        /*
         * Ordered landmarks forming the mouth opening.
         */
        val innerLip = listOf(
            78,
            95,
            88,
            178,
            87,
            14,
            317,
            402,
            318,
            324,
            308,
            415,
            310,
            311,
            312,
            13,
            82,
            81,
            80,
            191
        )

        fun point(index: Int): Offset {

            val landmark = landmarks[index]

            if (inputImageWidth <= 0 || inputImageHeight <= 0) {
                return Offset.Zero
            }

            val imageWidth = inputImageWidth.toFloat()
            val imageHeight = inputImageHeight.toFloat()

            // PreviewView uses FILL_CENTER, so the image is scaled
            // until it completely fills the Canvas.
            val scale = maxOf(
                size.width / imageWidth,
                size.height / imageHeight
            )

            val scaledWidth = imageWidth * scale
            val scaledHeight = imageHeight * scale

            // Amount cropped equally from both sides / top-bottom.
            val offsetX =
                (scaledWidth - size.width) / 2f

            val offsetY =
                (scaledHeight - size.height) / 2f

            return Offset(
                x = landmark.x() * scaledWidth - offsetX,
                y = landmark.y() * scaledHeight - offsetY
            )
        }

        // -------------------------
        // OUTER LIP PATH
        // -------------------------

        val outerPath = Path()

        val firstOuter = point(outerLip.first())

        outerPath.moveTo(
            firstOuter.x,
            firstOuter.y
        )

        outerLip
            .drop(1)
            .forEach { index ->

                val p = point(index)

                outerPath.lineTo(
                    p.x,
                    p.y
                )
            }

        outerPath.close()

        // -------------------------
        // INNER LIP PATH
        // -------------------------

        val innerPath = Path()

        val firstInner = point(innerLip.first())

        innerPath.moveTo(
            firstInner.x,
            firstInner.y
        )

        innerLip
            .drop(1)
            .forEach { index ->

                val p = point(index)

                innerPath.lineTo(
                    p.x,
                    p.y
                )
            }

        innerPath.close()

        // Draw temporary diagnostic outlines.

        drawPath(
            path = outerPath,
            color = Color.Green,
            style = Stroke(
                width = 4f
            )
        )

        drawPath(
            path = innerPath,
            color = Color.Yellow,
            style = Stroke(
                width = 3f
            )
        )
    }
}