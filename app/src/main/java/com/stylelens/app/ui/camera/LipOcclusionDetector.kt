package com.stylelens.app.ui.camera

import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min

object LipOcclusionDetector {

    fun isHandOverMouth(
        faceResult: FaceLandmarkerResult?,
        handResult: HandLandmarkerResult?
    ): Boolean {

        val faceLandmarks =
            faceResult
                ?.faceLandmarks()
                ?.firstOrNull()
                ?: return false

        val hands =
            handResult
                ?.landmarks()
                ?: return false

        if (hands.isEmpty()) {
            return false
        }

        /*
         * Outer lip landmarks.
         *
         * We use the actual lip contour instead of only landmarks
         * 61 and 291 so the region accounts for lip height too.
         */
        val lipIndices = listOf(
            61, 146, 91, 181, 84,
            17, 314, 405, 321, 375,
            291, 409, 270, 269, 267,
            0, 37, 39, 40, 185
        )

        val lipPoints =
            lipIndices.map {
                faceLandmarks[it]
            }

        var minX =
            lipPoints.minOf { it.x() }

        var maxX =
            lipPoints.maxOf { it.x() }

        var minY =
            lipPoints.minOf { it.y() }

        var maxY =
            lipPoints.maxOf { it.y() }

        /*
         * Add a small margin because a finger can obscure the lips
         * without its landmark center landing exactly inside the
         * tight lip contour.
         */
        val lipWidth =
            maxX - minX

        val lipHeight =
            maxY - minY

        val horizontalMargin =
            max(
                lipWidth * 0.20f,
                0.01f
            )

        val verticalMargin =
            max(
                lipHeight * 0.35f,
                0.01f
            )

        minX =
            max(
                0f,
                minX - horizontalMargin
            )

        maxX =
            min(
                1f,
                maxX + horizontalMargin
            )

        minY =
            max(
                0f,
                minY - verticalMargin
            )

        maxY =
            min(
                1f,
                maxY + verticalMargin
            )

        /*
         * If one or more hand landmarks enter the mouth region,
         * consider the mouth potentially occluded.
         */
        return hands.any { hand ->

            hand.any { point ->

                point.x() in minX..maxX &&
                        point.y() in minY..maxY
            }
        }
    }
}