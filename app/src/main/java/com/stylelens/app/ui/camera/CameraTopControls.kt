package com.stylelens.app.ui.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CameraTopControls(
    onCloseClick: () -> Unit,
    onFlipCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Close button

        CircularCameraButton(
            text = "×",
            onClick = onCloseClick
        )

        // Flip-camera placeholder

        CircularCameraButton(
            text = "↻",
            onClick = onFlipCameraClick
        )
    }
}

@Composable
private fun CircularCameraButton(
    text: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = Color.Black.copy(
                    alpha = 0.35f
                ),
                shape = CircleShape
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium
        )
    }
}