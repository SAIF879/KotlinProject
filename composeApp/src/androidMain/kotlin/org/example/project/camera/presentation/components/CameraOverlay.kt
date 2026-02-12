package org.example.project.camera.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.camera.presentation.theme.SurveillanceColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clean status bar overlay for the camera feed.
 *
 * Shows camera label, timestamp, and recording status in a
 * minimal, professional layout.
 */
@Composable
fun CameraOverlay(
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf(getCurrentTimestamp()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTimestamp()
            delay(1_000L)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Top bar — camera info
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .background(SurveillanceColors.Background.copy(alpha = 0.4f))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Camera label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = SurveillanceColors.Accent,
                            shape = RoundedCornerShape(3.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = if (isFrontCamera) "FRONT" else "REAR",
                        color = SurveillanceColors.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Camera 01",
                    color = SurveillanceColors.TextPrimary.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Timestamp
            Text(
                text = currentTime,
                color = SurveillanceColors.TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
