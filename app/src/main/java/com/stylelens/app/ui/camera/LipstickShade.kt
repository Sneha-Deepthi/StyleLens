package com.stylelens.app.ui.camera

import androidx.compose.ui.graphics.Color

data class LipstickShade(
    val name: String,
    val color: Color,
    val intensity: Float
)

val lipstickShades = listOf(
    LipstickShade(
        name = "Nude",
        color = Color(0xFFB76E79),
        intensity = 0.22f
    ),
    LipstickShade(
        name = "Rose",
        color = Color(0xFFD94A64),
        intensity = 0.30f
    ),
    LipstickShade(
        name = "Coral",
        color = Color(0xFFE76F61),
        intensity = 0.28f
    ),
    LipstickShade(
        name = "Red",
        color = Color(0xFFC62828),
        intensity = 0.32f
    ),
    LipstickShade(
        name = "Berry",
        color = Color(0xFF8E3557),
        intensity = 0.27f
    ),
    LipstickShade(
        name = "Wine",
        color = Color(0xFF722F37),
        intensity = 0.25f
    )
)