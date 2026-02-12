package org.example.project.camera.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.camera.presentation.theme.SurveillanceColors

/**
 * Tactical-styled recording controls.
 *
 * Idle: green outer ring + green inner circle — "START REC" label.
 * Recording: pulsing red outer ring + red inner square — "STOP" label.
 */
@Composable
fun RecordingControls(
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rec_ring")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_alpha",
    )

    val ringColor = if (isRecording) {
        SurveillanceColors.RecRed.copy(alpha = pulseAlpha)
    } else {
        SurveillanceColors.NeonGreen
    }

    val innerColor = if (isRecording) {
        SurveillanceColors.RecRed
    } else {
        SurveillanceColors.NeonGreen
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onToggleRecording,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2

                // Outer ring
                drawCircle(
                    color = ringColor,
                    radius = radius - 2f,
                    center = center,
                    style = Stroke(width = 3f),
                )

                // Tick marks at cardinal positions
                val tickLen = 8f
                val tickColor = ringColor.copy(alpha = 0.6f)
                // Top
                drawLine(tickColor, Offset(center.x, 0f), Offset(center.x, tickLen), 2f, StrokeCap.Square)
                // Bottom
                drawLine(tickColor, Offset(center.x, size.height), Offset(center.x, size.height - tickLen), 2f, StrokeCap.Square)
                // Left
                drawLine(tickColor, Offset(0f, center.y), Offset(tickLen, center.y), 2f, StrokeCap.Square)
                // Right
                drawLine(tickColor, Offset(size.width, center.y), Offset(size.width - tickLen, center.y), 2f, StrokeCap.Square)

                if (isRecording) {
                    // Inner square (stop)
                    val squareHalf = radius * 0.3f
                    drawRect(
                        color = innerColor,
                        topLeft = Offset(center.x - squareHalf, center.y - squareHalf),
                        size = androidx.compose.ui.geometry.Size(squareHalf * 2, squareHalf * 2),
                    )
                } else {
                    // Inner circle (record)
                    drawCircle(
                        color = innerColor,
                        radius = radius * 0.35f,
                        center = center,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isRecording) "[ STOP ]" else "[ START REC ]",
            color = if (isRecording) SurveillanceColors.RecRed else SurveillanceColors.NeonGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
    }
}
