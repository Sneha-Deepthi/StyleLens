package com.stylelens.app.ui.camera

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

@Composable
fun ShadeSelector(
    shades: List<LipstickShade>,
    selectedShade: LipstickShade,
    onShadeSelected: (LipstickShade) -> Unit,
    modifier: Modifier = Modifier
) {

    if (shades.isEmpty()) {
        return
    }

    val selectedIndex =
        shades.indexOf(selectedShade)
            .coerceAtLeast(0)

    val segmentAngle =
        360f / shades.size

    var dragRotation by remember {
        mutableFloatStateOf(0f)
    }

    var targetRotation by remember {
        mutableFloatStateOf(0f)
    }

    val animatedRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = 420f
        ),
        label = "WheelRotation"
    )

    /*
     * Diameter of the complete wheel.
     *
     * A large portion of this wheel will later
     * be moved below the bottom of the screen.
     */
    val wheelSize = 340.dp

    /*
     * Radius at which shade controls sit.
     */
    val shadeRadius = 128.dp

    Box(
        modifier = modifier
            .size(wheelSize)
            .pointerInput(
                selectedShade,
                shades
            ) {

                detectDragGestures(

                    onDrag = { change, dragAmount ->

                        change.consume()

                        /*
                         * Horizontal movement controls
                         * the temporary wheel rotation.
                         */
                        dragRotation +=
                            dragAmount.x * 0.25f
                    },

                    onDragEnd = {

                        /*
                         * Determine how many shade
                         * positions the user moved.
                         */
                        val steps =
                            (
                                    dragRotation /
                                            segmentAngle
                                    )
                                .roundToInt()

                        if (steps != 0) {

                            val newIndex =
                                (
                                        selectedIndex -
                                                steps
                                        )
                                    .mod(shades.size)

                            onShadeSelected(
                                shades[newIndex]
                            )
                        }

                        /*
                         * Selected shade becomes the new
                         * top-center position.
                         */
                        targetRotation = 0f
                        dragRotation = 0f
                    },

                    onDragCancel = {
                        dragRotation = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        // ---------------------------------
        // TRANSLUCENT SEGMENTED RING
        // ---------------------------------

        Canvas(
            modifier =
                Modifier.size(wheelSize)
        ) {

            val strokeWidth =
                72.dp.toPx()

            val padding =
                strokeWidth / 2f + 4.dp.toPx()

            val arcSize = Size(
                width =
                    size.width -
                            padding * 2f,

                height =
                    size.height -
                            padding * 2f
            )

            val topLeft = Offset(
                x = padding,
                y = padding
            )

            /*
             * Draw one translucent segment
             * for every lipstick shade.
             */
            shades.forEachIndexed { index, shade ->

                var relativeIndex =
                    index - selectedIndex

                if (
                    relativeIndex >
                    shades.size / 2
                ) {

                    relativeIndex -=
                        shades.size
                }

                if (
                    relativeIndex <
                    -shades.size / 2
                ) {

                    relativeIndex +=
                        shades.size
                }

                /*
                 * Selected shade is centered
                 * at 12 o'clock.
                 */
                val centerAngle =
                    -90f +
                            relativeIndex *
                            segmentAngle +
                            dragRotation +
                            animatedRotation

                val startAngle =
                    centerAngle -
                            segmentAngle / 2f +
                            1.5f

                val sweepAngle =
                    segmentAngle -
                            3f

                val isSelected =
                    shade == selectedShade

                drawArc(
                    color =
                        if (isSelected) {

                            shade.color.copy(
                                alpha = 0.88f
                            )

                        } else {

                            shade.color.copy(
                                alpha = 0.42f
                            )
                        },

                    startAngle =
                        startAngle,

                    sweepAngle =
                        sweepAngle,

                    useCenter =
                        false,

                    topLeft =
                        topLeft,

                    size =
                        arcSize,

                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                )
            }
        }

        // ---------------------------------
        // SHADE LABELS / TOUCH TARGETS
        // ---------------------------------

        shades.forEachIndexed { index, shade ->

            var relativeIndex =
                index - selectedIndex

            if (
                relativeIndex >
                shades.size / 2
            ) {

                relativeIndex -=
                    shades.size
            }

            if (
                relativeIndex <
                -shades.size / 2
            ) {

                relativeIndex +=
                    shades.size
            }

            val angle =
                -90f +
                        relativeIndex *
                        segmentAngle +
                        dragRotation +
                        animatedRotation

            val radians =
                Math.toRadians(
                    angle.toDouble()
                )

            val x =
                shadeRadius.value *
                        cos(radians)
                            .toFloat()

            val y =
                shadeRadius.value *
                        sin(radians)
                            .toFloat()

            val isSelected =
                shade == selectedShade

            Box(
                modifier = Modifier
                    .offset(
                        x = x.dp,
                        y = y.dp
                    )
                    .size(
                        width = 72.dp,
                        height = 54.dp
                    )
                    .clickable {

                        onShadeSelected(
                            shade
                        )
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = shade.name,

                    color =
                        Color.White.copy(
                            alpha =
                                if (isSelected)
                                    1f
                                else
                                    0.72f
                        ),

                    fontSize =
                        if (isSelected)
                            14.sp
                        else
                            11.sp,

                    fontWeight =
                        if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                )
            }
        }

    }
}