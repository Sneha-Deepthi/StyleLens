package com.stylelens.app.ui.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShadeSelector(
    shades: List<LipstickShade>,
    selectedShade: LipstickShade,
    onShadeSelected: (LipstickShade) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .padding(
                horizontal = 16.dp,
                vertical = 20.dp
            ),
        horizontalArrangement =
            Arrangement.spacedBy(14.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        shades.forEach { shade ->

            val isSelected =
                shade == selectedShade

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    onShadeSelected(shade)
                }
            ) {

                Spacer(
                    modifier = Modifier
                        .size(
                            if (isSelected)
                                48.dp
                            else
                                44.dp
                        )
                        .background(
                            color = shade.color,
                            shape = CircleShape
                        )
                        .then(
                            if (isSelected) {

                                Modifier.border(
                                    width = 3.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )

                            } else {

                                Modifier
                            }
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text = shade.name,
                    color = Color.White
                )
            }
        }
    }
}