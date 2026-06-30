package com.stylelens.app.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BackgroundColor = Color(0xFF0F0F12)
private val AccentColor = Color(0xFFD6336C)

@Composable
fun SplashScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "S",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 96.sp
                ),
                color = AccentColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Style",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )

                Text(
                    text = "Lens",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AccentColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "See It Before You Wear It",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFBDBDBD)
            )

            Spacer(modifier = Modifier.height(90.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Preparing...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    modifier = Modifier.width(220.dp),
                    color = AccentColor,
                    trackColor = Color.DarkGray
                )
            }
        }
    }
}