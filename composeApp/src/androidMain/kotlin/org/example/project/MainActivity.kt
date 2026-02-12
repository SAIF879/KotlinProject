package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.camera.presentation.screen.CameraScreen
import org.example.project.camera.presentation.screen.SplashScreen
import org.example.project.camera.presentation.theme.SurveillanceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SurveillanceTheme {
                var showSplash by remember { mutableStateOf(true) }

                AnimatedContent(
                    targetState = showSplash,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "screen_transition",
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(
                            onBootComplete = { showSplash = false }
                        )
                    } else {
                        CameraScreen()
                    }
                }
            }
        }
    }
}