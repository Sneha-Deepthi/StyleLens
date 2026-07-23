package com.stylelens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stylelens.app.ui.camera.CameraScreen
import com.stylelens.app.ui.splash.SplashScreen
import com.stylelens.app.ui.theme.StyleLensTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StyleLensTheme {
                CameraScreen()
//                SplashScreen()
            }
        }
    }
}