package com.stylelens.app.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            .size(wheelSize),
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

            val segmentAngle =
                360f / shades.size

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
                            segmentAngle

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
                        (
                                360f /
                                        shades.size
                                )

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