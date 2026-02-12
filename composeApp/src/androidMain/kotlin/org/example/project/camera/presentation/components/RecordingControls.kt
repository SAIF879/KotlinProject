package org.example.project.camera.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.example.project.camera.presentation.theme.SurveillanceColors

/**
 * Professional record/stop button.
 *
 * Idle: white circle inside a ring.
 * Recording: red rounded-square inside a ring.
 */
@Composable
fun RecordingControls(
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val innerColor by animateColorAsState(
        targetValue = if (isRecording) SurveillanceColors.RecRed else SurveillanceColors.White,
        animationSpec = tween(200),
        label = "inner_color",
    )

    val innerCorner by animateDpAsState(
        targetValue = if (isRecording) 8.dp else 32.dp,
        animationSpec = tween(200),
        label = "inner_corner",
    )

    val innerSize by animateDpAsState(
        targetValue = if (isRecording) 28.dp else 56.dp,
        animationSpec = tween(200),
        label = "inner_size",
    )

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = SurveillanceColors.White.copy(alpha = 0.9f),
                shape = CircleShape,
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onToggleRecording,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(RoundedCornerShape(innerCorner))
                .background(innerColor),
        )
    }
}
