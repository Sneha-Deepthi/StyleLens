package com.stylelens.app.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
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

        fun createSmoothPath(indices: List<Int>): Path {

            val points = indices.map { index ->
                point(index)
            }

            val path = Path()

            if (points.size < 2) {
                return path
            }

            // Start at the midpoint between the last and first points
            val firstMidX =
                (points.last().x + points.first().x) / 2f

            val firstMidY =
                (points.last().y + points.first().y) / 2f

            path.moveTo(
                firstMidX,
                firstMidY
            )

            for (i in points.indices) {

                val current = points[i]

                val next =
                    points[(i + 1) % points.size]

                val midX =
                    (current.x + next.x) / 2f

                val midY =
                    (current.y + next.y) / 2f

                path.quadraticTo(
                    current.x,
                    current.y,
                    midX,
                    midY
                )
            }

            path.close()

            return path
        }

        // -------------------------
        // OUTER LIP PATH
        // -------------------------

        val outerPath =
            createSmoothPath(outerLip)


        // -------------------------
        // INNER LIP PATH
        // -------------------------


        val innerPath =
            createSmoothPath(innerLip)


        // Draw temporary diagnostic outlines.

        // Combine outer and inner contours into one lip shape.
// EvenOdd means:
// - inside outer contour = filled
// - inside inner contour = cut out
        val lipstickPath = Path().apply {

            fillType = PathFillType.EvenOdd

            addPath(outerPath)
            addPath(innerPath)
        }

// Temporary lipstick shade.
// Semi-transparent so the natural lip texture remains visible.
        drawPath(
            path = lipstickPath,
            color = Color(0xFFD94A64).copy(alpha = 0.45f)
        )
    }
}