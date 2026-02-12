package org.example.project.camera.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.camera.presentation.theme.SurveillanceColors

/**
 * Clean recording indicator: red dot + formatted time.
 * Hidden when not recording.
 */
@Composable
fun RecordingTimer(
    isRecording: Boolean,
    formattedTime: String,
    modifier: Modifier = Modifier,
) {
    if (!isRecording) return

    val infiniteTransition = rememberInfiniteTransition(label = "rec_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot_alpha",
    )

    Row(
        modifier = modifier
            .background(
                color = SurveillanceColors.Background.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(
                color = SurveillanceColors.RecRed.copy(alpha = dotAlpha),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formattedTime,
            color = SurveillanceColors.TextPrimary,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
        )
    }
}
