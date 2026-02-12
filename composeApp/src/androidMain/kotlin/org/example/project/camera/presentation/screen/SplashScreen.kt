package org.example.project.camera.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.camera.presentation.theme.SurveillanceColors

private val bootMessages = listOf(
    "Initializing system...",
    "Loading camera modules...",
    "Calibrating sensors...",
    "Establishing secure feed...",
    "System ready.",
)

/**
 * Surveillance system boot-up splash screen.
 *
 * Plays a sequential boot animation with typing effects, a progress bar,
 * and scan-line visuals, then calls [onBootComplete] to navigate away.
 */
@Composable
fun SplashScreen(
    onBootComplete: () -> Unit,
) {
    val progress = remember { Animatable(0f) }
    var currentMessageIndex by remember { mutableIntStateOf(-1) }
    var displayedText by remember { mutableStateOf("") }
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var bootFinished by remember { mutableStateOf(false) }

    // Scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan_y",
    )

    // Boot sequence orchestration
    LaunchedEffect(Unit) {
        delay(300)
        showLogo = true
        delay(600)
        showTitle = true
        delay(500)

        for ((index, message) in bootMessages.withIndex()) {
            currentMessageIndex = index
            displayedText = ""

            // Typing effect
            for (char in message) {
                displayedText += char
                delay(30)
            }

            // Animate progress
            val targetProgress = (index + 1).toFloat() / bootMessages.size
            progress.animateTo(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 400),
            )

            delay(300)
        }

        delay(500)
        bootFinished = true
        delay(400)
        onBootComplete()
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (bootFinished) 0f else 1f,
        animationSpec = tween(400),
        label = "fade_out",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurveillanceColors.Background)
            .alpha(contentAlpha),
    ) {
        // Scan line effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height * scanLineY
            drawLine(
                color = SurveillanceColors.NeonGreen.copy(alpha = 0.08f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 60f,
            )
            // Subtle horizontal grid lines
            val step = size.height / 30
            for (i in 0..30) {
                drawLine(
                    color = SurveillanceColors.NeonGreen.copy(alpha = 0.03f),
                    start = Offset(0f, i * step),
                    end = Offset(size.width, i * step),
                    strokeWidth = 1f,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo — surveillance eye icon
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(tween(600)),
            ) {
                SurveillanceIcon()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(500)),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SENTINEL",
                        color = SurveillanceColors.NeonGreen,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 8.sp,
                    )
                    Text(
                        text = "SURVEILLANCE SYSTEM",
                        color = SurveillanceColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Progress bar
            if (currentMessageIndex >= 0) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = SurveillanceColors.NeonGreen,
                    trackColor = SurveillanceColors.SurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Boot message with cursor blink
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "> ",
                        color = SurveillanceColors.NeonGreen,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = displayedText,
                        color = SurveillanceColors.TextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                    )
                    BlinkingCursor()
                }
            }
        }

        // Version tag — bottom
        Text(
            text = "v1.0.0 // CLASSIFIED",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            color = SurveillanceColors.TextSecondary.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun SurveillanceIcon() {
    Canvas(modifier = Modifier.size(80.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.5f

        // Outer ring
        drawCircle(
            color = SurveillanceColors.NeonGreen,
            radius = radius,
            center = center,
            style = Stroke(width = 3f),
        )

        // Inner ring
        drawCircle(
            color = SurveillanceColors.NeonGreen.copy(alpha = 0.6f),
            radius = radius * 0.6f,
            center = center,
            style = Stroke(width = 2f),
        )

        // Center dot (pupil)
        drawCircle(
            color = SurveillanceColors.NeonGreen,
            radius = radius * 0.2f,
            center = center,
        )

        // Cross-hairs
        val armLength = radius * 1.3f
        val lineColor = SurveillanceColors.NeonGreen.copy(alpha = 0.4f)
        // Top
        drawLine(lineColor, Offset(center.x, center.y - radius - 8), Offset(center.x, center.y - armLength), strokeWidth = 2f)
        // Bottom
        drawLine(lineColor, Offset(center.x, center.y + radius + 8), Offset(center.x, center.y + armLength), strokeWidth = 2f)
        // Left
        drawLine(lineColor, Offset(center.x - radius - 8, center.y), Offset(center.x - armLength, center.y), strokeWidth = 2f)
        // Right
        drawLine(lineColor, Offset(center.x + radius + 8, center.y), Offset(center.x + armLength, center.y), strokeWidth = 2f)
    }
}

@Composable
private fun BlinkingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursor_alpha",
    )

    Text(
        text = "█",
        color = SurveillanceColors.NeonGreen.copy(alpha = alpha),
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
    )
}
