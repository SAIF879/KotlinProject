package org.example.project.camera.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.camera.presentation.theme.SurveillanceColors

private val statusMessages = listOf(
    "Preparing camera…",
    "Loading modules…",
    "Almost ready…",
)

/**
 * Professional splash screen with clean loading animation.
 */
@Composable
fun SplashScreen(
    onBootComplete: () -> Unit,
) {
    val progress = remember { Animatable(0f) }
    var currentMessageIndex by remember { mutableIntStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    var bootFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
        delay(400)

        for (index in statusMessages.indices) {
            currentMessageIndex = index
            val target = (index + 1).toFloat() / statusMessages.size
            progress.animateTo(target, animationSpec = tween(600))
            delay(400)
        }

        delay(300)
        bootFinished = true
        delay(300)
        onBootComplete()
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (bootFinished) 0f else 1f,
        animationSpec = tween(300),
        label = "fade_out",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurveillanceColors.Background)
            .alpha(contentAlpha),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(500)),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // App icon — clean circle with initial
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SurveillanceColors.Accent,
                                    SurveillanceColors.AccentDim,
                                ),
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "S",
                        color = SurveillanceColors.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sentinel",
                    color = SurveillanceColors.TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Security Monitor",
                    color = SurveillanceColors.TextTertiary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp,
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Progress
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = SurveillanceColors.Accent,
                    trackColor = SurveillanceColors.SurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = statusMessages[currentMessageIndex],
                    color = SurveillanceColors.TextTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }

        // Version — bottom
        Text(
            text = "v1.0.0",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            color = SurveillanceColors.TextTertiary.copy(alpha = 0.4f),
            fontSize = 11.sp,
        )
    }
}
